package ch.trancee.meshlink.wire

import ch.trancee.meshlink.model.PeerId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for MeshEnvelope FlatBuffers schema.
 * Implements acceptance criteria from e02s01 spec.
 */
class MeshEnvelopeSchemaTest {

    @Test
    fun `encode and decode mesh envelope`() {
        // Arrange
        val destination = PeerId.generate()
        val payload = byteArrayOf(0x01, 0x02, 0x03, 0x04)

        // Act
        val envelope = MeshEnvelope(
            destination = destination,
            payload = payload,
            hopLimit = 3u
        )
        val encoded = WireCodec.encode(envelope)
        val decoded = WireCodec.decodeMeshEnvelope(encoded)

        // Assert
        assertTrue(destination.contentEquals(decoded?.destination!!))
        assertEquals(payload.toList(), decoded.payload.toList())
        assertEquals(3u, decoded.hopLimit)
    }

    @Test
    fun `mesh envelope round trip preserves all fields`() {
        // Arrange
        val destination = PeerId.generate()
        val payload = byteArrayOf(0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte())

        // Act
        val original = MeshEnvelope(
            destination = destination,
            payload = payload,
            hopLimit = 5u
        )
        val encoded = WireCodec.encode(original)
        val decoded = WireCodec.decodeMeshEnvelope(encoded)!!

        // Assert
        assertTrue(original.destination.contentEquals(decoded.destination))
        assertEquals(original.payload.toList(), decoded.payload.toList())
        assertEquals(original.hopLimit, decoded.hopLimit)
    }

    @Test
    fun `mesh envelope with empty payload`() {
        // Arrange
        val destination = PeerId.generate()

        // Act
        val envelope = MeshEnvelope(
            destination = destination,
            payload = byteArrayOf(),
            hopLimit = 1u
        )
        val encoded = WireCodec.encode(envelope)
        val decoded = WireCodec.decodeMeshEnvelope(encoded)!!

        // Assert
        assertEquals(0, decoded.payload.size)
    }

    @Test
    fun `mesh envelope hop limit wraps correctly`() {
        // Act
        val envelope = MeshEnvelope(
            destination = PeerId.generate(),
            payload = byteArrayOf(1),
            hopLimit = 255u
        )
        val encoded = WireCodec.encode(envelope)
        val decoded = WireCodec.decodeMeshEnvelope(encoded)!!

        // Assert
        assertEquals(255u, decoded.hopLimit)
    }

    @Test
    fun `decode returns null for corrupted data`() {
        // Arrange
        val corrupted = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte())

        // Act
        val result = WireCodec.decodeMeshEnvelope(corrupted)

        // Assert
        assertEquals(null, result)
    }

    @Test
    fun `decode returns null for empty data`() {
        // Act
        val result = WireCodec.decodeMeshEnvelope(byteArrayOf())

        // Assert
        assertEquals(null, result)
    }

    @Test
    fun `mesh envelope size is reasonable`() {
        // Arrange
        val payload = ByteArray(100) { 0x01 }

        // Act
        val envelope = MeshEnvelope(
            destination = PeerId.generate(),
            payload = payload,
            hopLimit = 3u
        )
        val encoded = WireCodec.encode(envelope)

        // Assert - should be compact: 1 + 1 + 16 + 4 + 100 = 122 bytes
        assertEquals(122, encoded.size)
    }
}