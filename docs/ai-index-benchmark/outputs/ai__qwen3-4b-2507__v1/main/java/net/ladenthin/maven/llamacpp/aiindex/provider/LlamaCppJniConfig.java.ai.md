### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:55:01Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines immutable configuration for llama.cpp JNI-based AI model inference

#### Purpose
- Configures runtime parameters for llama.cpp JNI-based LLM inference  
- Encapsulates model and execution settings in a thread-safe, immutable format  

#### Type
- final class  
- extends: none  
- implements: none  
- generics: none  
- annotations: @ConvertToRecord, @ToString, @EqualsAndHashCode  

#### Input
- libraryPath (native library path; may be null)  
- modelPath (path to GGUF model file)  
- contextSize (context window size in tokens)  
- maxOutputTokens (max output tokens per call)  
- temperature (sampling temperature)  
- threads (CPU thread count)  
- topP (nucleus sampling threshold)  
- topK (top-k sampling limit)  
- repeatPenalty (repetition penalty)  
- chatTemplateEnableThinking (boolean for thinking mode)  
- stopStrings (list of stop strings; null → empty list)  

#### Output
- libraryPath (native library path)  
- modelPath (model file path)  
- contextSize (context window size)  
- maxOutputTokens (max output tokens)  
- temperature (sampling temperature)  
- threads (thread count)  
- topP (nucleus sampling threshold)  
- topK (top-k limit)  
- repeatPenalty (repetition penalty)  
- chatTemplateEnableThinking (thinking mode enabled)  
- stopStrings (unmodifiable list of stop strings)  

#### Core logic
- Requires non-null modelPath at construction  
- Null stopStrings default to empty list  
- All fields are final and initialized in constructor  
- All accessors return immutable, defensive copies  
- Lombok generates equals, hashCode, toString with bit-level float comparison  

#### Public API
- libraryPath() → String (native library path)  
- modelPath() → String (model file path)  
- contextSize() → int (context window size)  
- maxOutputTokens() → int (max output tokens)  
- temperature() → float (sampling temperature)  
- threads() → int (CPU thread count)  
- topP() → float (top-p threshold)  
- topK() → int (top-k limit)  
- repeatPenalty() → float (repetition penalty)  
- chatTemplateEnableThinking() → boolean (thinking mode enabled)  
- stopStrings() → List<String> (unmodifiable stop strings)  

#### Dependencies
- java.util.Collections  
- java.util.List  
- java.util.Objects  
- lombok.EqualsAndHashCode  
- lombok.ToString  
- net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord  

#### Exceptions / Errors
- Throws NullPointerException if modelPath is null  

#### Concurrency
- Immutable state; thread-safe by design  
- No shared mutable fields or synchronization needed  
- All accessors return defensive copies of internal state
