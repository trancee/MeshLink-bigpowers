package ch.trancee.meshlink.model

/**
 * Type-safe PeerKey for mesh network peer identification.
 *
 * A 12-byte truncated hash derived from a public key, used for peer discovery and verification.
 * This type provides compile-time safety to prevent accidental misuse of raw byte arrays for peer
 * key identification.
 *
 * NEVER exposes raw key material in toString() or diagnostics.
 */
@JvmInline
value class PeerKey private constructor(private val bytes: ByteArray) {
    companion object {
        private const val SIZE = 12

        /**
         * Creates a PeerKey from a 12-byte array.
         *
         * @param bytes A 12-byte array representing the peer key
         * @return A PeerKey instance
         * @throws IllegalArgumentException if bytes is not exactly 12 bytes
         */
        fun fromBytes(bytes: ByteArray): PeerKey {
            require(bytes.size == SIZE) { "PeerKey must be $SIZE bytes, got ${bytes.size}" }
            return PeerKey(bytes.clone())
        }

        /**
         * Creates a PeerKey from a 32-byte public key by truncating to 12 bytes. This is used
         * during peer discovery to derive a short identifier.
         *
         * @param publicKey A 32-byte public key
         * @return A PeerKey instance (12 bytes)
         * @throws IllegalArgumentException if publicKey is not exactly 32 bytes
         */
        fun fromPublicKey(publicKey: ByteArray): PeerKey {
            require(publicKey.size == 32) { "PublicKey must be 32 bytes, got ${publicKey.size}" }
            // Truncate to 12 bytes (first 12 bytes of the public key)
            val truncated = ByteArray(SIZE) { publicKey[it] }
            return PeerKey(truncated)
        }
    }

    /**
     * Returns a diagnostic-friendly identifier that doesn't expose raw key material. Format:
     * "peerKey:<first-byte-as-decimal>"
     */
    val diagnosticId: String
        get() = "peerKey:${bytes[0].toInt() and 0xFF}"

    internal val byteArray: ByteArray
        get() = bytes.clone()
}
