# MeshLink Specification

**Version:** 1.0.0  
**Status:** Draft  
**Based on:** MeshLink-template v1.18.0

## Vision

A library-first SDK enabling encrypted, serverless, fully offline peer-to-peer messaging between mobile devices over a short-range radio mesh — no internet, no backend, no user accounts. Two independent mobile platforms must be fully interoperable and behaviorally identical from a developer's perspective.

## Product Pillars

1. **Zero-infrastructure trust** — Trust On First Use (TOFU) authentication
2. **Two-layer encryption** — Hop-by-hop + end-to-end using Noise Protocol Framework
3. **Proactive multi-hop routing** — Babel-inspired distance-vector routing
4. **Reliable large-payload transfer** — Chunked transfer with selective ACK over BLE
5. **Power-aware operation** — Discrete power tiers governing scan/connection behavior
6. **Deterministic cross-platform parity** — Identical public API on Android and iOS

## Module Structure

| Module | Purpose | Quality Gates |
|--------|---------|---------------|
| `meshlink` | Shipped library with public API | 100% line/branch coverage, Dokka/SKIE docs |
| `meshlink-reference` | Reference app consuming public API only | UI integration tests |
| `meshlink-proof` | Real Android and iOS hardware validation | Proof-of-correctness tests |
| `meshlink-benchmark` | Performance validation | Benchmark gates |

## Technical Stack

- Kotlin Multiplatform (JVM + Android + iOS)
- Coroutines for async operations
- FlatBuffers for wire encoding (pure Kotlin)
- Gradle build system with Detekt/ktfmt/Kover/BCV

## Key Decisions (Locked)

### Transport
- **GATT** = always-available control plane (handshake, routing, transfer control)
- **L2CAP CoC** = preferred data plane (bulk payload), GATT fallback
- See `docs/decisions/transport/gatt-l2cap-transport-selection.md`

### Crypto/Handshake
- **Link layer:** Noise XX for first-contact mutual authentication
- **End-to-end:** Noise IX for destination-knowledge asymmetry
- **Fallback:** Noise NX with PeerKey verification when destination key unknown
- **Future:** Noise IK for post-TOFU reconnect optimization
- See `docs/decisions/crypto/e2e-handshake-pattern.md`

### Routing
- Destination-self-reported seqno on cold start (not per-reconnect)
- Hello/IHU frames removed (BLE connection is liveness signal)
- Digest mismatch triggers full-table push
- Link quality metric using RSSI normalization + platform flags
- See `docs/decisions/routing/destination-sourced-seqno-ihu-removal-digest-resync-design.md`

### Peer Lifecycle
- CONNECTED → DISCONNECTED (adaptive grace period) → GONE
- Grace period adapts based on peer stability and power tier
- Public API exposes only CONNECTED/DISCONNECTED states
- See `docs/explanation/peer-lifecycle.md`

### Key Rotation
- Triggered by timer, manual API, or security event
- **Configurable sub-object:** `keyRotation { interval, gracePeriod }`
- Seqno resets to 1 (new identity)
- Signed announcement with PeerKey verification
- See `docs/decisions/crypto/key-rotation-protocol.md`

### Power Tiers
- **HIGH:** 20% scan, 100ms adv, 7.5ms conn, 8 concurrent
- **MEDIUM:** 10% scan, 500ms adv, 15ms conn, 4 concurrent (default)
- **LOW:** 5% scan, 1000ms adv, 30ms conn, 2 concurrent
- **OFF:** No background activity
- Adaptive grace period based on peer history
- See `docs/decisions/power/power-tier-behavior.md`

## Non-Functional Requirements

| Metric | Target | Validation |
|--------|--------|------------|
| Throughput (1-hop L2CAP) | ≥80 KB/s Android, ≥60 KB/s iOS | Benchmark |
| Latency (1-hop, 256B, p95) | <50 ms | Benchmark |
| Memory (steady state, 8 peers) | ≤8 MB heap | Benchmark |
| Cold start | <500 ms | Benchmark |
| Routing convergence | ≤3 s (10-node) | Benchmark |
| Wire codec (encode/decode) | <1 μs/msg | Benchmark |

## Quality Gates

- 100% line/branch coverage for `meshlink` module (Kover)
- Detekt zero suppressions
- ktfmt formatting applied
- BCV API compatibility checks
- Wycheproof validation for: ChaCha20-Poly1305, Ed25519, X25519, HKDF
- Benchmark check: regression >10% blocks merge
- Security scan (gitleaks) on every PR

## Constitution Anchors

All code must comply with `CONSTITUTION.md` principles:
- I. Rigorous Code Quality (crypto, explicit API, zero suppressions)
- II. Exhaustive Testing Standards (coverage, AAA pattern, no emulators for BLE)
- III. User Experience Consistency (cross-platform parity)
- IV. Performance Requirements (quantified budgets)
- V. Maintainable Design (composition over inheritance, small files)