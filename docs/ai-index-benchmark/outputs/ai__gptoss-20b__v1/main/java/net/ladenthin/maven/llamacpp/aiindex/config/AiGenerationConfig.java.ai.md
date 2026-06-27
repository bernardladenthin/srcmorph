### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T21:07:32Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> A mutable configuration holder for AI generation parameters, used to transfer settings from Maven to AI providers.

#### Purpose
- Stores AI generation options (model path, sampling, retries, input limits).
- Acts as a JavaBean populated by Maven plugin.

#### Type
- Class `AiGenerationConfig` (public), final by default, annotated `@ToString` (Lombok), no additional modifiers.

#### Input
- Constructor (no parameters).
- Setters: `setModelPath`, `setContextSize`, `setMaxOutputTokens`, `setTemperature`, `setThreads`, `setCharsPerToken`, `setMaxInputChars`, `setWarnOnTrim`, `setMaxRetries`, `setRetryTemperatureIncrement`, `setTopP`, `setTopK`, `setRepeatPenalty`, `setChatTemplateEnableThinking`, `setStopStrings`.
- Defaults initialized in field declarations.

#### Output
- Getters: `getModelPath`, `getContextSize`, `getMaxOutputTokens`, `getTemperature`, `getThreads`, `getCharsPerToken`, `getMaxInputChars`, `isWarnOnTrim`, `getMaxRetries`, `getRetryTemperatureIncrement`, `getTopP`, `getTopK`, `getRepeatPenalty`, `isChatTemplateEnableThinking`, `getStopStrings`.
- Unmodifiable view of stop strings.

#### Core logic
- Constants define default values for context, output tokens, temperature, threads, input chars, etc.
- Fields hold mutable state initialized to defaults.
- `setStopStrings` replaces internal list or clears to empty.
- `getStopStrings` returns `Collections.unmodifiableList` or `null` if internal list unset.
- No additional computation or algorithm.

#### Public API
- `AiGenerationConfig()` → construct empty config.
- `getModelPath() -> String` → model file path.
- `setModelPath(String)` → set model path.
- `getContextSize() -> int` → context window size.
- `setContextSize(int)` → set context size.
- `getMaxOutputTokens() -> int` → max output tokens.
- `setMaxOutputTokens(int)` → set max output tokens.
- `getTemperature() -> float` → sampling temperature.
- `setTemperature(float)` → set temperature.
- `getThreads() -> int` → CPU thread count.
- `setThreads(int)` → set thread count.
- `getCharsPerToken() -> int` → chars per token ratio.
- `setCharsPerToken(int)` → set ratio.
- `getMaxInputChars() -> int` → max input chars.
- `setMaxInputChars(int)` → set max input chars.
- `isWarnOnTrim() -> boolean` → trim warning flag.
- `setWarnOnTrim(boolean)` → set trim warning.
- `getMaxRetries() -> int` → max retry attempts.
- `setMaxRetries(int)` → set max retries.
- `getRetryTemperatureIncrement() -> float` → retry temperature increment.
- `setRetryTemperatureIncrement(float)` → set increment.
- `getTopP() -> float` → nucleus sampling threshold.
- `setTopP(float)` → set top‑p.
- `getTopK() -> int` → top‑k limit.
- `setTopK(int)` → set top‑k.
- `getRepeatPenalty() -> float` → repetition penalty.
- `setRepeatPenalty(float)` → set penalty.
- `isChatTemplateEnableThinking() -> boolean` → chat‑template thinking flag.
- `setChatTemplateEnableThinking(boolean)` → set flag.
- `getStopStrings() -> List<String>` → unmodifiable stop strings.
- `setStopStrings(List<String>)` → set stop strings.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`.
- `lombok.ToString`.
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- No checked or unchecked exceptions thrown.

#### Concurrency
- Class is not thread‑safe; state changes via setters. No synchronization.
