# Story e09s04: Integration with Virtual Harness
**Epic:** e09 · **BCPs:** 3 · **Status:** todo

## 1. Business Narrative

iOS proof must integrate with the JVM virtual harness for cross-platform verification.

## 2. Stakeholders

- **Driver:** Validation team
- **Affected:** QA engineers

## 3. Problem Statement

Connect iOS tests to virtual harness:
1. Wire compatible messages
2. Shared test vectors
3. Cross-platform parity assertions

## 4. Goal

Integrated validation suite.

## 5. Happy Path

```swift
func testWireCompatibilityWithJvm() {
  let bytes = loadTestVector("mesh_envelope.bin")
  let decoded = try! MeshEnvelope(serializedData: bytes)
  assertValidMeshEnvelope(decoded)
}
```

## 6. Key Constraints

- Same wire format as JVM
- Test vectors shared
- XCTest + Kotlin interop

## 7. Alternative Flows

- JVM offline → skip tests

## 8. Out of Scope

- Mock harness (uses real JVM)

## 9. Architecture Notes

Integration tests call into JVM process.

## 10. Wire Compatibility

Full compatibility with meshlink module.

## 11. Dependencies

- e02 (Wire Format)

## 12. Testing Strategy

- Shared test vector tests
- Cross-platform handshake tests

## 13. Observability

- `integration_test_passed`: vector_name

## 14. Configuration

None.

## 15. Error Handling

- Test vector mismatch → fail

## 16. Naming Conventions

- XCTest with JVM integration

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: iOS decodes JVM wire format
  Given a test vector generated on JVM
  When iOS decodes it
  Then the decoded values match expectations
```

## 18. References

- `docs/decisions/ios/ios-proof-module-design.md`