package ch.trancee.meshlink.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Tests for TrustRecord data class. Implements acceptance criteria from e01s02 spec. */
class TrustRecordTest {

    private val peerId = PeerId.generate()
    private val publicKey = CryptoKey.fromBytes(ByteArray(32) { it.toByte() })

    @Test
    fun `create trust record with PINNED status`() {
        // Act
        val record = TrustRecord(peerId, publicKey, TrustStatus.PINNED)

        // Assert
        assertEquals(peerId, record.peerId)
        assertEquals(publicKey, record.publicKey)
        assertEquals(TrustStatus.PINNED, record.status)
        assertNotNull(record.createdAt)
        assertNotNull(record.updatedAt)
    }

    @Test
    fun `trust record with old key for rotation`() {
        // Arrange
        val oldKey = CryptoKey.fromBytes(ByteArray(32) { 0x01 })
        val newKey = CryptoKey.fromBytes(ByteArray(32) { 0x02 })

        // Act
        val record =
            TrustRecord(
                peerId = peerId,
                publicKey = newKey,
                status = TrustStatus.VERIFIED,
                previousKey = oldKey,
            )

        // Assert
        assertEquals(newKey, record.publicKey)
        assertEquals(oldKey, record.previousKey)
    }

    @Test
    fun `trust record without previous key`() {
        // Act
        val record = TrustRecord(peerId, publicKey, TrustStatus.PINNED)

        // Assert
        assertNull(record.previousKey)
    }

    @Test
    fun `trust record can be updated to verified`() {
        // Act
        val record = TrustRecord(peerId, publicKey, TrustStatus.PINNED)
        val updated = record.updateStatus(TrustStatus.VERIFIED)

        // Assert
        assertEquals(TrustStatus.VERIFIED, updated.status)
        assertEquals(TrustStatus.PINNED, record.status) // original unchanged
    }

    @Test
    fun `trust record can be updated with new key for rotation`() {
        // Arrange
        val newKey = CryptoKey.fromBytes(ByteArray(32) { 0x03 })

        // Act
        val record = TrustRecord(peerId, publicKey, TrustStatus.VERIFIED)
        val updated = record.updateKey(newKey)

        // Assert
        assertEquals(newKey, updated.publicKey)
        assertEquals(publicKey, updated.previousKey) // old key becomes previous
        assertEquals(TrustStatus.VERIFIED, updated.status)
    }

    @Test
    fun `trust record can be revoked`() {
        // Act
        val record = TrustRecord(peerId, publicKey, TrustStatus.VERIFIED)
        val revoked = record.revoke()

        // Assert
        assertEquals(TrustStatus.REVOKED, revoked.status)
        assertFalse(revoked.isActive)
    }

    @Test
    fun `active trust record is not revoked`() {
        // Act
        val pinned = TrustRecord(peerId, publicKey, TrustStatus.PINNED)
        val verified = TrustRecord(peerId, publicKey, TrustStatus.VERIFIED)
        val revoked = TrustRecord(peerId, publicKey, TrustStatus.REVOKED)

        // Assert
        assertTrue(pinned.isActive)
        assertTrue(verified.isActive)
        assertFalse(revoked.isActive)
    }

    @Test
    fun `trust record diagnostic id is safe`() {
        // Act
        val record = TrustRecord(peerId, publicKey, TrustStatus.PINNED)

        // Assert
        val str = record.toString()
        assertTrue(str.contains("TrustRecord"))
        assertTrue(str.contains(peerId.diagnosticId))
        assertFalse(str.contains("AB")) // no raw key material
    }
}
