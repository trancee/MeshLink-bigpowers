# Story e05s03: Path Selection
**Epic:** e05 · **BCPs:** 4 · **Status:** todo

## 1. Business Narrative

Multi-hop routing requires path selection based on hop count and link quality metric.

## 2. Stakeholders

- **Driver:** Routing team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Implement route selection algorithm:
1. Apply RFC 8966 feasibility condition
2. Prefer lower hop count
3. Use link quality metric for tie-breaking

## 4. Goal

Working path selection for mesh.

## 5. Happy Path

```kotlin
val candidates = listOf(
  RouteCandidate(hopA, 3u, 187u),
  RouteCandidate(hopB, 2u, 150u)
)
val selected = pathSelector.select(candidates)
assertEquals(hopB, selected.nextHop)
```

## 6. Key Constraints

- Feasibility condition must be RFC 8966 compliant
- Metric high byte prioritizes CoC/fast interval
- Tie-breaker: lower hop count

## 7. Alternative Flows

- Unfeasible routes → ignored

## 8. Out of Scope

- Load balancing
- Multipath

## 9. Architecture Notes

PathSelector with compare function.

## 10. Wire Compatibility

None.

## 11. Dependencies

- e05s01

## 12. Testing Strategy

- Feasibility condition tests
- Metric comparison tests
- Hop count preference tests

## 13. Observability

- `path_selected`: destination, next_hop, metric

## 14. Configuration

None.

## 15. Error Handling

- Empty candidates → None returned

## 16. Naming Conventions

- `PathSelector` class
- `FeasibilityCondition` sealed class

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Select path with lower hop count
  Given two route candidates with different hop counts
  When I select the best path
  Then the candidate with fewer hops is chosen
```

## 18. References

- `docs/decisions/routing/link-quality-metric.md`
- RFC 8966 §3.2 (feasibility condition)