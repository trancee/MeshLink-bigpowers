# Story e04s03: MeshEnvelope Routing
**Epic:** e04 · **BCPs:** 3 · **Status:** todo

## 1. Business Narrative

Multi-hop routing of E2E handshakes requires MeshEnvelope encapsulation.

## 2. Stakeholders

- **Driver:** Routing team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Route E2E handshakes through the mesh:
1. Encapsulate handshake in MeshEnvelope
2. Set appropriate hop limit
3. Forward to next hop toward destination

## 4. Goal

Routed E2E handshake delivery.

## 5. Happy Path

```kotlin
val envelope = MeshEnvelope(
  destination = destPeerId,
  payload = ixHandshakeBytes,
  hopLimit = 3u
)
val forwarded = router.forward(envelope, nextHop)
assertTrue(forwarded)
```

## 6. Key Constraints

- Hop limit decremented on forward
- Envelope dropped when hop limit reaches 0
- Must match route table

## 7. Alternative Flows

- No route → drop envelope
- Hop limit 0 → drop packet

## 8. Out of Scope

- Route discovery
- Mesh flooding

## 9. Architecture Notes

Router checks route table before forward.

## 10. Wire Compatibility

MeshEnvelope schema from e02s01.

## 11. Dependencies

- e02 (Wire Format)
- e05 (Routing Coordinator)

## 12. Testing Strategy

- Envelope forward tests
- Hop limit decrement tests
- No-route drop tests

## 13. Observability

- `mesh_envelope_forwarded`: destination, hop_limit
- `mesh_envelope_dropped`: destination, reason

## 14. Configuration

None.

## 15. Error Handling

- `RoutingError.NoRoute` when route missing

## 16. Naming Conventions

- `MeshEnvelope` type
- `hopLimit` decremented field

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Forward MeshEnvelope toward destination
  Given a route to destination via next hop
  When I create a MeshEnvelope with hop limit 3
  Then it is forwarded with hop limit 2
```

## 18. References

- `docs/decisions/crypto/e2e-routing-over-mesh.md`
- `docs/decisions/routing/destination-sourced-seqno-ihu-removal-digest-resync-design.md`