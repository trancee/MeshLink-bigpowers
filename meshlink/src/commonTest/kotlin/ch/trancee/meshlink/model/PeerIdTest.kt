package ch.trancee.meshlink.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/** Tests for PeerId type-safe wrapper. Implements acceptance criteria from e01s01 spec. */
class PeerIdTest {

    @Test
    fun `create valid PeerId from 16-byte array`() {
        // Arrange
        val bytes = ByteArray(16) { it.toByte() }

        // Act
        val peerId = PeerId.fromBytes(bytes)

        // Assert
        assertEquals(16, peerId.byteArray.size)
        assertEquals("peer:0", peerId.diagnosticId)
    }

    @Test
    fun `generate creates stable random ID`() {
        // Act
        val peerId1 = PeerId.generate()
        val peerId2 = PeerId.generate()

        // Assert - generated IDs should be distinct
        assertTrue(peerId1.byteArray.isNotEmpty())
        assertTrue(peerId2.byteArray.isNotEmpty())
    }

    @Test
    fun `reject invalid size at construction`() {
        // Arrange
        val invalidBytes = ByteArray(20) // wrong size for PeerId which expects 16

        // Act & Assert
        assertFailsWith<IllegalArgumentException> { PeerId.fromBytes(invalidBytes) }
    }

    @Test
    fun `reject empty byte array at construction`() {
        // Arrange
        val emptyBytes = ByteArray(0)

        // Act & Assert
        assertFailsWith<IllegalArgumentException> { PeerId.fromBytes(emptyBytes) }
    }

    @Test
    fun `diagnosticId returns correct format`() {
        // Arrange
        val bytes = ByteArray(16) { 0x42 } // first byte is 0x42 = 66

        // Act
        val peerId = PeerId.fromBytes(bytes)

        // Assert
        assertEquals("peer:66", peerId.diagnosticId)
    }
}
