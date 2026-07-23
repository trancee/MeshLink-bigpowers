package ch.trancee.meshlink.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Tests for LinkMetric data class. Implements acceptance criteria from e01s03 spec. */
class LinkMetricTest {

    @Test
    fun `create link metric with RSSI and flags`() {
        // Arrange
        val rssiNormalized = 187u

        // Act
        val metric =
            LinkMetric(
                rssiNormalized = rssiNormalized,
                supportsCoc = true,
                fastInterval = true,
                highPowerTier = false,
            )

        // Assert
        assertEquals(rssiNormalized, metric.rssiNormalized)
        assertTrue(metric.supportsCoc)
        assertTrue(metric.fastInterval)
        assertFalse(metric.highPowerTier)
    }

    @Test
    fun `composite metric combines RSSI with flags`() {
        // Arrange
        val rssiNormalized = 187u

        // Act
        val metric =
            LinkMetric(
                rssiNormalized = rssiNormalized,
                supportsCoc = true,
                fastInterval = false,
                highPowerTier = false,
            )

        // Assert - composite should include RSSI and CoC flag
        // RSSI is in bits 0-7, CoC flag is in bit 8
        assertEquals(187u, metric.composite and 0xFFu)
        assertEquals(256u, metric.composite and 0x100u) // CoC flag
    }

    @Test
    fun `reject invalid RSSI range at construction`() {
        // Arrange
        val invalidRssi = 256u // RSSI normalized is 0-255

        // Act & Assert
        assertFailsWith<IllegalArgumentException> { LinkMetric(rssiNormalized = invalidRssi) }
    }

    @Test
    fun `RSSI zero is valid`() {
        // Act
        val metric = LinkMetric(rssiNormalized = 0u)

        // Assert
        assertEquals(0u, metric.rssiNormalized)
    }

    @Test
    fun `RSSI max is valid`() {
        // Act
        val metric = LinkMetric(rssiNormalized = 255u)

        // Assert
        assertEquals(255u, metric.rssiNormalized)
    }

    @Test
    fun `all flags set produces correct composite`() {
        // Act
        val metric =
            LinkMetric(
                rssiNormalized = 100u,
                supportsCoc = true,
                fastInterval = true,
                highPowerTier = true,
            )

        // Assert - bits 0-7: RSSI, bit 8: CoC, bit 9: Fast, bit 10: HighPower
        val expected = 100u or (1u shl 8) or (1u shl 9) or (1u shl 10)
        assertEquals(expected, metric.composite)
    }

    @Test
    fun `no flags set produces just RSSI in composite`() {
        // Act
        val metric =
            LinkMetric(
                rssiNormalized = 200u,
                supportsCoc = false,
                fastInterval = false,
                highPowerTier = false,
            )

        // Assert
        assertEquals(200u, metric.composite)
    }

    @Test
    fun `isReachable returns true for non-zero RSSI`() {
        // Act
        val metric = LinkMetric(rssiNormalized = 100u)

        // Assert
        assertTrue(metric.isReachable)
    }

    @Test
    fun `isReachable returns false for zero RSSI`() {
        // Act
        val metric = LinkMetric(rssiNormalized = 0u)

        // Assert
        assertFalse(metric.isReachable)
    }

    @Test
    fun `toString returns safe representation`() {
        // Act
        val metric = LinkMetric(rssiNormalized = 150u, supportsCoc = true)
        val str = metric.toString()

        // Assert
        assertTrue(str.contains("LinkMetric"))
        assertTrue(str.contains("rssi=150"))
    }
}
