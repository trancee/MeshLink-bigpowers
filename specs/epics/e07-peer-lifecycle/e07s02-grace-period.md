# Story e07s02: Grace Period Logic
**Epic:** e07 · **BCPs:** 2 · **Status:** todo

## 1. Business Narrative

Adaptive grace periods prevent premature peer cleanup during transient disconnections.

## 2. Stakeholders

- **Driver:** Lifecycle team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Implement adaptive grace periods:
1. Adjust duration based on peer stability
2. Consider power tier for sweep timing
3. Count graceful sweeps before GONE

## 4. Goal

Adaptive peer lifecycle timing.

## 5. Happy Path

```kotlin
val grace = GracePeriodCalculator()
val stablePeer = grace.calculate(peerHistory = PeerHistory(stable = true))
val unstablePeer = grace.calculate(peerHistory = PeerHistory(stable = false))
assertTrue(stablePeer > unstablePeer)
```

## 6. Key Constraints

- Minimum 1 second, maximum 5 minutes
- Stability based on reconnection count
- Power tier affects timing

## 7. Alternative Flows

- Unstable peer → short grace

## 8. Out of Scope

- Predictive reconnection
- ML-based stability

## 9. Architecture Notes

GracePeriodCalculator with history input.

## 10. Wire Compatibility

None.

## 11. Dependencies

- e07s01

## 12. Testing Strategy

- Grace calculation tests
- Stability threshold tests
- Integration tests

## 13. Observability

- `grace_period_calculated`: peerId, duration
- `peer_stability_score`: peerId, score

## 14. Configuration

None.

## 15. Error Handling

- Invalid input → default grace

## 16. Naming Conventions

- `GracePeriodCalculator` class

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Stable peer gets longer grace period
  Given a peer with stable connection history
  When I calculate the grace period
  Then it is longer than for unstable peers
```

## 18. References

- `docs/decisions/power/power-tier-behavior.md`
- `docs/explanation/peer-lifecycle.md`