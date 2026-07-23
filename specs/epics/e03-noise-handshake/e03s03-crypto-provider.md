# Story e03s03: Crypto Provider Abstraction
**Epic:** e03 · **BCPs:** 4 · **Status:** todo

## 1. Business Narrative

Cross-platform crypto requires abstraction over Android Keystore and iOS Security Framework.

## 2. Stakeholders

- **Driver:** Platform team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Create `CryptoProvider` abstraction for:
1. Key pair generation (X25519, Ed25519)
2. Signing and verification
3. AEAD encryption/decryption
4. HKDF key derivation

## 4. Goal

Working crypto abstraction with platform implementations.

## 5. Happy Path

```kotlin
val provider = CryptoProvider.default()
val keyPair = provider.generateKeyPair(Curve.X25519)
val signature = provider.sign(data, keyPair.private)
val valid = provider.verify(signature, data, keyPair.public)
assertTrue(valid)
```

## 6. Key Constraints

- Single implementation per platform
- SecureRandom as only RNG source
- No external crypto deps (pure Kotlin)

## 7. Alternative Flows

- Platform unsupported → fallback to Java crypto

## 8. Out of Scope

- BouncyCastle (forbidden by template)
- OpenSSL direct

## 9. Architecture Notes

Expect/actual pattern with JVM fallback.

## 10. Wire Compatibility

No wire impact.

## 11. Dependencies

- None (foundational)

## 12. Testing Strategy

- Unit tests for each crypto operation
- Wycheproof validation tests
- Platform integration tests

## 13. Observability

- `crypto_key_generated`: curve
- `crypto_operation_failed`: op, error

## 14. Configuration

None.

## 15. Error Handling

- `CryptoError.PlatformUnsupported` with fallback
- `CryptoError.OperationFailed` for bad signatures

## 16. Naming Conventions

- `CryptoProvider` interface
- `Curve.X25519` / `Curve.Ed25519`

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Generate X25519 key pair
  Given a CryptoProvider
  When I generate an X25519 key pair
  Then I get a 32-byte public key
  And I can derive shared secret
```

## 18. References

- `docs/decisions/crypto/e2e-handshake-pattern.md`