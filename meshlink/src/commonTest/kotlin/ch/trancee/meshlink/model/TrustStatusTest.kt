package ch.trancee.meshlink.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Tests for TrustStatus enum. Implements acceptance criteria from e01s02 spec. */
class TrustStatusTest {

    @Test
    fun `PINNED is the initial trust status`() {
        // Act & Assert
        assertTrue(TrustStatus.PINNED.isInitial)
    }

    @Test
    fun `VERIFIED indicates successful verification`() {
        // Act & Assert
        assertTrue(TrustStatus.VERIFIED.isVerified)
    }

    @Test
    fun `REVOKED indicates trust is revoked`() {
        // Act & Assert
        assertTrue(TrustStatus.REVOKED.isRevoked)
    }

    @Test
    fun `trust status has correct ordinal values`() {
        // Assert - order matters for state machine
        assertEquals(0, TrustStatus.PINNED.ordinal)
        assertEquals(1, TrustStatus.VERIFIED.ordinal)
        assertEquals(2, TrustStatus.REVOKED.ordinal)
    }

    @Test
    fun `trust status can transition to VERIFIED from PINNED`() {
        // Act
        val next = TrustStatus.PINNED.transitionTo(TrustStatus.VERIFIED)

        // Assert
        assertEquals(TrustStatus.VERIFIED, next)
    }

    @Test
    fun `trust status can transition to REVOKED from any state`() {
        // Act & Assert
        assertEquals(TrustStatus.REVOKED, TrustStatus.PINNED.transitionTo(TrustStatus.REVOKED))
        assertEquals(TrustStatus.REVOKED, TrustStatus.VERIFIED.transitionTo(TrustStatus.REVOKED))
        assertEquals(TrustStatus.REVOKED, TrustStatus.REVOKED.transitionTo(TrustStatus.REVOKED))
    }

    @Test
    fun `trust status cannot transition backwards from REVOKED`() {
        // Act & Assert - REVOKED is terminal state
        val result = TrustStatus.REVOKED.transitionTo(TrustStatus.PINNED)
        assertEquals(TrustStatus.REVOKED, result)
    }

    @Test
    fun `trust status can remain PINNED (idempotent)`() {
        // Act
        val next = TrustStatus.PINNED.transitionTo(TrustStatus.PINNED)

        // Assert
        assertEquals(TrustStatus.PINNED, next)
    }

    @Test
    fun `trust status can remain VERIFIED (idempotent)`() {
        // Act
        val next = TrustStatus.VERIFIED.transitionTo(TrustStatus.VERIFIED)

        // Assert
        assertEquals(TrustStatus.VERIFIED, next)
    }

    @Test
    fun `VERIFIED can attempt to transition to PINNED but gets VERIFIED`() {
        // Act
        val next = TrustStatus.VERIFIED.transitionTo(TrustStatus.PINNED)

        // Assert - cannot go back to PINNED
        assertEquals(TrustStatus.VERIFIED, next)
    }
}
