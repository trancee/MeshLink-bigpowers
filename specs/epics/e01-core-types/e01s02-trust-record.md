# Story e01s02: TrustRecord and TrustStore
**Epic:** e01 · **BCPs:** 2 · **Status:** todo

## 1. Business Narrative

Developers need secure trust storage for peer identities, including TOFU pinning and key rotation support.

## 2. Stakeholders

- **Driver:** Security team
- **Affected:** All MeshLink developers
- **Secondary:** Security auditors

## 3. Problem Statement

The codebase needs persistent trust storage for:
1. TOFU (Trust On First Use) identity pinning
2. Key rotation tracking (old/new key support)
3. Trust status lifecycle (PINNED, VERIFIED, REVOKED)

## 4. Goal

Working TrustStore with secure key storage and verification APIs.

## 5. Happy Path

```kotlin
val trustStore = TrustStore()
val publicKey: CryptoKey = generateKeyPair().public
val saved = trustStore.save(peerId, publicKey)
assertTrue(saved)
val retrieved = trustStore.getPublicKey(peerId)
assertEquals(publicKey.diagnosticId, retrieved?.diagnosticId)
```

## 6. Key Constraints

- Keys stored in platform keystore (Android Keystore / iOS Security Framework)
- No raw key material in memory except during operations
- Trust status transitions validated

## 7. Alternative Flows

- Unknown peer → return null
- Double pin → idempotent (no error)
- Revoked peer → still retrievable (for audit)

## 8. Out of Scope

- Trust verification UI
- Certificate validation

## 9. Architecture Notes

TrustStore uses platform-specific storage via expect/actual.

## 10. Wire Compatibility

No wire impact — internal type.

## 11. Dependencies

- `e03s03` — Crypto Provider Abstraction

## 12. Testing Strategy

- Unit tests for each TrustStatus transition
- Integration tests with virtual keystore
- Key rotation round-trip tests

## 13. Observability

- `trust_store_key_saved`: peerId, status
- `trust_store_key_retrieved`: peerId found
- `trust_store_key_revoked`: peerId

## 14. Configuration

None.

## 15. Error Handling

- `TrustStoreError.UnknownPeer` on missing key
- `TrustStoreError.StorageFailure` on platform error
- Wrapped in sealed hierarchy

## 16. Naming Conventions

- `TrustStore` interface
- `TrustRecord` data class
- `TrustStatus` enum

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Store and retrieve trusted peer key
  Given a TrustStore and peerId/publicKey pair
  When I save the key for the peerId
  Then I can retrieve it later
  And the key is stored in platform keystore

Scenario: Key rotation preserves trust record
  Given a peer with an existing key
  When a KeyRotationAnnouncement arrives
  Then the trust record is updated with the new key
  And the old key is retained for grace period verification
```

## 18. References

- `docs/decisions/crypto/e2e-handshake-pattern.md`
- `docs/decisions/crypto/key-rotation-protocol.md`