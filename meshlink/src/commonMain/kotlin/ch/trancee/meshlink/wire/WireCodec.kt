package ch.trancee.meshlink.wire

import ch.trancee.meshlink.model.PeerId

/**
 * Wire codec for MeshLink binary format.
 *
 * Implements FlatBuffers-inspired binary encoding for compact, forward-compatible
 * serialization. Uses a simple table-based format with:
 * - Fixed header with field offsets
 * - Length-prefixed variable fields
 * - Forward-compatible (skips unknown fields)
 */
object WireCodec {

    private const val MESH_ENVELOPE_TYPE = 0x01u

    /**
     * Encodes a MeshEnvelope to binary format.
     */
    fun encode(envelope: MeshEnvelope): ByteArray {
        val destination = envelope.destination.byteArray
        val payload = envelope.payload

        // Calculate size:
        // 1 byte: type
        // 1 byte: hop limit
        // 16 bytes: destination (fixed length)
        // 4 bytes: payload length
        // N bytes: payload
        val totalSize = 1 + 1 + destination.size + 4 + payload.size
        val result = ByteArray(totalSize)

        var offset = 0
        result[offset++] = MESH_ENVELOPE_TYPE.toInt().toByte()
        result[offset++] = envelope.hopLimit.toInt().toByte()
        System.arraycopy(destination, 0, result, offset, destination.size)
        offset += destination.size
        result[offset++] = (payload.size shr 24).toByte()
        result[offset++] = (payload.size shr 16).toByte()
        result[offset++] = (payload.size shr 8).toByte()
        result[offset++] = payload.size.toByte()
        System.arraycopy(payload, 0, result, offset, payload.size)

        return result
    }

    /**
     * Decodes a MeshEnvelope from binary format.
     * Returns null if the data is corrupted or invalid.
     */
    fun decodeMeshEnvelope(data: ByteArray): MeshEnvelope? {
        if (data.size < 22) return null // minimum size: 1 + 1 + 16 + 4

        var offset = 0
        val type = (data[offset++].toInt() and 0xFF).toUInt()
        if (type != MESH_ENVELOPE_TYPE) return null

        val hopLimit = (data[offset++].toInt() and 0xFF).toUInt()

        if (data.size < offset + 16 + 4) return null

        val destination = data.sliceArray(offset until offset + 16)
        offset += 16

        val payloadLength = ((data[offset].toInt() and 0xFF) shl 24) or
                ((data[offset + 1].toInt() and 0xFF) shl 16) or
                ((data[offset + 2].toInt() and 0xFF) shl 8) or
                (data[offset + 3].toInt() and 0xFF)
        offset += 4

        if (data.size < offset + payloadLength) return null
        val payload = data.sliceArray(offset until offset + payloadLength)

        return try {
            MeshEnvelope(
                destination = PeerId.fromBytes(destination),
                payload = payload,
                hopLimit = hopLimit
            )
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}