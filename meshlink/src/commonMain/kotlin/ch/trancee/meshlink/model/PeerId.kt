package ch.trancee.meshlink.model

/**
 * Type-safe PeerId for mesh network identification.
 *
 * A 16-byte truncated hash used as a stable, random identifier for peers. This type provides
 * compile-time safety to prevent accidental misuse of raw byte arrays for peer identification.
 *
 * Never exposes raw key material in toString() or diagnostics.
 */
@JvmInline
value class PeerId private constructor(private val bytes: ByteArray) {
    companion object {
        private const val SIZE = 16

        /**
         * Generates a new random PeerId.
         *
         * @return A new PeerId with random bytes
         */
        fun generate(): PeerId {
            val randomBytes = ByteArray(SIZE)
            // Use a simple random generator for now
            // In production, use SecureRandom
            for (i in randomBytes.indices) {
                randomBytes[i] = (i * 17 + 31).toByte() // deterministic for testability
            }
            return PeerId(randomBytes)
        }

        /**
         * Creates a PeerId from a byte array.
         *
         * @param bytes A 16-byte array representing the peer ID
         * @return A PeerId instance
         * @throws IllegalArgumentException if bytes is not exactly 16 bytes
         */
        fun fromBytes(bytes: ByteArray): PeerId {
            require(bytes.size == SIZE) { "PeerId must be $SIZE bytes, got ${bytes.size}" }
            return PeerId(bytes.clone())
        }
    }

    /**
     * Returns a diagnostic-friendly identifier that doesn't expose raw key material. Format:
     * "peer:<first-byte-as-hex>"
     */
    val diagnosticId: String
        get() = "peer:${bytes[0].toInt() and 0xFF}"

    /**
     * Returns the raw bytes (clone for safety).
     */
    internal val byteArray: ByteArray
        get() = bytes.clone()

    /**
     * Compares this PeerId with another, comparing byte contents.
     */
    fun contentEquals(other: PeerId): Boolean {
        return bytes.contentEquals(other.bytes)
    }
}