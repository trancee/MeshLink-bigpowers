# Story e09s02: Security Framework Verification
**Epic:** e09 · **BCPs:** 5 · **Status:** todo

## 1. Business Narrative

iOS proof harness must verify that Security Framework keys are correctly used and that fallback paths work — otherwise the asymmetric security gap remains.

## 2. Stakeholders

- **Driver:** Security team
- **Affected:** iOS users
- **Secondary:** Protocol maintainers

## 3. Problem Statement

- Android proof validates crypto provider selection
- iOS has no equivalent real-device validation
- Security Framework behavior on iOS 14+ must be verified

## 4. Goal

Real-device iOS tests that validate Security Framework key operations.

## 5. Happy Path

```swift
func testSecurityFrameworkKeyGeneration() {
  let harness = IosProofHarness()
  let provider = harness.verifyCryptoProviderSelected("test-peer")
  XCTAssertEqual(provider, .securityFrameworkWithSecureEnclave)
}
```

## 6. Key Constraints

- iOS 14+ minimum (Secure Enclave support)
- Must run on real device (simulator insufficient)
- Must match Android proof patterns

## 7. Alternative Flows

- iOS < 14 → Use Java crypto fallback
- Secure Enclave unavailable → Software key

## 8. Out of Scope

- Simulator testing
- Performance benchmarks

## 9. Architecture Notes

Tests in `meshlink-proof/ios/src/test/swift/`. Use XCTest framework.

## 10. Wire Compatibility

N/A — test harness only.

## 11. Dependencies

- Real iOS device or cloud testing service

## 12. Testing Strategy

- Key generation test
- Sign/verify test
- Secure Enclave detection test

## 13. Observability

- Provider type exposed in diagnostics

## 14. Configuration

None

## 15. Error Handling

- Key generation failure → Test failure
- Secure Enclave unavailable → Logged

## 16. Naming Conventions

- Test methods: `test[Feature]` (no underscores)
- Swift naming: PascalCase

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Security Framework key generation works
  Given an iOS device running iOS 14+
  When I generate a key pair
  Then it uses Security Framework
  And Secure Enclave is used when available
```

## 18. References

- `docs/decisions/crypto/android-crypto-fallback-proof.md`
- `specs/decisions/ios/ios-proof-module-design.md`