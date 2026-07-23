package ch.trancee.meshlink.wire

import ch.trancee.meshlink.model.PeerId

/**
 * Mesh envelope for routed end-to-end messages.
 *
 * Wire format for E2E message transport in the mesh network.
 * Uses FlatBuffers-inspired binary encoding for compact, forward-compatible serialization.
 */
data class MeshEnvelope(
    val destination: PeerId,
    val payload: ByteArray,
    val hopLimit: UInt
) {
    init {
        require(hopLimit <= 255u) { "Hop limit must be 0-255, got $hopLimit" }
    }
}