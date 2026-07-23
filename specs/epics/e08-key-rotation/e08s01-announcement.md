# Story e08s01: KeyRotationAnnouncement
**Epic:** e08 · **BCPs:** 4 · **Status:** todo

## 1. Business Narrative

Key rotation must be announced to the mesh with signed proof for verification.

## 2. Stakeholders

- **Driver:** Security team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Create KeyRotationAnnouncement wire format:
1. New public key
2. Seqno reset to 1
3. Signature with OLD key

## 4. Goal

Wire-compatible key rotation announcement.

## 5. Happy Path

```kotlin
val announcement = KeyRotationAnnouncement(
  publicKey = newPublicKey,
  seqNo = 1u,
  signature = signWithOldKey(data),
  reason = KeyRotationReason.PERIODIC
)
val bytes = WireCodec.encode(announcement)
val decoded = WireCodec.decodeKeyRotationAnnouncement(bytes)
assertEquals(announcement.publicKey, decoded.publicKey)
```

## 6. Key Constraints

- Must be signed with OLD key
- Seqno always resets to 1
- Wire format from e02s01

## 7. Alternative Flows

- Security event reason → override

## 8. Out of Scope

- Announcement distribution

## 9. Architecture Notes

Announcement created on key rotation.

## 10. Wire Compatibility

KeyRotationAnnouncement schema.

## 11. Dependencies

- e02 (Wire Format)

## 12. Testing Strategy

- Encode/decode tests
- Signature verification tests
- Seqno reset tests

## 13. Observability

- `key_rotation_announced`: peerId

## 14. Configuration

- Interval: 90 days default
- Grace period: 1 hour

## 15. Error Handling

- Invalid signature → reject

## 16. Naming Conventions

- `KeyRotationAnnouncement` type
- `KeyRotationReason` enum

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Create key rotation announcement
  Given a key rotation event
  When I create an announcement
  Then it contains the new public key
  And seqNo is reset to 1
```

## 18. References

- `docs/decisions/crypto/key-rotation-protocol.md`