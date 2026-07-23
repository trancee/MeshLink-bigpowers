# Story e10s01: Power Tier Enum and Settings
**Epic:** e10 · **BCPs:** 3 · **Status:** todo

## 1. Business Narrative

Power awareness requires discrete tiers controlling scan/advertisement/connection behavior.

## 2. Stakeholders

- **Driver:** Platform team
- **Affected:** All MeshLink developers

## 3. Problem Statement

Create PowerTier configuration:
1. HIGH, MEDIUM, LOW, OFF enum values
2. Scan percentage, advertisement interval, connection interval
3. Concurrent peer limit

## 4. Goal

Configurable power-aware operation.

## 5. Happy Path

```kotlin
val config = MeshLinkConfig(powerTier = PowerTier.HIGH)
assertEquals(20, config.powerSettings.scanPercentage)
assertEquals(100, config.powerSettings.advertisementIntervalMs)
assertEquals(7, config.powerSettings.connectionInterval)
```

## 6. Key Constraints

- HIGH: 20% scan, 100ms adv, 7.5ms conn, 8 concurrent
- MEDIUM: 10% scan, 500ms adv, 15ms conn, 4 concurrent (default)
- LOW: 5% scan, 1000ms adv, 30ms conn, 2 concurrent
- OFF: No background activity

## 7. Alternative Flows

- Battery low → suggest LOW tier

## 8. Out of Scope

- Automatic tier switching

## 9. Architecture Notes

PowerSettings data class, PowerTier enum.

## 10. Wire Compatibility

None.

## 11. Dependencies

- None

## 12. Testing Strategy

- Each tier value tests
- Concurrent limit tests
- Battery saver detection tests

## 13. Observability

- `power_tier_changed`: old, new
- `battery_saver_detected`: suggestion

## 14. Configuration

In MeshLinkConfig DSL.

## 15. Error Handling

- Invalid tier → use default

## 16. Naming Conventions

- `PowerTier` enum
- `PowerSettings` data class

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: High power tier enables aggressive scanning
  Given HIGH power tier
  When I check power settings
  Then scan percentage is 20
  And advertisement interval is 100ms
```

## 18. References

- `docs/decisions/power/power-tier-behavior.md`