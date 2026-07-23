# Story e06s03: Transfer State Machine
**Epic:** e06 · **BCPs:** 2 · **Status:** todo

## 1. Business Narrative

Transfer sessions need state machine handling pause/resume/timeouts.

## 2. Stakeholders

- **Driver:** Transfer team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Implement transfer state transitions:
1. `IN_PROGRESS` → active transfer
2. `PAUSED` → waiting for route
3. `COMPLETE` → success
4. `FAILED`/`TIMEOUT` → error states

## 4. Goal

Working transfer state machine.

## 5. Happy Path

```kotlin
val session = TransferSession.create(destPeerId, data)
session.handleRouteLoss()
assertEquals(TransferStatus.PAUSED, session.status)
session.handleRouteRestore()
assertEquals(TransferStatus.IN_PROGRESS, session.status)
```

## 6. Key Constraints

- State transitions only on events
- Grace period on timeout
- Persist state across restart

## 7. Alternative Flows

- Timeout → FAILED
- Route restore → resume

## 8. Out of Scope

- State persistence (handled elsewhere)

## 9. Architecture Notes

TransferSession with state enum.

## 10. Wire Compatibility

None.

## 11. Dependencies

- e06s01, e07 (Peer Lifecycle)

## 12. Testing Strategy

- State transition tests
- Timeout tests
- Resume tests

## 13. Observability

- `transfer_state_changed`: session, old, new

## 14. Configuration

None.

## 15. Error Handling

- Invalid transition → ignore or log

## 16. Naming Conventions

- `TransferStatus` enum

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Transfer pauses on route loss
  Given an active transfer session
  When the route to destination is lost
  Then the session transitions to PAUSED
```

## 18. References

- `docs/decisions/transfer/transfer-reliability.md`
- `docs/explanation/peer-lifecycle.md`