package ch.trancee.meshlink.model

/**
 * In-memory trust store for peer identities.
 *
 * Provides TOFU (Trust On First Use) identity pinning and key rotation support. In production, this
 * would use platform keystore for secure storage.
 */
class TrustStore {
    private val records = mutableMapOf<PeerId, TrustRecord>()

    /**
     * Saves a trust record for a peer. Returns true if the save was successful. Idempotent - saving
     * the same peer twice does not error.
     */
    fun save(peerId: PeerId, publicKey: CryptoKey, status: TrustStatus): Boolean {
        val existing = records[peerId]
        val record =
            if (existing == null) {
                TrustRecord(peerId, publicKey, status)
            } else {
                // Update existing record
                TrustRecord(
                    peerId = peerId,
                    publicKey = publicKey,
                    status = status,
                    previousKey = existing.publicKey,
                    createdAt = existing.createdAt,
                    updatedAt = existing.updatedAt,
                )
            }
        records[peerId] = record
        return true
    }

    /**
     * Retrieves a trust record for a peer. Returns null if the peer is unknown. Returns revoked
     * records for audit purposes.
     */
    fun get(peerId: PeerId): TrustRecord? {
        return records[peerId]
    }

    /** Updates the trust status for a peer. Returns true if the peer exists, false otherwise. */
    fun updateStatus(peerId: PeerId, newStatus: TrustStatus): Boolean {
        val record = records[peerId] ?: return false
        records[peerId] = record.updateStatus(newStatus)
        return true
    }

    /**
     * Updates the key for a peer (key rotation). Preserves the old key as previousKey. Returns true
     * if the peer exists, false otherwise.
     */
    fun updateKey(peerId: PeerId, newKey: CryptoKey): Boolean {
        val record = records[peerId] ?: return false
        records[peerId] = record.updateKey(newKey)
        return true
    }

    /**
     * Revokes trust for a peer. Returns true if the peer exists, false otherwise. Revoked records
     * are still retrievable for audit.
     */
    fun revoke(peerId: PeerId): Boolean {
        val record = records[peerId] ?: return false
        records[peerId] = record.revoke()
        return true
    }

    /** Removes a trust record for a peer. Returns true if the peer existed, false otherwise. */
    fun remove(peerId: PeerId): Boolean {
        return records.remove(peerId) != null
    }

    /** Returns all trust records. */
    fun getAll(): List<TrustRecord> {
        return records.values.toList()
    }

    /** Returns the number of trust records. */
    fun size(): Int {
        return records.size
    }
}
