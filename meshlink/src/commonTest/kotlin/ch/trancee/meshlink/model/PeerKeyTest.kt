package ch.trancee.meshlink.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** Tests for PeerKey type-safe wrapper. Implements acceptance criteria from e01s01 spec. */
class PeerKeyTest {

    @Test
    fun `create valid PeerKey from 12-byte array`() {
        // Arrange
        val bytes = ByteArray(12) { it.toByte() }

        // Act
        val peerKey = PeerKey.fromBytes(bytes)

        // Assert
        assertEquals(12, peerKey.byteArray.size)
        assertEquals("peerKey:0", peerKey.diagnosticId)
    }

    @Test
    fun `fromPublicKey creates 12-byte key from 32-byte public key`() {
        // Arrange
        val publicKey = ByteArray(32) { it.toByte() }

        // Act
        val peerKey = PeerKey.fromPublicKey(publicKey)

        // Assert
        assertEquals(12, peerKey.byteArray.size)
    }

    @Test
    fun `reject invalid size at construction`() {
        // Arrange
        val invalidBytes = ByteArray(20) // wrong size for PeerKey which expects 12

        // Act & Assert
        assertFailsWith<IllegalArgumentException> { PeerKey.fromBytes(invalidBytes) }
    }

    @Test
    fun `reject empty byte array at construction`() {
        // Arrange
        val emptyBytes = ByteArray(0)

        // Act & Assert
        assertFailsWith<IllegalArgumentException> { PeerKey.fromBytes(emptyBytes) }
    }

    @Test
    fun `diagnosticId returns correct format`() {
        // Arrange
        val bytes = ByteArray(12) { 0x42 } // first byte is 0x42 = 66

        // Act
        val peerKey = PeerKey.fromBytes(bytes)

        // Assert
        assertEquals("peerKey:66", peerKey.diagnosticId)
    }
}
