# Story e04s01: NX Handshake with PeerKey
**Epic:** e04 · **BCPs:** 4 · **Status:** todo

## 1. Business Narrative

When the destination key is unknown, NX fallback enables one-way authenticated handshake with PeerKey verification.

## 2. Stakeholders

- **Driver:** Security team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Implement Noise_NX_25519_ChaChaPoly_SHA256 handshake:
1. Source ephemeral key exchange
2. Source static key in payload
3. PeerKey verification after handshake

## 4. Goal

Working NX handshake with verification.

## 5. Happy Path

```kotlin
val handshake = NoiseNxHandshake()
val session = handshake.initiate(peerKey, nonce)
val accepted = handshake.respond(receivedPayload)
assertTrue(session.isVerified)
```

## 6. Key Constraints

- Rate limiting: max 3 attempts/min
- 10s timeout vs 30s for IX
- PeerKey must match in verification

## 7. Alternative Flows

- Rate limit exceeded → reject
- PeerKey mismatch → reject

## 8. Out of Scope

- Key distribution (handled elsewhere)

## 9. Architecture Notes

NX-specific handshake class extending base NoiseHandshake.

## 10. Wire Compatibility

Uses PeerKey from discovery.

## 11. Dependencies

- e01 (PeerKey type)
- e03 (Noise XX/IX)

## 12. Testing Strategy

- NX handshake round-trip tests
- PeerKey verification tests
- Rate limiting tests

## 13. Observability

- `nx_handshake_initiated`: destination
- `nx_handshake_rate_limited`: destination
- `nx_handshake_peer_key_mismatch`: destination

## 14. Configuration

None.

## 15. Error Handling

- `NxHandshakeError.RateLimited` after 3 attempts
- `NxHandshakeError.PeerKeyMismatch` on verification fail

## 16. Naming Conventions

- `NoiseNxHandshake` class
- `NxFallbackState` for rate limiting

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: NX handshake with valid PeerKey
  Given a destination with known PeerKey but unknown full key
  When I initiate NX handshake with matching PeerKey
  Then the handshake completes
  And PeerKey verification succeeds
```

## 18. References

- `docs/decisions/crypto/nx-fallback-mitigation.md`