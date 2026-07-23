# MeshLink — AI Agents

Read CONVENTIONS.md before any GitHub or git operation.

## Project
A library-first SDK enabling encrypted, serverless, fully offline peer-to-peer messaging between mobile devices over a short-range radio mesh — no internet, no backend, no user accounts. Two independent mobile platforms must be fully interoperable and behaviorally identical from a developer's perspective.

## Stack
Kotlin Multiplatform (JVM + Android + iOS) with Gradle, Coroutines, FlatBuffers

## Commands
| Action | Command |
|--------|---------|
| Run | `./gradlew :meshlink:run` |
| Test | `./gradlew :meshlink:test` |
| Build | `./gradlew :meshlink:build` |
| Lint | `./gradlew :meshlink:detekt` |
| Format | `./gradlew :meshlink:ktfmtFormat` |
| Typecheck | `./gradlew :meshlink:compileKotlin` |
| Preflight | `./gradlew :meshlink:test && ./gradlew :meshlink:detekt && ./gradlew :meshlink:build` |
| CI | `gh pr checks` (when a PR is open) |

## Architecture
MeshLink is split into four Gradle modules:
- `meshlink` — shipped library with public API
- `meshlink-reference` — reference app consuming public API
- `meshlink-proof` — real device validation (Android + iOS)
- `meshlink-benchmark` — performance validation

Shared logic lives in `commonMain`; platform glue in `androidMain`/`iosMain`.

## Conventions
- Use `Noise NX` fallback with `KeyHash` verification for unknown destination keys
- `KeyHash` = 12-byte truncated SHA-256 of public key (from discovery)
- `CryptoKey` = 32-byte full cryptographic key
- Adaptive grace periods for peer lifecycle (based on peer stability)
- Cross-platform error handling via sealed exception hierarchies

## Never
- Never dismiss reproducible gate failures as pre-existing or out of scope
- Never proceed on red Preflight or red CI — invoke quick-fix or fix-bug first
- Never use `ByteArray` for key material — use `KeyHash` or `CryptoKey` types
- Never leak platform exceptions to public API — always wrap

## Agent Rules
- **Workflow Mandate:** You MUST use the bigpowers skills to perform tasks
- **Always Green:** Preflight and CI must be green before forward work
- Read specs/ before writing code
- All planning and specifications MUST be written to `specs/` before any code is generated
- Use TDD (red-green-refactor) for all implementation
- One clarifying question beats a wrong assumption baked into 200 lines