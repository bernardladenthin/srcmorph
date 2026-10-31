### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:36:23Z
- G: 
- A: 0.0.0
- X: file
---
> Specifies how the srcmorph pipeline handles source files exceeding the routed AI model's context window: abort the build, sample the head, map-reduce over line-bounded chunks, or emit deterministic metadata.

#### Purpose
- Enum of oversize-handling strategies for source files routed to AI models in the srcmorph build
- Configured per routing rule via the `<onOversize>` token parsed by `fromConfig`

#### Type
- Enum (implicitly final, extends `java.lang.Enum<AiOversizeStrategy>`)
- Package `net.ladenthin.srcmorph.config`
- No explicit interfaces; parameter annotated with `@Nullable` from `org.jspecify.annotations`

#### Input
- Constructor: `String configValue` — the `<onOversize>` token (e.g. `"mapReduce"`)
- `fromConfig(@Nullable String value)` — raw config string from a routing rule; `null` or blank permitted

#### Output
- Enum constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`
- `DEFAULT` static field -> `AiOversizeStrategy` (value `FAIL`)
- `configValue()` -> `String` — the config token
- `fromConfig(...)` -> `AiOversizeStrategy` — parsed strategy
- Throws `IllegalArgumentException` on unrecognized non-blank input

#### Core logic
- `FAIL` (default): aborts the build; user must manually route oversized files to a larger-context model
- `SAMPLE`: trims source to the window and summarizes only the head in a single model call
- `MAP_REDUCE`: splits file into window-sized chunks at line boundaries, summarizes each chunk, then combines partial summaries in one final call, bounded by a `maxChunks` cap
- `DETERMINISTIC`: no model call; emits a deterministic body with size, line count, and head/tail sample
- `fromConfig`: returns `DEFAULT` for `null` or blank; case-insensitive match against each constant's `configValue`; throws `IllegalArgumentException` listing valid tokens (`fail/sample/mapReduce/deterministic`) otherwise

#### Public API
- `FAIL` -> `AiOversizeStrategy` — Abort build on oversize (default)
- `SAMPLE` -> `AiOversizeStrategy` — Summarize trimmed head only
- `MAP_REDUCE` -> `AiOversizeStrategy` — Chunk at lines and combine summaries
- `DETERMINISTIC` -> `AiOversizeStrategy` — Emit metadata without model call
- `DEFAULT` -> `AiOversizeStrategy` — Strategy when none configured
- `configValue()` -> `String` — Returns `<onOversize>` config token
- `fromConfig(@Nullable String value)` -> `AiOversizeStrategy` — Parses config token case-insensitively

#### Dependencies
- `org.jspecify.annotations.Nullable`
- `java.lang.Enum` (implicit superclass)

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for non-blank values matching no strategy; message includes the invalid value and the expected token list
- `null` and blank/empty strings are handled gracefully, returning `DEFAULT` (`FAIL`)

#### Concurrency
- Enum constants and `DEFAULT` are immutable, stateless, and thread-safe
- No mutable instance fields; safe for concurrent access across routing rules
