# Story e09s03: BLE Behavior Tests
**Epic:** e09 · **BCPs:** 4 · **Status:** todo

## 1. Business Narrative

Real iOS BLE behavior needs validation for L2CAP CoC and connection parameters.

## 2. Stakeholders

- **Driver:** Platform validation team
- **Affected:** iOS developers

## 3. Problem Statement

Create iOS BLE behavior tests:
1. L2CAP CoC connection
2. Connection interval negotiation
3. RSSI reading accuracy

## 4. Goal

Validated iOS BLE integration.

## 5. Happy Path

```swift
func testL2capConnection() {
  let peripheral = connectToPeripheral()
  XCTAssertNotNil(peripheral.l2capChannel)
}
```

## 6. Key Constraints

- Uses iOS device (not simulator)
- XCTest framework
- Bluetooth permission

## 7. Alternative Flows

- Simulator → skip gracefully

## 8. Out of Scope

- Android BLE tests (e09s03)

## 9. Architecture Notes

Test in meshlink-proof-ios module.

## 10. Wire Compatibility

None.

## 11. Dependencies

- e09s01, e09s02

## 12. Testing Strategy

- XCTest on real device
- Parameterized for different intervals

## 13. Observability

- `ble_connection_established`: peripheral
- `l2cap_channel_ready`: mtu

## 14. Configuration

None.

## 15. Error Handling

- XCTSkipIf for simulator

## 16. Naming Conventions

- XCTest naming conventions

## 17. Acceptance Criteria (Gherkin)

```gherkin
Scenario: L2CAP CoC connects successfully
  Given a peripheral advertising CoC support
  When I connect and open L2CAP channel
  Then the channel is ready with MTU >= 256
```

## 18. References

- `docs/decisions/ios/ios-proof-module-design.md`