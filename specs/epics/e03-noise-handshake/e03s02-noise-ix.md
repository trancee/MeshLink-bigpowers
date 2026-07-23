# Story e03s02: Noise IX E2E Handshake
**Epic:** e03 · **BCPs:** 6 · **Status:** todo

## 1. Business Narrative

End-to-end encryption requires Noise IX handshake when destination key is known, providing destination authentication.

## 2. Stakeholders

- **Driver:** Security team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Implement Noise_IX_25519_ChaChaPoly_SHA256 handshake:
1. Source ephemeral key exchange
2. Destination static key authentication
3. Secure E2E transport establishment

## 4. Goal

Working Noise IX handshake for known destinations.

## 5. Happy Path

```kotlin
val handshake = NoiseHandshake(NoisePatterns.IX)
val session = handshake.initiate(destinationKey)
val accepted = handshake.respond(initiatorEphemeral, localStatic)
assertTrue(session.canEncrypt())
```

## 6. Key Constraints

- IX requires destination key to be known upfront
- Must handle unknown key (fallback to NX)
- Uses ChaChaPoly for AEAD

## 7. Alternative Flows

- Unknown key → fallback to NX (handled in e04)

## 8. Out of Scope

- Peer discovery (handled elsewhere)
- Key rotation (handled in e08)

## 9. Architecture Notes

NoiseHandshake class with pattern enum.

## 10. Wire Compatibility

IX payload inside MeshEnvelope.

## 11. Dependencies

- e01 (PeerKey type)
- e03s03 (Crypto Provider)

## 12. Testing Strategy

- IX handshake round-trip tests
- Wycheproof vectors for ChaChaPoly
- Unknown key fallback trigger tests

## 13. Observability

- `e2e_handshake_ix_initiated`: destination
- `e2e_handshake_ix_completed`: destination

## 14. Configuration

None

## 15. Error Handling

- HandshakeError.UnknownKey if key missing

## 16. Naming Conventions

- `NoisePatterns.IX` enum value
- `NoiseHandshake` class

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: IX handshake completes with known key
  Given two peers with known static keys
  When they perform IX handshake
  Then both have E2E transport keys
  And messages encrypt/decrypt correctly
```

## 18. References

- `docs/decisions/crypto/e2e-handshake-pattern.md`
- Noise Protocol Framework §7.2 (IX pattern)