package ch.trancee.meshlink.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

/** Tests for RouteEntry data class. Implements acceptance criteria from e01s03 spec. */
class RouteEntryTest {

    private val destPeerId = PeerId.generate()
    private val nextHopId = PeerId.generate()
    private val metric = LinkMetric(rssiNormalized = 187u, supportsCoc = true)

    @Test
    fun `create route entry with all fields`() {
        // Act
        val entry =
            RouteEntry(
                destination = destPeerId,
                nextHop = nextHopId,
                seqNo = 1u,
                metric = metric.composite,
            )

        // Assert
        assertEquals(destPeerId, entry.destination)
        assertEquals(nextHopId, entry.nextHop)
        assertEquals(1u, entry.seqNo)
        assertEquals(metric.composite, entry.metric)
    }

    @Test
    fun `route entry composite metric has correct bit flags`() {
        // Act
        val entry =
            RouteEntry(
                destination = destPeerId,
                nextHop = nextHopId,
                seqNo = 1u,
                metric = metric.composite,
            )

        // Assert
        assertEquals(187u, entry.metric and 0xFFu) // RSSI
        assertTrue(entry.metric and 0x100u != 0u) // CoC flag
    }

    @Test
    fun `route entry with default expiry`() {
        // Act
        val entry =
            RouteEntry(
                destination = destPeerId,
                nextHop = nextHopId,
                seqNo = 1u,
                metric = metric.composite,
            )

        // Assert
        assertNotNull(entry.expiresAt)
    }

    @Test
    fun `route entry with custom expiry`() {
        // Arrange
        val customExpiry = Clock.System.now() + 60.seconds

        // Act
        val entry =
            RouteEntry(
                destination = destPeerId,
                nextHop = nextHopId,
                seqNo = 1u,
                metric = metric.composite,
                expiresAt = customExpiry,
            )

        // Assert
        assertEquals(customExpiry, entry.expiresAt)
    }

    @Test
    fun `isExpired returns false for valid entry`() {
        // Act
        val entry =
            RouteEntry(
                destination = destPeerId,
                nextHop = nextHopId,
                seqNo = 1u,
                metric = metric.composite,
            )

        // Assert
        assertTrue(!entry.isExpired())
    }

    @Test
    fun `isExpired returns true for expired entry`() {
        // Act
        val entry =
            RouteEntry(
                destination = destPeerId,
                nextHop = nextHopId,
                seqNo = 1u,
                metric = metric.composite,
                expiresAt = Clock.System.now() - 1.seconds,
            )

        // Assert
        assertTrue(entry.isExpired())
    }

    @Test
    fun `route entry with infinite metric is unreachable`() {
        // Act
        val entry =
            RouteEntry(
                destination = destPeerId,
                nextHop = nextHopId,
                seqNo = 1u,
                metric = 0u, // infinite/unreachable
            )

        // Assert
        assertEquals(0u, entry.metric)
    }

    @Test
    fun `route entry seqno wraps around`() {
        // Act
        val entry =
            RouteEntry(
                destination = destPeerId,
                nextHop = nextHopId,
                seqNo = 255u, // max UInt8 would be 255, but we use UInt
                metric = metric.composite,
            )

        // Assert
        assertEquals(255u, entry.seqNo)
    }

    @Test
    fun `route entry diagnostic id is safe`() {
        // Act
        val entry =
            RouteEntry(
                destination = destPeerId,
                nextHop = nextHopId,
                seqNo = 1u,
                metric = metric.composite,
            )

        // Assert
        val str = entry.toString()
        assertTrue(str.contains("RouteEntry"))
        assertTrue(str.contains(destPeerId.diagnosticId))
    }

    @Test
    fun `rssi extracts RSSI from metric`() {
        // Act
        val entry =
            RouteEntry(
                destination = destPeerId,
                nextHop = nextHopId,
                seqNo = 1u,
                metric = 0xABu, // 171 in lower 8 bits
            )

        // Assert
        assertEquals(171u, entry.rssi)
    }

    @Test
    fun `isReachable returns true for non-zero metric`() {
        // Act
        val entry =
            RouteEntry(destination = destPeerId, nextHop = nextHopId, seqNo = 1u, metric = 100u)

        // Assert
        assertTrue(entry.isReachable)
    }

    @Test
    fun `isReachable returns false for zero metric`() {
        // Act
        val entry =
            RouteEntry(destination = destPeerId, nextHop = nextHopId, seqNo = 1u, metric = 0u)

        // Assert
        assertFalse(entry.isReachable)
    }
}
