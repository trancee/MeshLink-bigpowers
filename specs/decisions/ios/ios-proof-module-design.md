# iOS Proof Module Design

## Status: Proposed

## Context

The `meshlink-proof` module currently validates real-device behavior only on Android. The constitution requires iOS simulator tests but explicitly prohibits them for BLE behavior (see Principle II). This creates an asymmetric testing risk: iOS platform glue is the only platform without real-device proof coverage.

## Decision: Extend meshlink-proof for iOS Real-Device Validation

### Problem Statement

| Platform | Real-Device Proof | Simulator Proof | Gap |
|----------|-------------------|-----------------|-----|
| Android | ✅ Yes | N/A | None |
| iOS | ❌ No | ✅ Yes (but invalid for BLE) | **Critical gap** |

The constitution states: "Android emulators and the iOS simulator do not implement real BLE radios. Never add an emulator/simulator test target as coverage for actual BLE behavior."

Yet the current design has no iOS real-device proof module.

### Proposed Solution: Add iOS Proof Target to meshlink-proof

### Module Structure Change

```
meshlink-proof/
├── android/
│   ├── src/
│   │   ├── main/          # Android-specific harness
│   │   └── test/          # Real device tests
├── ios/
│   ├── src/
│   │   ├── main/          # iOS-specific harness (Swift/Objective-C++)
│   │   └── test/          # XCTest targets for real devices
│   └── exported/          # Public headers for KMP interop
└── common/
    └── src/
        └── main/          # Shared test vectors and harnesses
```

### iOS-Specific Validation

The iOS proof module tests:

1. **Crypto provider selection** on real iOS hardware
   - Verify Security framework keys used correctly
   - Fallback path behavior (when available)
   
2. **BLE connection behavior** via CoreBluetooth
   - GATT write/notify timing (matches `optimize-ble-throughput` references)
   - L2CAP CoC establishment on iOS 11+
   - Background mode handling during transfers

3. **Platform integration edge cases**
   - App termination during transfer
   - Bluetooth state transitions (powered off/unauthorized)
   - Memory warnings during large transfers

### Test Harness Interface

Shared Kotlin interface for cross-platform test assertions:

```kotlin
// common/src/commonMain/kotlin/ProofHarness.kt
interface ProofHarness {
  suspend fun verifyCryptoProviderSelected(peerId: PeerId): CryptoProviderType
  suspend fun measureGattRoundTrip(timeMs: Long): Boolean
  suspend fun verifyL2capCoCSupported(): Boolean
  suspend fun measureConnectionInterval(realIntervalMs: Long): Boolean
}

// ios/src/main/swift/IosProofHarness.swift
class IosProofHarness: ProofHarness {
  func verifyCryptoProviderSelected(_ peerId: String) -> CryptoProviderType {
    // Verify Keychain or Secure Enclave used for key operations
    // iOS 14+ supports Secure Enclave for ECDH
    if #available(iOS 14, *) {
      return .securityFrameworkWithSecureEnclave
    } else {
      return .securityFramework
    }
  }
}
```

### CI Integration

GitHub Actions matrix expansion:

```yaml
strategy:
  matrix:
    runner: [macos-15, ubuntu-22.04]
    device: [iphone-15, pixel-7]
    target: [android-device, ios-device]

jobs:
  proof-tests:
    runs-on: ${{ matrix.runner }}
    steps:
      - name: Run iOS proof tests
        if: matrix.target == 'ios-device'
        uses: test-engine-ios.action@v1
        with:
          scheme: meshlink-proof-ios
          destination: 'platform=iOS Simulator,name=iPhone 15,OS=latest'
          # Note: For true BLE proof, requires physical device runner
```

**Critical note:** iOS proof tests require a physical device runner (macOS with connected iPhone) or cloud device testing service. Simulator tests are acceptable for non-BLE logic but MUST NOT substitute for BLE proof.

### Test Cases to Port from Android

| Android Proof Test | iOS Equivalent | Notes |
|-------------------|---------------|-------|
| `CryptoProviderSelectionTest` | `IosCryptoProviderTest` | Verify Security framework + Secure Enclave |
| `BleThroughputBenchmark` | `CoreBluetoothThroughputTest` | 15-20ms floor per references |
| `CoCFallbackTest` | `L2capCoCFallbackTest` | iOS 11+ support verified |
| `BackgroundTransferTest` | `IosBackgroundTransferTest` | Background modes enabled |

### Risk Mitigation

Adding iOS proof testing removes:
- **Asymmetric security risk:** iOS platform glue now tested on real hardware
- **Asymmetric reliability risk:** BLE behavior differences validated
- **Coverage gap in proof story:** Constitution requirement met

### Trade-offs

| Trade-off | Analysis |
|-----------|----------|
| Module complexity | Worth it - removes critical gap |
| CI infrastructure | Required for full validation anyway |
| Swift/Objective-C++ needed | iOS test harness requires native code |
| Test duplication | Some overlap with reference module, but different scope |
| Physical device requirement | Must use cloud testing or have device lab |

## Testing Strategy

### Unit Tests (JVM)
- Crypto primitives validation
- Wire format encoding/decoding
- Routing logic

### Integration Tests (Device)
- Real BLE connections
- Multi-hop routing
- Transfer sessions

### Performance Tests (Device)
- Throughput benchmarks
- Latency measurements
- Memory usage

## Related

- `CONSTITUTION.md` §II Testing Standards
- `docs/decisions/crypto/android-crypto-fallback-proof.md`
- `.agents/skills/optimize-ble-throughput/references/mobile-platforms.md`
- `docs/explanation/module-structure.md`
- RFC 8439 (ChaCha20-Poly1305 test vectors)