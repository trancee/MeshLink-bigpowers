package ch.trancee.meshlink.model

/**
 * Type-safe CryptoKey for cryptographic operations.
 *
 * A 32-byte full cryptographic key used for key exchange and encryption. This type provides
 * compile-time safety and ensures raw key material is never exposed in toString() or diagnostics.
 *
 * NEVER exposes raw key material in toString() or diagnostics.
 */
@JvmInline
value class CryptoKey private constructor(private val bytes: ByteArray) {
    companion object {
        private const val SIZE = 32

        /**
         * Creates a CryptoKey from a 32-byte array.
         *
         * @param bytes A 32-byte array representing the crypto key
         * @return A CryptoKey instance
         * @throws IllegalArgumentException if bytes is not exactly 32 bytes
         */
        fun fromBytes(bytes: ByteArray): CryptoKey {
            requireValidSize(bytes, "CryptoKey")
            return CryptoKey(bytes.clone())
        }

        /**
         * Creates a CryptoKey from a 32-byte public key.
         *
         * @param publicKey A 32-byte public key
         * @return A CryptoKey instance
         * @throws IllegalArgumentException if publicKey is not exactly 32 bytes
         */
        fun fromPublicKey(publicKey: ByteArray): CryptoKey {
            requireValidSize(publicKey, "PublicKey")
            return CryptoKey(publicKey.clone())
        }

        private fun requireValidSize(bytes: ByteArray, typeName: String) {
            if (bytes.size != SIZE) {
                throw IllegalArgumentException("$typeName must be $SIZE bytes, got ${bytes.size}")
            }
        }
    }

    /**
     * Returns a diagnostic-friendly identifier that doesn't expose raw key material. Format:
     * "cryptoKey:<first-byte-as-decimal>"
     */
    val diagnosticId: String
        get() = "cryptoKey:${bytes[0].toInt() and 0xFF}"

    internal val byteArray: ByteArray
        get() = bytes.clone()
}
