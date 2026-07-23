# Story e01s03: RouteEntry and LinkMetric
**Epic:** e01 · **BCPs:** 1 · **Status:** todo

## 1. Business Narrative

Multi-hop routing requires route entries storing destination, next hop, seqno, and link quality metric.

## 2. Stakeholders

- **Driver:** Routing team
- **Affected:** All MeshLink developers

## 3. Problem Statement

The codebase needs route table entry types with:
1. Destination and next hop identification
2. Sequence number for route freshness
3. Link quality metric combining RSSI and capability flags

## 4. Goal

RouteEntry and LinkMetric types for routing table.

## 5. Happy Path

```kotlin
val metric = LinkMetric(
  rssiNormalized = 187u,
  supportsCoc = true,
  fastInterval = true,
  highPowerTier = false
)
val entry = RouteEntry(
  destination = destPeerId,
  nextHop = nextPeerId,
  seqNo = 1u,
  metric = metric.composite
)
assertEquals(187u, entry.metric and 0xFFu)
```

## 6. Key Constraints

- Metric combines RSSI (0-255) with capability flags
- Seqno sourced by destination (per RFC 8966)
- Feasibility flag for route validation

## 7. Alternative Flows

- Expired route → removed on access
- Infinite metric → route considered unreachable

## 8. Out of Scope

- Route selection algorithm
- Metric refinement

## 9. Architecture Notes

RouteEntry is a data class; LinkMetric has composite property.

## 10. Wire Compatibility

Matches wire format in e02s01.

## 11. Dependencies

- None (self-contained)

## 12. Testing Strategy

- Unit tests for metric composition
- Seqno handling tests
- Feasibility condition tests

## 13. Observability

- `route_metric_computed`: rssi, coc, interval flags

## 14. Configuration

None.

## 15. Error Handling

- IllegalArgumentException on invalid RSSI range

## 16. Naming Conventions

- `RouteEntry` in `ch.trancee.meshlink.model`
- `LinkMetric` with `composite` property

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Create route entry with metric
  Given a destination and next hop peer ID
  When I create a RouteEntry with RSSI -60 dBm and CoC support
  Then the metric composite has correct bit flags set
```

## 18. References

- `docs/decisions/routing/link-quality-metric.md`
- `docs/decisions/routing/destination-sourced-seqno-ihu-removal-digest-resync-design.md`