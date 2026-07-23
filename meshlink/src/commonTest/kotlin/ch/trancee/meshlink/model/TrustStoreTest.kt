package ch.trancee.meshlink.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Tests for TrustStore interface. Implements acceptance criteria from e01s02 spec. */
class TrustStoreTest {

    private val trustStore = TrustStore()
    private val peerId = PeerId.generate()
    private val publicKey = CryptoKey.fromBytes(ByteArray(32) { it.toByte() })

    @Test
    fun `store and retrieve trusted peer key`() {
        // Act
        val saved = trustStore.save(peerId, publicKey, TrustStatus.PINNED)

        // Assert
        assertTrue(saved)

        // Act - retrieve
        val retrieved = trustStore.get(peerId)

        // Assert
        assertNotNull(retrieved)
        assertEquals(publicKey, retrieved.publicKey)
        assertEquals(TrustStatus.PINNED, retrieved.status)
    }

    @Test
    fun `get returns null for unknown peer`() {
        // Act
        val unknownPeerId = PeerId.generate()
        val result = trustStore.get(unknownPeerId)

        // Assert
        assertNull(result)
    }

    @Test
    fun `save is idempotent for same peer`() {
        // Act
        val saved1 = trustStore.save(peerId, publicKey, TrustStatus.PINNED)
        val saved2 = trustStore.save(peerId, publicKey, TrustStatus.PINNED)

        // Assert
        assertTrue(saved1)
        assertTrue(saved2)

        // Verify only one record exists
        val records = trustStore.getAll()
        assertEquals(1, records.size)
    }

    @Test
    fun `update status for existing peer`() {
        // Act
        trustStore.save(peerId, publicKey, TrustStatus.PINNED)
        val updated = trustStore.updateStatus(peerId, TrustStatus.VERIFIED)

        // Assert
        assertTrue(updated)
        val record = trustStore.get(peerId)
        assertNotNull(record)
        assertEquals(TrustStatus.VERIFIED, record.status)
    }

    @Test
    fun `revoke peer trust`() {
        // Act
        trustStore.save(peerId, publicKey, TrustStatus.VERIFIED)
        val revoked = trustStore.revoke(peerId)

        // Assert
        assertTrue(revoked)
        val record = trustStore.get(peerId)
        assertNotNull(record)
        assertEquals(TrustStatus.REVOKED, record.status)
        assertFalse(record.isActive)
    }

    @Test
    fun `get returns revoked record for audit`() {
        // Act
        trustStore.save(peerId, publicKey, TrustStatus.VERIFIED)
        trustStore.revoke(peerId)
        val record = trustStore.get(peerId)

        // Assert - revoked records are still retrievable
        assertNotNull(record)
        assertEquals(TrustStatus.REVOKED, record.status)
    }

    @Test
    fun `getAll returns all trust records`() {
        // Arrange
        val peerId2 = PeerId.generate()
        val publicKey2 = CryptoKey.fromBytes(ByteArray(32) { 0x02 })

        // Act
        trustStore.save(peerId, publicKey, TrustStatus.PINNED)
        trustStore.save(peerId2, publicKey2, TrustStatus.VERIFIED)

        // Assert
        val all = trustStore.getAll()
        assertEquals(2, all.size)
    }

    @Test
    fun `remove trust record`() {
        // Act
        trustStore.save(peerId, publicKey, TrustStatus.PINNED)
        val removed = trustStore.remove(peerId)

        // Assert
        assertTrue(removed)
        assertNull(trustStore.get(peerId))
    }

    @Test
    fun `remove returns false for unknown peer`() {
        // Act
        val unknownPeerId = PeerId.generate()
        val removed = trustStore.remove(unknownPeerId)

        // Assert
        assertFalse(removed)
    }

    @Test
    fun `trust store handles key rotation`() {
        // Arrange
        val newKey = CryptoKey.fromBytes(ByteArray(32) { 0x03 })

        // Act
        trustStore.save(peerId, publicKey, TrustStatus.VERIFIED)
        val updated = trustStore.updateKey(peerId, newKey)

        // Assert
        assertTrue(updated)
        val record = trustStore.get(peerId)
        assertNotNull(record)
        assertEquals(newKey, record.publicKey)
        assertEquals(publicKey, record.previousKey)
    }

    @Test
    fun `size returns correct count`() {
        // Act
        val size = trustStore.size()

        // Assert - initially empty
        assertEquals(0, size)
    }

    @Test
    fun `size reflects added records`() {
        // Arrange
        val peerId2 = PeerId.generate()
        val publicKey2 = CryptoKey.fromBytes(ByteArray(32) { 0x02 })

        // Act
        trustStore.save(peerId, publicKey, TrustStatus.PINNED)
        trustStore.save(peerId2, publicKey2, TrustStatus.VERIFIED)

        // Assert
        assertEquals(2, trustStore.size())
    }
}
