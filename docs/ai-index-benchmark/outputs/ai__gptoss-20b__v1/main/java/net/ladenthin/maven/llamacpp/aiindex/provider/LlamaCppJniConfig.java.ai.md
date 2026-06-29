### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T21:25:29Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Immutable config holder for llama.cpp JNI provider parameters.  

#### Purpose
- Supplies configuration to llama.cpp JNI provider  
- Defines model, inference, and sampling options  

#### Type
- `final class LlamaCppJniConfig`  
- Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`  
- Fields: `libraryPath`, `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `topP`, `topK`, `repeatPenalty`, `chatTemplateEnableThinking`, `stopStrings`  

#### Input
- Constructor parameters:  
  - `String libraryPath` (native lib path, may be null)  
  - `String modelPath` (GGUF model file, non‑null)  
  - `int contextSize` (context window tokens)  
  - `int maxOutputTokens` (max tokens per call)  
  - `float temperature` (sampling temperature)  
  - `int threads` (CPU threads)  
  - `float topP` (nucleus threshold)  
  - `int topK` (top‑k limit)  
  - `float repeatPenalty` (repetition penalty)  
  - `boolean chatTemplateEnableThinking` (chat‑template mode)  
  - `List<String> stopStrings` (may be null → empty list)  

#### Output
- Getter methods expose stored values:  
  - `libraryPath()`  
  - `modelPath()`  
  - `contextSize()`  
  - `maxOutputTokens()`  
  - `temperature()`  
  - `threads()`  
  - `topP()`  
  - `topK()`  
  - `repeatPenalty()`  
  - `chatTemplateEnableThinking()`  
  - `stopStrings()` returns unmodifiable list  

#### Core logic
- Constructor enforces `modelPath` non‑null  
- Normalizes `stopStrings` to `Collections.emptyList()` if null  

#### Public API
- `libraryPath() -> String` (native lib path)  
- `modelPath() -> String` (model file path)  
- `contextSize() -> int` (context tokens)  
- `maxOutputTokens() -> int` (max output tokens)  
- `temperature() -> float` (sampling temp)  
- `threads() -> int` (CPU threads)  
- `topP() -> float` (top‑p)  
- `topK() -> int` (top‑k)  
- `repeatPenalty() -> float` (repetition penalty)  
- `chatTemplateEnableThinking() -> boolean` (chat‑template mode)  
- `stopStrings() -> List<String>` (unmodifiable stop strings)  

#### Dependencies
- `java.util.Collections`, `java.util.List`, `java.util.Objects`  
- `lombok.EqualsAndHashCode`, `lombok.ToString`  
- `net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord`  

#### Exceptions / Errors
- Constructor throws `NullPointerException` if `modelPath` is null  

#### Concurrency
- Immutable after construction; thread‑safe access to all fields.
