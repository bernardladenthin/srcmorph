### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:13:20Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices

#### Purpose
- Provides configuration for the llama.cpp JNI provider.

#### Type
- Kind: final class
- Modifiers: none
- Extends: none
- Implements: none
- Key generics and type bounds: none
- Notable annotations: @ConvertToRecord, @ToString, @EqualsAndHashCode

#### Input
- Constructor and method parameters: libraryPath, modelPath, contextSize, maxOutputTokens, temperature, threads, topP, topK, repeatPenalty, chatTemplateEnableThinking, stopStrings
- Injected dependencies: none
- Consumed fields: none
- Read resources: none

#### Output
- Produced state: none
- Mutated fields: none
- Written resources: none
- Side effects: none

#### Core logic
- Immutable configuration with value semantics
- Accessors for each configuration parameter

#### Public API
- libraryPath() -> String: Returns the native library path
- modelPath() -> String: Returns the GGUF model file path
- contextSize() -> int: Returns the context window size in tokens
- maxOutputTokens() -> int: Returns the maximum number of output tokens per call
- temperature() -> float: Returns the sampling temperature
- threads() -> int: Returns the number of CPU threads
- topP() -> float: Returns the nucleus-sampling probability threshold
- topK() -> int: Returns the top-k sampling limit
- repeatPenalty() -> float: Returns the repetition penalty
- chatTemplateEnableThinking() -> boolean: Returns whether chat-template thinking mode is enabled
- stopStrings() -> List<String>: Returns an unmodifiable view of the configured stop strings

#### Dependencies
- java.util.Collections
- java.util.List
- java.util.Objects
- lombok.EqualsAndHashCode
- lombok.ToString
- net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord

#### Exceptions / Errors
- None

#### Concurrency
- Immutable configuration; thread-safe by design
