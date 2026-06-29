### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T17:03:39Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures native llama.cpp inference parameters for AI model execution.

#### Purpose
- Encapsulates immutable settings for JNI-based llama.cpp model invocation.
- Provides accessors for all configuration fields with defensive copying.

#### Type
final class implements no interfaces; extends no type; generics: none; annotations: @ConvertToRecord, @ToString, @EqualsAndHashCode

#### Input
Constructor accepts 11 parameters including String libraryPath, modelPath, int contextSize, int maxOutputTokens, float temperature, int threads, float topP, int topK, float repeatPenalty, boolean chatTemplateEnableThinking, List<String> stopStrings; requires non-null modelPath

#### Output
Public accessors return field values; stopStrings() returns unmodifiable view of list; all fields are defensively copied or wrapped

#### Core logic
- Validates non-null modelPath during construction
- Assigns constructor parameters to private final fields
- Converts null stopStrings to empty immutable list
- Provides record-style accessor methods for each field

#### Public API
libraryPath() -> String returns native library path  
modelPath() -> String returns GGUF model file path  
contextSize() -> int returns context window size  
maxOutputTokens() -> int returns maximum output tokens  
temperature() -> float returns sampling temperature  
threads() -> int returns CPU threads count  
topP() -> float returns nucleus-sampling threshold  
topK() -> int returns top-k sampling limit  
repeatPenalty() -> float returns repetition penalty  
chatTemplateEnableThinking() -> boolean returns thinking mode flag  
stopStrings() -> List<String> returns unmodifiable stop strings  

#### Dependencies
java.util.Collections, java.util.List, java.util.Objects, lombok.EqualsAndHashCode, lombok.ToString, net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord

#### Exceptions / Errors
Throws NullPointerException if modelPath is null; handles null stopStrings by converting to empty list

#### Concurrency
Immutable field design ensures thread-safe access; no synchronization required; defensive copying prevents external mutation
