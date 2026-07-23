# Cross-Platform Error Handling

## Status: Proposed

## Context

The constitution requires "Errors use sealed exception hierarchies in `commonMain`. Platform exceptions get wrapped and MUST NOT leak to consumers."

Currently, the MeshLink template doesn't define how platform-specific errors are handled and wrapped.

## Decision: Platform Exception Wrapping Strategy

### Error Hierarchy Design

All errors are sealed hierarchies in `commonMain`:

```kotlin
// commonMain/kotlin/ch/trancee/meshlink/error/MeshLinkException.kt
sealed class MeshLinkException(message: String) : Exception(message)

sealed class MeshLinkError : MeshLinkException(message) {
  abstract val code: ErrorCode
}

// Public API errors
class PeerNotFoundError(peerId: PeerId) : MeshLinkError()
class TransferTimeoutError(duration: Duration) : MeshLinkError()
class TrustError(message: String) : MeshLinkError()

// Internal errors (wrapped from platform)
internal class AndroidBleException(
  cause: Throwable,
  val bleErrorCode: Int? = null
) : MeshLinkException("Android BLE error: ${cause.message}", cause)

internal class IosBluetoothException(
  cause: NSError,
  val bluetoothErrorCode: Int? = null
) : MeshLinkException("iOS Bluetooth error: ${cause.localizedDescription}", cause)
```

### Platform Exception Wrapping

#### Android

```kotlin
// androidMain/kotlin/ch/trancee/meshlink/platform/PlatformErrorWrapper.kt
class AndroidErrorWrapper {
  fun wrap(throwable: Throwable): MeshLinkError = when (throwable) {
    is SecurityException -> {
      diagnostics.security("BLE permission denied")
      TrustError("Permission denied for BLE")
    }
    is IllegalArgumentException -> {
      diagnostics.error("Invalid BLE parameter: ${throwable.message}")
      TransferConfigurationError(throwable.message ?: "Invalid parameter")
    }
    is BluetoothDisabledException -> {
      diagnostics.error("Bluetooth disabled")
      BluetoothStateError("Bluetooth is disabled")
    }
    else -> {
      diagnostics.error("Unhandled Android error: ${throwable::class}")
      InternalError("Unexpected error: ${throwable.message}")
    }
  }
}
```

#### iOS

```swift
// iosMain/swift/PlatformErrorWrapper.swift
class IosErrorWrapper {
  func wrap(_ error: NSError) -> MeshLinkError {
    switch error.code {
    case CBErrorBluetoothUnauthorized.rawValue:
      diagnostics.security("Bluetooth unauthorized")
      return TrustError("Bluetooth permission denied")
    case CBErrorConnectionTimeout.rawValue:
      return TransferTimeoutError(duration: timeout)
    case CBErrorInvalidParameters.rawValue:
      return TransferConfigurationError(error.localizedDescription)
    default:
      diagnostics.error("Unhandled iOS error: \(error)")
      return InternalError("Unexpected error: \(error.localizedDescription)")
    }
  }
}
```

### Exception Translation Rules

1. **Never leak platform types to public API**
   - All public functions return `Result<T>` or throw `MeshLinkException`
   - Platform exceptions are caught and wrapped immediately

2. **Preserve root cause for diagnostics**
   - Chain exceptions: `MeshLinkException("message", cause)`
   - Log original platform exception for debugging

3. **Map to stable error codes**
   - Use sealed `ErrorCode` enum for programmatic handling
   - Avoid string matching on exception messages

4. **Handle nullability gracefully**
   - Platform APIs may return null instead of throwing
   - Convert to appropriate `MeshLinkError`

### Error Codes

```kotlin
enum class ErrorCode {
  // Trust/Identity
  PEER_NOT_FOUND,
  KEY_UNKNOWN,
  TRUST_VIOLATION,
  
  // Transfer
  TRANSFER_TIMEOUT,
  TRANSFER_CANCELLED,
  TRANSFER_CORRUPTED,
  
  // BLE/Transport
  BLUETOOTH_DISABLED,
  CONNECTION_FAILED,
  CONNECTION_TIMEOUT,
  COC_NOT_SUPPORTED,
  
  // Configuration
  INVALID_PARAMETER,
  OUT_OF_MEMORY,
  
  // Internal
  INTERNAL_ERROR
}
```

### Testing Requirements

- `ErrorWrappingTest`: verify all platform exceptions are wrapped
- `ErrorCodeMappingTest`: verify correct error codes assigned
- `ExceptionChainingTest`: verify root cause preserved
- `PublicApiBoundaryTest`: verify no platform types leak

### Diagnostic Contract

Per error event:
```yaml
error:
  type: "MeshLinkError"
  code: "TRANSFER_TIMEOUT"
  message: "Transfer timed out after 30s"
  cause: "java.util.TimeoutException: ..."
  peer_id: "<peer-hash>"
```

## Related

- `CONSTITUTION.md` §I Code Quality (errors must not leak)
- `docs/explanation/peer-lifecycle.md` (error events)
- RFC 8439 (ChaCha20-Poly1305 error handling)