# Story e07s01: Peer State Machine
**Epic:** e07 · **BCPs:** 4 · **Status:** todo

## 1. Business Narrative

Peer connections need state machine for CONNECTED → DISCONNECTED → GONE transitions.

## 2. Stakeholders

- **Driver:** Lifecycle team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Implement peer state machine:
1. `CONNECTED` on BLE connection established
2. `DISCONNECTED` on connection lost (with grace period)
3. `GONE` after grace sweeps expire

## 4. Goal

Working peer lifecycle state machine.

## 5. Happy Path

```kotlin
val state = ConnectionState(peerId)
state.onConnected()
assertEquals(PeerConnectionState.CONNECTED, state.connectionState)
state.onDisconnected()
assertEquals(PeerConnectionState.DISCONNECTED, state.connectionState)
```

## 6. Key Constraints

- Grace period prevents flapping
- State only exposed as CONNECTED/DISCONNECTED
- GONE is internal only

## 7. Alternative Flows

- Grace expired → GONE

## 8. Out of Scope

- Reconnection logic
- Platform lifecycle

## 9. Architecture Notes

ConnectionState with state transitions.

## 10. Wire Compatibility

None.

## 11. Dependencies

- None (foundational)

## 12. Testing Strategy

- State transition tests
- Grace period tests
- Integration with router

## 13. Observability

- `peer_state_changed`: peerId, old, new
- `peer_grace_sweep`: peerId, sweeps_remaining

## 14. Configuration

None.

## 15. Error Handling

- Invalid transition → ignore

## 16. Naming Conventions

- `PeerConnectionState` enum
- `ConnectionState` class

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Peer transitions to disconnected on connection loss
  Given a connected peer
  When the BLE connection is lost
  Then the state becomes DISCONNECTED
  And grace sweeps begin
```

## 18. References

- `docs/explanation/peer-lifecycle.md`
- `docs/decisions/routing/destination-sourced-seqno-ihu-removal-digest-resync-design.md`