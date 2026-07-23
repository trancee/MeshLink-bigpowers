package ch.trancee.meshlink.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/** Tests for CryptoKey type-safe wrapper. Implements acceptance criteria from e01s01 spec. */
class CryptoKeyTest {

    @Test
    fun `create valid CryptoKey from 32-byte array`() {
        // Arrange
        val bytes = ByteArray(32) { it.toByte() }

        // Act
        val cryptoKey = CryptoKey.fromBytes(bytes)

        // Assert
        assertEquals(32, cryptoKey.byteArray.size)
    }

    @Test
    fun `fromPublicKey creates 32-byte key from public key`() {
        // Arrange
        val publicKey = ByteArray(32) { it.toByte() }

        // Act
        val cryptoKey = CryptoKey.fromPublicKey(publicKey)

        // Assert
        assertEquals(32, cryptoKey.byteArray.size)
    }

    @Test
    fun `reject invalid size at construction`() {
        // Arrange
        val invalidBytes = ByteArray(20) // wrong size for CryptoKey which expects 32

        // Act & Assert
        assertFailsWith<IllegalArgumentException> { CryptoKey.fromBytes(invalidBytes) }
    }

    @Test
    fun `reject empty byte array at construction`() {
        // Arrange
        val emptyBytes = ByteArray(0)

        // Act & Assert
        assertFailsWith<IllegalArgumentException> { CryptoKey.fromBytes(emptyBytes) }
    }

    @Test
    fun `diagnosticId returns correct format`() {
        // Arrange
        val bytes = ByteArray(32) { 0x42.toByte() } // first byte is 0x42 = 66

        // Act
        val cryptoKey = CryptoKey.fromBytes(bytes)

        // Assert
        assertEquals("cryptoKey:66", cryptoKey.diagnosticId)
    }

    @Test
    fun `default toString does not expose full key bytes`() {
        // Arrange
        val bytes = ByteArray(32) { 0xAB.toByte() }

        // Act
        val cryptoKey = CryptoKey.fromBytes(bytes)

        // Assert - default toString should not expose full key bytes
        val str = cryptoKey.toString()
        assertTrue(str.contains("CryptoKey"))
        // Should not expose the full 32-byte key as a hex string
        assertTrue(!str.contains("ab"))
        assertTrue(!str.contains("AB"))
    }
}
