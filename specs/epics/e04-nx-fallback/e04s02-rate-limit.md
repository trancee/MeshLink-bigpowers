# Story e04s02: Rate Limiting and Timeout
**Epic:** e04 · **BCPs:** 3 · **Status:** todo

## 1. Business Narrative

NX fallback must be rate-limited to prevent DoS attacks from unauthenticated handshake attempts.

## 2. Stakeholders

- **Driver:** Security team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Implement NX rate limiting with:
1. Per-destination attempt counting
2. Sliding window (per-minute reset)
3. Short timeout (10s vs 30s for IX)

## 4. Goal

DoS-resistant NX handshake.

## 5. Happy Path

```kotlin
val limiter = NxFallbackLimiter()
assertTrue(limiter.canInitiate(destination, nonce))
limiter.recordAttempt(destination, nonce)
assertFalse(limiter.canInitiate(destination, nonce)) // on 4th attempt
```

## 6. Key Constraints

- Max 3 attempts per destination per minute
- Nonce replay protection
- Fast fail on limit exceeded

## 7. Alternative Flows

- Window expired → counter reset

## 8. Out of Scope

- Global rate limiting
- IP-based blocking

## 9. Architecture Notes

`NxFallbackLimiter` with ConcurrentHashMap state.

## 10. Wire Compatibility

No wire impact.

## 11. Dependencies

- e04s01

## 12. Testing Strategy

- Unit tests for rate limiting
- Concurrency tests
- Timeout verification

## 13. Observability

- `nx_rate_limit_exceeded`: destination
- `nx_nonce_replay`: destination

## 14. Configuration

None.

## 15. Error Handling

- `NxHandshakeError.RateLimited`

## 16. Naming Conventions

- `NxFallbackLimiter` class
- `NX_FALLBACK_TIMEOUT_MS = 10_000`

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: Rate limiting prevents DoS
  Given a destination peer
  When I attempt NX handshake 3 times within 1 minute
  Then the 4th attempt is rejected with RateLimited error
```

## 18. References

- `docs/decisions/crypto/nx-fallback-mitigation.md`