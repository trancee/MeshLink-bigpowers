# Story e06s01: Chunked Transfer Protocol
**Epic:** e06 · **BCPs:** 6 · **Status:** todo

## 1. Business Narrative

Large payloads require chunked transfer protocol over BLE's small MTU.

## 2. Stakeholders

- **Driver:** Transfer team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Implement chunked transfer:
1. Split payload into MTU-sized chunks
2. Track progress with scoreboard
3. Request selective retransmission on loss

## 4. Goal

Working chunked file transfer.

## 5. Happy Path

```kotlin
val session = TransferSession.create(destPeerId, largeData, chunkSize = 256)
val chunks = session.nextChunks()
submitChunks(chunks)
session.handleAck(ack)
assertTrue(session.isComplete)
```

## 6. Key Constraints

- Chunk size based on power tier
- Scoreboard tracks received ranges
- Timeout-based retry

## 7. Alternative Flows

- Timeout → resend chunks
- Peer disconnect → pause

## 8. Out of Scope

- Compression
- Parallel streams

## 9. Architecture Notes

TransferSession with state machine.

## 10. Wire Compatibility

Uses TransferChunk/Ack from e02s01.

## 11. Dependencies

- e02 (Wire Format)

## 12. Testing Strategy

- Chunk creation tests
- Scoreboard update tests
- Timeout/retry tests

## 13. Observability

- `transfer_chunk_sent`: session, offset, size
- `transfer_ack_received`: session, ranges

## 14. Configuration

None.

## 15. Error Handling

- `TransferError.Timeout` on deadline

## 16. Naming Conventions

- `TransferSession` class
- `ChunkRange` for scoreboard

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Transfer session creates chunks
  Given a large payload (1024 bytes) and chunk size 256
  When I create a TransferSession
  Then I get exactly 4 chunks
  And offsets are sequential
```

## 18. References

- `docs/decisions/transfer/transfer-reliability.md`