# Story e07s03: Event Emission
**Epic:** e07 · **BCPs:** 2 · **Status:** todo

## 1. Business Narrative

Applications need to observe peer lifecycle changes for UI and diagnostics.

## 2. Stakeholders

- **Driver:** API team
- **Affected:** Application developers

## 3. Problem Statement

Emit lifecycle events:
1. Peer CONNECTED event
2. Peer DISCONNECTED event
3. Peer GONE event (internal)

## 4. Goal

Observable peer lifecycle events.

## 5. Happy Path

```kotlin
val events = mutableListOf<PeerEvent>()
meshLink.observeEvents { events.add(it) }
// When peer connects:
assertEquals(PeerEvent.Connected(peerId), events.first())
```

## 6. Key Constraints

- Events only for CONNECTED/DISCONNECTED
- No duplicate events
- Suspend callback for flow control

## 7. Alternative Flows

- Rapid state change → latest state only

## 8. Out of Scope

- GONE event exposure
- History replay

## 9. Architecture Notes

SharedFlow for event emission.

## 10. Wire Compatibility

None.

## 11. Dependencies

- e07s01

## 12. Testing Strategy

- Event emission tests
- Duplicate prevention tests
- Flow control tests

## 13. Observability

- `lifecycle_event_emitted`: type, peerId

## 14. Configuration

None.

## 15. Error Handling

- Throw in callback → try-catch + log

## 16. Naming Conventions

- `PeerEvent` sealed class
- `observeEvents()` extension

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Emit connected event
  Given a MeshLink instance with event observer
  When a peer connects
  Then a Connected event is emitted
```

## 18. References

- `docs/decisions/events/public-api-events.md`