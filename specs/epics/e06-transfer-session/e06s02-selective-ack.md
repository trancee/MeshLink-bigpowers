# Story e06s02: Selective ACK (SACK)
**Epic:** e06 · **BCPs:** 4 · **Status:** todo

## 1. Business Narrative

Efficient retransmission requires selective ACK to avoid resending received chunks.

## 2. Stakeholders

- **Driver:** Transfer team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Implement SACK-style acknowledgment:
1. Track received byte ranges
2. Generate SACK blocks in ACK
3. Request specific missing chunks

## 4. Goal

Selective retransmission on packet loss.

## 5. Happy Path

```kotlin
val scoreboard = Scoreboard(received = listOf(0u..255u, 512u..767u))
val ack = scoreboard.generateAck()
assertTrue(ack.missing.contains(256u..511u))
```

</think>
... (continuing)
Here's the full file writing:
<tool_call>write
<arg_key>content</arg_key>
<arg_value># Story e06s02: Selective ACK (SACK)
**Epic:** e06 · **BCPs:** 4 · **Status:** todo

## 1. Business Narrative

Efficient retransmission requires selective ACK to avoid resending received chunks.

## 2. Stakeholders

- **Driver:** Transfer team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Implement SACK-style acknowledgment:
1. Track received byte ranges
2. Generate SACK blocks in ACK
3. Request specific missing chunks

## 4. Goal

Selective retransmission on packet loss.

## 5. Happy Path

```kotlin
val scoreboard = Scoreboard(received = listOf(0u..255u, 512u..767u))
val ack = scoreboard.generateAck()
assertTrue(ack.missing.contains(256u..511u))
```

## 6. Key Constraints

- SACK blocks fit in MTU
- Merge adjacent ranges
- Max 4 missing ranges in ACK

## 7. Alternative Flows

- Gap at start → single range request
- All received → terminal ACK

## 8. Out of Scope

- Cumulative ACK (always use SACK)

## 9. Architecture Notes

Scoreboard with range merging.

## 10. Wire Compatibility

TransferAck schema from e02s01.

## 11. Dependencies

- e06s01

## 12. Testing Strategy

- Range merge tests
- SACK generation tests
- Missing range detection tests

## 13. Observability

- `sack_generated`: session, missing_ranges
- `retransmit_requested`: session, offset

## 14. Configuration

None.

## 15. Error Handling

- `TransferError.InvalidAck` on malformed SACK

## 16. Naming Conventions

- `Scoreboard` class
- `ChunkRange` for ranges

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Generate SACK for missing chunks
  Given a scoreboard with received and missing ranges
  When I generate an ACK
  Then missing ranges are correctly identified
```

## 18. References

- `docs/decisions/transfer/transfer-reliability.md`