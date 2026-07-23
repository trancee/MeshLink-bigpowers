# MeshLink Technical Architecture

## Tech Stack

| Layer | Technology | Notes |
|-------|------------|-------|
| Language | Kotlin Multiplatform | JVM + Android + iOS targets |
| Async | kotlinx-coroutines-core | Only permitted runtime dependency |
| Build | Gradle (KTS) | Using Detekt, ktfmt, Kover, BCV plugins |
| Wire Format | FlatBuffers (pure Kotlin) | See `docs/explanation/why-pure-kotlin-flatbuffers.md` |
| Crypto Primitives | X25519, Ed25519, ChaCha20-Poly1305, HKDF-SHA256 | Validated against Wycheproof |
| Transport | BLE (GATT + L2CAP CoC) | GATT always available, CoC preferred for data |
| State Machine | Finite states with sealed error hierarchy | See `docs/explanation/peer-lifecycle.md` |

## Architecture Layers

### 1. Public API (`MeshLink`)
- Single entry point, identical on both platforms
- Lifecycle: Uninitialized → Running → Paused/Stopped
- Event emissions for peer discovery/state changes
- Config DSL: `meshLinkConfig { }`

### 2. Security Layer (`crypto/`)
- **CryptoProvider** abstraction over platform crypto
- **NoiseHandshake** for XX (link) and IX (E2E) patterns
- **TrustStore** for TOFU pinning of peer identities
- **SignedIdentityGossip** for distributing public keys

### 3. Routing Layer (`routing/`)
- **RouteCoordinator** for route table management
- **RouteDigestTracker** for route table synchronization
- **Babel-inspired distance-vector** with feasibility condition
- Self-origin sequence numbers on cold start

### 4. Transfer Layer (`transfer/`)
- **ChunkedTransferSession** for large payloads
- **SACK-style scoreboard** for selective retransmission
- **TransferScheduler** for flow control

### 5. Platform Glue (`androidMain`, `iosMain`)
- `expect`/`actual` for BLE radio operations
- `expect`/`actual` for crypto provider selection
- Platform lifecycle integration

## Module Boundaries

```
meshlink/
  src/
    commonMain/
      api/          → Public API surface
      crypto/       → Security primitives (Noise, Trust)
      routing/      → Route coordinator, digest tracking
      transfer/     → Chunked transfer, SACK
      model/        → Wire frames, data models
      internal/     → Implementation details (sealed from API)
    androidMain/    → Android BLE + crypto glue
    iosMain/        → iOS CoreBluetooth + crypto glue
```

## Cross-Platform Contracts

- **Public API shape** identical on both platforms (III)
- **Error hierarchy** sealed in commonMain (III)
- **Diagnostic events** shared catalog with severity tiers (III)
- **Wire format** must encode/decode identically on both platforms

## Testing Strategy

| Test Type | Target | Purpose |
|-----------|--------|---------|
| Unit | JVM | Logic verification, coverage measurement |
| Integration | JVM | Multi-node virtual harness |
| API Tests | Android + iOS | Public API consumption (reference app) |
| Proof Tests | Android (real hardware) | Crypto provider, BLE behavior |
| Benchmarks | JVM + real devices | Performance budget validation |

## Design Constraints

- No server connectivity required (zero-infrastructure)
- Min platforms: Android API 26, iOS 14
- One runtime dependency allowed in shipped artifact (`kotlinx-coroutines-core`)
- All crypto via single provider abstraction
- 100% coverage gate only applies to `meshlink` module