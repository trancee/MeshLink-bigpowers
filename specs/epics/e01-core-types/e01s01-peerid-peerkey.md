# Story e01s01: PeerId and PeerKey Types
**Epic:** e01 · **BCPs:** 2 · **Status:** todo

## 1. Business Narrative

Developers need type-safe identifiers to distinguish between peers and verify key identities without risking confusion between truncated hints and full keys. Using raw `ByteArray` causes type unsafety and potential misuse.

## 2. Stakeholders

- **Driver:** Library maintainers (want type safety)
- **Affected:** All MeshLink developers consuming the API
- **Secondary:** Security auditors (want no key exposure)

## 3. Problem Statement

The codebase currently lacks distinct types for:
1. Peer identity (16-byte truncated hash) — used in all APIs
2. Discovery hint (12-byte truncated key hash) — used in advertisements
3. Cryptographic key (32-byte full key) — NEVER exposed raw

Without these types, developers can accidentally:
- Pass a PeerKey where PeerId is expected
- Log or expose raw key material
- Mix up different key sizes

## 4. Goal

Create type-safe wrappers with compile-time size guarantees and diagnostic-safe identifiers.

## 5. Happy Path

```kotlin
val publicKey: CryptoKey = generateKeyPair().public
val peerId: PeerId = PeerId.generate()  // Stable random ID (16 bytes)
val peerKey: PeerKey = PeerKey.fromPublicKey(publicKey) // 12 bytes from key

// These are type-safe - can't be mixed
// peerId == peerKey // Compile error
```

## 6. Key Constraints

- Must be `@JvmInline` value classes for performance
- Must NOT expose raw key bytes in `toString()` or diagnostics
- Must enforce size at construction time

## 7. Alternative Flows

- Invalid byte array size → throw at construction
- Empty byte array → throw at construction

## 8. Out of Scope

- Key derivation (separate)
- Serialization (handled by wire codec)
- Platform-specific implementations (commonMain only)

## 9. Architecture Notes

These types live in `commonMain` and are used throughout the codebase. They are the foundation for type-safe APIs.

## 10. Wire Compatibility

No wire impact — types are internal to the codebase. Serialization uses the underlying ByteArray.

## 11. Dependencies

- None (foundational types)

## 12. Testing Strategy

- Unit tests for each type
- Boundary tests for size validation
- Diagnostic leak tests (ensure no key exposure)

## 13. Observability

- Constructor throws on invalid size → logged via diagnostics
- `diagnosticId` property → used in all diagnostic output

## 14. Configuration

None — immutable value classes.

## 15. Error Handling

Throw `IllegalArgumentException` for invalid size. This is a programming error, not a runtime error.

## 16. Naming Conventions

- Type names: `PeerId`, `PeerKey`, `CryptoKey` (not `PublicKey` — abstraction)
- Package: `ch.trancee.meshlink.model`

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Create valid PeerId from public key
  Given a 32-byte public key
  When I call PeerId.fromPublicKey()
  Then I get a 16-byte PeerId
  And diagnosticId returns "peer:<first-byte>"

Scenario: Reject invalid size at construction
  Given a 20-byte array (wrong size for PeerId which expects 16)
  When I try to create a PeerId
  Then I get an IllegalArgumentException

Scenario: PeerKey is distinct from PeerId
  Given a public key
  When I create both PeerId and PeerKey
  Then I cannot assign one to the other (compile error)
```

## 18. References

- `docs/decisions/data-model/core-types.md`
- `docs/decisions/crypto/nx-fallback-mitigation.md` (PeerKey usage)