# Story e05s02: RouteDigest Sync
**Epic:** e05 · **BCPs:** 4 · **Status:** todo

## 1. Business Narrative

Route table synchronization requires digests to detect and recover from table mismatches.

## 2. Stakeholders

- **Driver:** Routing team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Implement route digest protocol:
1. Generate periodic route digests
2. Compare with neighbor digests
3. Trigger full table push on mismatch

## 4. Goal

Working route digest synchronization.

## 5. Happy Path

```kotlin
val digest = routeDigestTracker.generateDigest()
val mismatch = routeDigestTracker.detectMismatch(neighborDigest)
if (mismatch) {
  router.sendFullTable()
}
```

## 6. Key Constraints

- Digest mismatch triggers immediate sync
- Must fit in single BLE packet
- Timestamp-based for staleness detection

## 7. Alternative Flows

- Stale digest → request fresh

## 8. Out of Scope

- Compression of full table

## 9. Architecture Notes

RouteDigestTracker with periodic generation.

## 10. Wire Compatibility

RouteDigest schema from e02s01.

## 11. Dependencies

- e02, e05s01

## 12. Testing Strategy

- Digest generation tests
- Mismatch detection tests
- Full sync trigger tests

## 13. Observability

- `route_digest_generated`: entry_count
- `route_digest_mismatch`: expected vs actual

## 14. Configuration

None.

## 15. Error Handling

- `RoutingError.SyncFailed` on push failure

## 16. Naming Conventions

- `RouteDigestTracker` class
- `generateDigest()` method

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Digest mismatch triggers full sync
  Given a route table with entries
  When neighbor sends digest with different timestamp
  Then full route table is pushed
```

## 18. References

- `docs/decisions/routing/destination-sourced-seqno-ihu-removal-digest-resync-design.md`
- RFC 8966 (Babel digest)