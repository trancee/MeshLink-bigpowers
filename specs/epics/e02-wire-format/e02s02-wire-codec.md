# Story e02s02: Wire Codec Implementation
**Epic:** e02 · **BCPs:** 3 · **Status:** todo

## 1. Business Narrative

Developers need a wire codec that encodes and decodes FlatBuffers messages correctly, matching the format used by both Android and iOS peers.

## 2. Stakeholders

- **Driver:** Protocol maintainers
- **Affected:** All MeshLink developers

## 3. Problem Statement

The wire codec must:
1. Encode all message types to ByteBuffer for BLE transmission
2. Decode ByteBuffer to typed messages
3. Handle forward compatibility (unknown fields)
4. Match wire format exactly

## 4. Goal

Complete `WireCodec.kt` implementation with all encode/decode methods.

## 5. Happy Path

```kotlin
val chunk = TransferChunk(sessionId, offset, length, data, true)
val bytes = WireCodec.encode(chunk)
val decoded = WireCodec.decodeTransferChunk(bytes)
assertEquals(chunk.sessionId, decoded.sessionId)
```

## 6. Key Constraints

- Use pure-Kotlin FlatBuffers (no flatc)
- All decode methods return null on malformed input
- No exceptions on unknown fields

## 7. Alternative Flows

- Malformed input → return null
- Unknown type → log warning, skip

## 8. Out of Scope

- JSON serialization
- Compression

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Encode transfer chunk
  Given a TransferChunk with valid data
  When I encode it
  Then I can decode it back identically
  And the size is within BLE MTU bounds
```

## 18. References

- `specs/decisions/wire/wire-format-spec.md`