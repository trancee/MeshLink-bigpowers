# Story e10s02: Adaptive Grace Period
**Epic:** e10 · **BCPs:** 3 · **Status:** todo

## 1. Business Narrative

Grace periods should adapt based on peer history and power tier to optimize for real-world behavior.

## 2. Stakeholders

- **Driver:** Lifecycle team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Implement adaptive grace:
1. Track peer connection stability
2. Calculate grace duration
3. Consider power tier for timing

## 4. Goal

Optimized peer lifecycle timing.

## 5. Happy Path

```kotlin
val grace = AdaptiveGracePeriod()
val stableGrace = grace.calculate(peerHistory.stable, PowerTier.MEDIUM)
val unstableGrace = grace.calculate(peerHistory.unstable, PowerTier.MEDIUM)
assertTrue(stableGrace > unstableGrace)
```

## 6. Key Constraints

- Minimum 1 second
- Maximum 5 minutes
- Stability score based on reconnections

## 7. Alternative Flows

- Unstable peer → short grace
- Power OFF → no grace

## 8. Out of Scope

- ML-based prediction
- User-configurable grace

## 9. Architecture Notes

AdaptiveGracePeriod with history input.

## 10. Wire Compatibility

None.

## 11. Dependencies

- e07 (Peer Lifecycle)

## 12. Testing Strategy

- Calculation tests
- Integration with ConnectionState

## 13. Observability

- `grace_duration_calculated`: peerId, duration

## 14. Configuration

None.

## 15. Error Handling

- Invalid input → use default

## 16. Naming Conventions

- `AdaptiveGracePeriod` class
- `PeerHistory` for stability tracking

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Stable peer gets longer grace
  Given a peer with stable connection history
  When I calculate grace period for MEDIUM tier
  Then it exceeds the unstable peer grace period
```

## 18. References

- `docs/decisions/power/power-tier-behavior.md`
- `docs/explanation/peer-lifecycle.md`