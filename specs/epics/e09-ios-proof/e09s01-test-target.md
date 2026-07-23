# Story e09s01: XCTest Target Setup
**Epic:** e09 · **BCPs:** 3 · **Status:** todo

## 1. Business Narrative

The iOS proof harness needs XCTest target configuration to run real-device tests.

## 5. Happy Path

XCTest scheme builds and runs on iOS device.

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: XCTest target builds
  Given an iOS project with meshlink-proof-ios target
  When I build the scheme
  Then it compiles successfully
```

## 18. References

- `specs/decisions/ios/ios-proof-module-design.md`