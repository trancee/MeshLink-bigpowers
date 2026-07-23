# Story e02s01: FlatBuffers Schema Definitions
**Epic:** e02 · **BCPs:** 3 · **Status:** todo

## 1. Business Narrative

Developers need a wire format that is compact, forward-compatible, and works identically on Android and iOS. FlatBuffers provides this without external code generation.

## 2. Stakeholders

- **Driver:** Protocol maintainers
- **Affected:** All MeshLink developers
- **Secondary:** Security auditors (wire compatibility)

## 3. Problem Statement

The wire protocol needs schemas for:
1. MeshEnvelope (routed E2E messages)
2. KeyRotationAnnouncement (key rotation)
3. RouteUpdate/RouteDigest (routing)
4. TransferChunk/TransferAck (chunked transfer)

These must be forward-compatible and type-safe.

## 4. Goal

Pure-Kotlin FlatBuffers schemas with explicit field IDs and forward-compatibility.

## 5. Happy Path

```kotlin
val envelope = MeshEnvelope(
  destination = peerId,
  payload = handshakeBytes,
  hopLimit = 3u
)
val wire = WireCodec.encode(envelope)
val decoded = WireCodec.decodeMeshEnvelope(wire)
assertEquals(envelope.destination, decoded.destination)
```

## 6. Key Constraints

- No flatc code generation (pure Kotlin)
- All fields required (no optional)
- Forward-compatible (skip unknown fields)

## 7. Alternative Flows

- Unknown field types → silently skip
- Corrupted data → fail gracefully

## 8. Out of Scope

- Union variants for all message types
- JSON serialization

## 9. Architecture Notes

Schemas in `meshlink/src/commonMain/kotlin/ch/trancee/meshlink/wire/`.

## 10. Wire Compatibility

Version 1 format. Breaking changes require major version bump.

## 11. Dependencies

- None

## 12. Testing Strategy

- Round-trip encode/decode
- Forward compatibility
- Size validation

## 13. Observability

- Invalid schema → Logged via diagnostics

## 14. Configuration

None

## 15. Error Handling

Return null or throw on corrupted data — testable error.

## 16. Naming Conventions

- Schema names: PascalCase matching message type
- File names: snake-case (e.g., `mesh_envelope.fbs`)

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Encode and decode mesh envelope
  Given a MeshEnvelope with destination and payload
  When I encode and decode the envelope
  Then I get the same destination and payload
```

## 18. References

- `docs/explanation/why-pure-kotlin-flatbuffers.md`
- `specs/decisions/wire/wire-format-spec.md`