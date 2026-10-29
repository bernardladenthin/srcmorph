### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:43:48Z
- G: 
- A: 0.0.0
- X: file
---
> Configures a single AI generation run with model, sampling, prompt, provider, and safety defaults for srcmorph indexing.

#### Purpose
- Stores all mutable settings for one AI generation step.
- Bridges Maven/plugin configuration to AI provider parameter building.

#### Type
- Kind: class
- Modifiers: public
- Annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@ToString`
- Extends: none
- Implements: none
- Key state: mutable JavaBean fields for model path, context, sampling, batching, KV-cache, provider settings, DRY controls, stop strings, calibration

#### Input
- Constructor: `AiGenerationConfig()`
- Bean setters accept primitive scalars, `String`, `Boolean`, `@Nullable` values, and `List<String>`
- Default values come from static `DEFAULT_*` constants computed from runtime CPU cores and model defaults
- No file/stream/config reading in this class itself; values are passed in by callers and stored

#### Output
- Getter methods return the configured values and unmodifiable list views
- Mutates instance state via setters
- `@ToString` generates a readable diagnostic string for logging
- No file writes, network calls, or external side effects

#### Core logic
- Defines a full default configuration surface for a single generation request.
- Maintains mutable fields for model invocation: context size, output tokens, temperature, threads, prompt trim limits, top-p/top-k/min-p, repetition controls, cache settings, GPU and batching flags, and provider details.
- Normalizes null setter inputs to default values or empty lists.
- Exposes list state via `Collections.unmodifiableList(...)` to prevent external mutation through getters.
- Keeps a per-machine `AiCalibration` hook for timing-based tuning.
- Uses Lombok-generated `toString()` for configuration diagnostics; no equality/hashCode generation by design.

#### Public API
- `AiGenerationConfig() -> AiGenerationConfig` + constructor
- `getModelPath..setCalibration (90 total)` + bean getters/setters for all configuration fields

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions declared
- Null arguments are intentionally handled by resetting to defaults or empty collections
- Field values are mutable and not validated here; correctness depends on caller-supplied ranges and provider semantics
- `@Nullable` is used for optional fields such as `modelPath`, `chatTemplateEnableThinking`, and calibration

#### Concurrency
- Mutable, non-thread-safe JavaBean configuration
- Intended for single-threaded configuration assembly and runtime parameter plumbing
- Getter list views are read-only, but instance state is not synchronized or immutable
