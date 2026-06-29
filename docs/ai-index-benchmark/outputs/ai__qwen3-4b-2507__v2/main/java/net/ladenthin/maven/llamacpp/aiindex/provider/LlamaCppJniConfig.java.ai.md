### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T06:23:31Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines immutable configuration for llama.cpp JNI-based AI model inference

#### Purpose
- Configures runtime parameters for llama.cpp JNI-based LLM inference  
- Encapsulates model, library, and generation settings with value semantics  

#### Type
class final public @ConvertToRecord @ToString @EqualsAndHashCode

#### Input
- libraryPath (String): native library path or null  
- modelPath (String): GGUF model file path  
- contextSize (int): context window size in tokens  
- maxOutputTokens (int): max output tokens per call  
- temperature (float): sampling temperature  
- threads (int): CPU thread count  
- topP (float): nucleus-sampling threshold  
- topK (int): top-k sampling limit  
- repeatPenalty (float): repetition penalty  
- chatTemplateEnableThinking (boolean): enables thinking mode in chat templates  
- stopStrings (List<String>): list of stop strings; null treated as empty  

#### Output
- libraryPath() → String  
- modelPath() → String  
- contextSize() → int  
- maxOutputTokens() → int  
- temperature() → float  
- threads() → int  
- topP() → float  
- topK() → int  
- repeatPenalty() → float  
- chatTemplateEnableThinking() → boolean  
- stopStrings() → List<String> (unmodifiable)  

#### Core logic
- Validates modelPath at construction  
- Null stopStrings is converted to empty list  
- All fields are final and immutable  
- Lombok generates equals, hashCode, toString with bit-level float precision  

#### Public API
- libraryPath() → String: native library path  
- modelPath() → String: GGUF model file path  
- contextSize() → int: context window size  
- maxOutputTokens() → int: max output tokens  
- temperature() → float: sampling temperature  
- threads() → int: CPU thread count  
- topP() → float: nucleus-sampling threshold  
- topK() → int: top-k limit  
- repeatPenalty() → float: repetition penalty  
- chatTemplateEnableThinking() → boolean: enables thinking mode  
- stopStrings() → List<String>: unmodifiable stop strings  

#### Dependencies
- java.util.Collections  
- java.util.Objects  
- lombok.EqualsAndHashCode  
- lombok.ToString  
- net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord  

#### Exceptions / Errors
- Throws IllegalArgumentException if modelPath is null  

#### Concurrency
- Immutable; thread-safe by design due to final fields and no mutable state
