# Story e03s01: Noise XX Handshake Implementation
**Epic:** e03 · **BCPs:** 5 · **Status:** todo

## 1. Business Narrative

Hop-by-hop link encryption requires Noise XX handshake for mutual authentication between adjacent peers.

## 2. Stakeholders

- **Driver:** Security team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Implement Noise_xx_25519_ChaChaPoly_SHA256 handshake:
1. Ephemeral key exchange
2. Mutual static key authentication
3. Transport key derivation
4. Platform crypto provider integration

## 4. Goal

Working Noise XX handshake over BLE link.

## 5. Happy Path

```kotlin
val handshake = NoiseHandshake(NoisePatterns.XX)
val session = handshake.initiate(peerKey)
val accepted = handshake.respond(peerKey)
assertTrue(session.canEncrypt())
```

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Complete XX handshake
  Given two peers with static keys
  When they perform XX handshake
  Then both have transport keys
  And both can encrypt/decrypt messages
```

## 18. References

- `docs/decisions/crypto/e2e-handshake-pattern.md`