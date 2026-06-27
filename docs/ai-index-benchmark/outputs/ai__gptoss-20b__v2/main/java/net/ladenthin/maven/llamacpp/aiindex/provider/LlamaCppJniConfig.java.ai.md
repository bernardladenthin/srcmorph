### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:16:12Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider parameters.

#### Purpose
- Holds immutable settings for llama.cpp JNI invocation.

#### Type
- public final class LlamaCppJniConfig; annotations: @ConvertToRecord, @ToString, @EqualsAndHashCode.

#### Input
- Constructor params: String libraryPath, String modelPath, int contextSize, int maxOutputTokens, float temperature, int threads, float topP, int topK, float repeatPenalty, boolean chatTemplateEnableThinking, List<String> stopStrings.
- modelPath required, stopStrings may be null.

#### Output
- Accessors return field values; stopStrings() returns unmodifiable view.
- No side effects.

#### Core logic
- Validate modelPath non‑null.
- Assign all fields; default empty list for null stopStrings.
- Provide record‑style getters.

#### Public API
- `LlamaCppJniConfig(String, String, int, int, float, int, float, int, float, boolean, List<String>) -> LlamaCppJniConfig` creates immutable config.
- `String libraryPath() -> String` native library path or null.
- `String modelPath() -> String` GGUF model path.
- `int contextSize() -> int` token window size.
- `int maxOutputTokens() -> int` max output per call.
- `float temperature() -> float` sampling temperature.
- `int threads() -> int` CPU thread count.
- `float topP() -> float` nucleus‑sampling threshold.
- `int topK() -> int` top‑k sampling limit.
- `float repeatPenalty() -> float` repetition penalty.
- `boolean chatTemplateEnableThinking() -> boolean` chat‑template thinking flag.
- `List<String> stopStrings() -> List<String>` unmodifiable stop‑strings list.

#### Dependencies
- java.util.Collections, java.util.List, java.util.Objects
- lombok.EqualsAndHashCode, lombok.ToString
- net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord

#### Exceptions / Errors
- NullPointerException if modelPath is null.

#### Concurrency
- Class is immutable; thread‑safe.
