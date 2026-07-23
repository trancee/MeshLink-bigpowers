# Story e08s02: Trust Store Update
**Epic:** e08 · **BCPs:** 2 · **Status:** todo

## 1. Business Narrative

TrustStore must handle key rotation with signature verification and grace period support.

## 2. Stakeholders

- **Driver:** Security team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Update TrustStore on key rotation:
1. Verify signature with old key
2. Store new key
3. Maintain old key for grace period

## 4. Goal

Secure key rotation propagation.

## 5. Happy Path

```kotlin
val updated = trustStore.handleKeyRotation(announcement)
assertTrue(updated)
val newKey = trustStore.getPublicKey(peerId)
assertEquals(announcement.publicKey.diagnosticId, newKey?.diagnosticId)
```

## 6. Key Constraints

- Signature verification required
- Old key retained during grace
- Seqno reset on update

## 7. Alternative Flows

- Signature fail → reject
- Unknown old key → reject

## 8. Out of Scope

- Key storage format changes

## 9. Architecture Notes

TrustStore.handleKeyRotation method.

## 10. Wire Compatibility

None.

## 11. Dependencies

- e01 (TrustStore)
- e08s01

## 12. Testing Strategy

- Signature verification tests
- Grace period tests
- Multi-rotation tests

## 13. Observability

- `trust_store_key_rotated`: peerId
- `trust_store_rotation_rejected`: peerId, reason

## 14. Configuration

- Grace period: 1 hour default

## 15. Error Handling

- `TrustStoreError.InvalidSignature`
- `TrustStoreError.UnknownOldKey`

## 16. Naming Conventions

- `handleKeyRotation()` method

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Accept valid key rotation announcement
  Given a TrustStore with known old key for a peer
  When a valid KeyRotationAnnouncement arrives
  Then the new key is stored
  And the old key is retained for grace period
```

## 18. References

- `docs/decisions/crypto/key-rotation-protocol.md`