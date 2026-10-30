### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:40:07Z
- G: 
- A: 0.0.0
- X: file
---
I will produce a dense structured markdown summary of AiOversizeStrategy.java.

The file defines an enum `AiOversizeStrategy` in package `net.ladenthin.srcmorph.config`. It handles cases where a source file exceeds its routed model's context window. Four strategies are defined: FAIL, SAMPLE, MAP_REDUCE, and DETERMINISTIC. The default is FAIL. Each strategy is configured per routing rule via the `<onOversize>` string field.

The enum exposes a `fromConfig` method that parses the `<onOversize>` string. The public API includes the four strategy constants, the `DEFAULT` field, and the `configValue()` accessor.

The `fromConfig` method returns `DEFAULT` for null or blank input, and throws `IllegalArgumentException` for unrecognized non-blank values. The enum is immutable and thread-safe by nature. It depends on `org.jspecify.annotations.Nullable`.

FAIL aborts the build. SAMPLE trims the source to the window head. MAP_REDUCE splits the file into chunks, summarizes each, and combines the results. DETERMINISTIC emits a model-free body built from metadata plus a head and tail sample. DEFAULT is the fallback strategy when none is configured.> Selects how a routing rule treats a source file larger than its routed model's context window.

#### Purpose
- Enumerates oversize handling for AI source routing: abort, head-sample, map-reduce, or model-free deterministic output.
- Parses the per-rule `<onOversize>` config token into an `AiOversizeStrategy`.

#### Type
- `public enum AiOversizeStrategy` in `net.ladenthin.srcmorph.config`.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- Private constructor; `configValue` is `private final String`.

#### Input
- Constructor: `configValue` token (`fail`, `sample`, `mapReduce`, `deterministic`).
- `fromConfig`: `@Nullable String` `<onOversize>` value.

#### Output
- `configValue()` returns the config token.
- `fromConfig` returns a matching `AiOversizeStrategy`, or `DEFAULT` (`FAIL`) for null/blank.
- Unknown non-blank values throw `IllegalArgumentException`; no other state mutation.

#### Core logic
- `FAIL`: abort the build; user must route oversized files to a larger-context model. This is the default.
- `SAMPLE`: trim the source to the window and summarize the head in one call.
- `MAP_REDUCE`: split at line boundaries into window-sized chunks, summarize each, then combine partial summaries; `maxChunks` bounds time.
- `DETERMINISTIC`: no model; emit size, line count, and a head/tail sample.
- `fromConfig` trims the value, matches `configValue` case-insensitively via `values()`, and rejects unknown tokens.

#### Public API
- `FAIL` — abort build (default)
- `SAMPLE` — single-call head summary
- `MAP_REDUCE` — chunk, summarize, combine
- `DETERMINISTIC` — model-free metadata body
- `DEFAULT` -> `AiOversizeStrategy` — unconfigured strategy (`FAIL`)
- `configValue()` -> `String` — `<onOversize>` config token
- `fromConfig(String)` -> `AiOversizeStrategy` — parse config token

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig`: `null` or blank (after trim) yields `DEFAULT`.
- Non-blank unmatched value throws `IllegalArgumentException` naming `fail`/`sample`/`mapReduce`/`deterministic`.

#### Concurrency
- Immutable enum constants and a `final` token; safe to share across threads.
