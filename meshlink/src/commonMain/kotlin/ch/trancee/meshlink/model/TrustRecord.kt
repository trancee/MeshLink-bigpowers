package ch.trancee.meshlink.model

import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Trust record for peer identity verification.
 *
 * Stores peer trust information including:
 * - The peer's public key
 * - Current trust status
 * - Optional previous key for rotation support
 * - Timestamps for audit trail
 */
data class TrustRecord(
    val peerId: PeerId,
    val publicKey: CryptoKey,
    var status: TrustStatus,
    val previousKey: CryptoKey? = null,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
) {
    /** Returns true if the trust record is active (not revoked). */
    val isActive: Boolean
        get() = status != TrustStatus.REVOKED

    /** Updates the trust status. */
    fun updateStatus(newStatus: TrustStatus): TrustRecord {
        return copy(status = newStatus, updatedAt = Clock.System.now())
    }

    /** Updates the public key (for key rotation) while preserving the old key. */
    fun updateKey(newKey: CryptoKey): TrustRecord {
        return copy(publicKey = newKey, previousKey = publicKey, updatedAt = Clock.System.now())
    }

    /** Revokes the trust record (terminal state). */
    fun revoke(): TrustRecord {
        return copy(status = TrustStatus.REVOKED, updatedAt = Clock.System.now())
    }

    override fun toString(): String {
        // Safe toString that doesn't expose key material
        return "TrustRecord(peer=${peerId.diagnosticId}, status=$status)"
    }
}
