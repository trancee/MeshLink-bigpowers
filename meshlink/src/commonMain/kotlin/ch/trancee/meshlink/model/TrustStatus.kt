package ch.trancee.meshlink.model

/**
 * Trust status lifecycle for peer identity verification.
 *
 * States:
 * - PINNED: Trust On First Use - peer key is stored but not yet verified
 * - VERIFIED: Peer key has been successfully verified
 * - REVOKED: Trust has been revoked (terminal state)
 */
enum class TrustStatus {
    PINNED {
        override fun transitionTo(next: TrustStatus): TrustStatus {
            return when (next) {
                VERIFIED -> VERIFIED
                REVOKED -> REVOKED
                PINNED -> PINNED // idempotent
            }
        }
    },
    VERIFIED {
        override fun transitionTo(next: TrustStatus): TrustStatus {
            return when (next) {
                REVOKED -> REVOKED
                VERIFIED -> VERIFIED // idempotent
                PINNED -> VERIFIED // cannot go back to PINNED
            }
        }
    },
    REVOKED {
        override fun transitionTo(next: TrustStatus): TrustStatus {
            return REVOKED // terminal state
        }
    };

    abstract fun transitionTo(next: TrustStatus): TrustStatus

    val isInitial: Boolean
        get() = this == PINNED

    val isVerified: Boolean
        get() = this == VERIFIED

    val isRevoked: Boolean
        get() = this == REVOKED
}
