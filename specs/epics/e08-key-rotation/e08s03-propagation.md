# Story e08s03: Propagation Logic
**Epic:** e08 · **BCPs:** 2 · **Status:** todo

## 1. Business Narrative

Key rotation announcements must propagate through the mesh to all neighbors.

## 2. Stakeholders

- **Driver:** Routing team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Implement announcement propagation:
1. Gossip via route self-announcement
2. Within propagation deadline
3. Integration with RouteDigest

## 4. Goal

Mesh-wide key rotation propagation.

## 5. Happy Path

```kotlin
val propagator = KeyRotationPropagator()
propagator.broadcast(announcement)
// Neighbors receive within 3 seconds
```

## 6. Key Constraints

- Must reach 2-hop within 3 seconds
- Use existing route paths
- Digest mismatch triggers push

## 7. Alternative Flows

- Propagation delay → fallback

## 8. Out of Scope

- Network partition handling

## 9. Architecture Notes

KeyRotationPropagator with route integration.

## 10. Wire Compatibility

None.

## 11. Dependencies

- e05 (Routing)

## 12. Testing Strategy

- Propagation timing tests
- Multi-hop tests
- Deadline tests

## 13. Observability

- `key_rotation_propagated`: hop_count

## 14. Configuration

None.

## 15. Error Handling

- `RoutingError.NoRoute` → drop

## 16. Naming Conventions

- `KeyRotationPropagator` class

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Key rotation propagates to neighbors
  Given a key rotation announcement
  When it is broadcast
  Then all direct neighbors receive it within 1 second
  And 2-hop neighbors receive it within 3 seconds
```

## 18. References

- `docs/decisions/crypto/key-rotation-protocol.md`
- `docs/decisions/routing/destination-sourced-seqno-ihu-removal-digest-resync-design.md`