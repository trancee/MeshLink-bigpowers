# Story e05s01: RouteTable and SeqNo
**Epic:** e05 · **BCPs:** 5 · **Status:** todo

## 1. Business Narrative

Multi-hop routing requires a route table storing destination, next hop, seqno, and metric.

## 5. Happy Path

```kotlin
val table = RouteTable()
table.install(RouteEntry(peerId, nextHop, seqNo, metric))
val entry = table.getRoute(peerId)
assertEquals(nextHop, entry?.nextHop)
```

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Install and retrieve route
  Given an empty RouteTable
  When I install a RouteEntry
  Then I can retrieve it by destination
```

## 18. References

- `docs/decisions/routing/destination-sourced-seqno-ihu-removal-digest-resync-design.md`