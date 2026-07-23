# Story e02s03: Wire Compatibility Tests
**Epic:** e02 · **BCPs:** 2 · **Status:** todo

## 1. Business Narrative

Cross-platform parity requires wire compatibility tests to verify encode/decode works identically on Android and iOS.

## 2. Stakeholders

- **Driver:** Protocol maintainers
- **Affected:** All MeshLink developers

## 3. Problem Statement

Need test vectors for wire format verification:
1. Round-trip encode/decode for all message types
2. Forward compatibility (unknown fields skipped)
3. Size validation within BLE MTU bounds

## 4. Goal

Comprehensive wire format test suite.

## 5. Happy Path

```kotlin
val envelope = MeshEnvelope(peerId, payload, 3u)
val bytes = WireCodec.encode(envelope)
val decoded = WireCodec.decodeMeshEnvelope(bytes)
assertEquals(envelope, decoded)
```

## 6. Key Constraints

- Tests must run on JVM (commonMain)
- No platform-specific assertions
- All FlatBuffers schemas covered

## 7. Alternative Flows

- Corrupted data → null returned

## 8. Out of Scope

- iOS-specific test target
- Android-specific test

## 9. Architecture Notes

Tests in `commonTest` source set.

## 10. Wire Compatibility

Test vectors used for both platforms.

## 11. Dependencies

- e02s01, e02s02

## 12. Testing Strategy

- Round-trip tests for each type
- Size boundary tests
- Unknown field skip tests

## 13. Observability

- `wire_round_trip_success`: type
- `wire_size_check`: type, size, mtu_ok

## 14. Configuration

None

## 15. Error Handling

- Assertions for failures

## 16. Naming Conventions

- `*WireCompatTest` for test classes

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: MeshEnvelope round-trip
  Given a MeshEnvelope with destination, payload, hop limit
  When I encode and decode
  Then I get the identical data back
  And the size fits in standard BLE MTU
```

## 18. References

- `docs/explanation/why-pure-kotlin-flatbuffers.md`