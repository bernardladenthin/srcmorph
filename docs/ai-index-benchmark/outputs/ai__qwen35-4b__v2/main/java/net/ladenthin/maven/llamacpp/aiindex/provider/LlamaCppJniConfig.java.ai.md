### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:58:46Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Immutable configuration record for llama.cpp JNI provider settings including library paths, model selection, sampling parameters, and chat template options.

#### Purpose
- Encapsulates all runtime configuration parameters for the LlamaCppJniProvider.
- Provides immutable accessors for native library paths, model files, and inference tuning values.

#### Type
Record (final class) with Lombok annotations; extends none; implements none; key generics: List<String>.

#### Input
Constructor accepts: libraryPath, modelPath, contextSize, maxOutputTokens, temperature, threads, topP, topK, repeatPenalty, chatTemplateEnableThinking, stopStrings.

#### Output
Returns field values via getters; returns unmodifiable List<String> for stopStrings; no external resources written.

#### Core logic
- Validates that modelPath is not null during construction.
- Converts null stopStrings to an empty list via Collections.emptyList().
- Stores all parameters as final fields to ensure immutability.

#### Public API
libraryPath() -> String <= native library path
modelPath() -> String <= GGUF model file path
contextSize() -> int <= context window size
maxOutputTokens() -> int <= max output tokens
temperature() -> float <= sampling temperature
threads() -> int <= CPU thread count
topP() -> float <= nucleus-sampling threshold
topK() -> int <= top-k limit
repeatPenalty() -> float <= repetition penalty
chatTemplateEnableThinking() -> boolean <= thinking mode enabled
stopStrings() -> List<String> <= unmodifiable stop strings

#### Dependencies
net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord, java.util.Collections, java.util.List, java.util.Objects, lombok.EqualsAndHashCode, lombok.ToString.

#### Exceptions / Errors
Throws NullPointerException if modelPath is null; otherwise no exceptions thrown.

#### Concurrency
Thread-safe due to immutability and final fields; no synchronization required.
