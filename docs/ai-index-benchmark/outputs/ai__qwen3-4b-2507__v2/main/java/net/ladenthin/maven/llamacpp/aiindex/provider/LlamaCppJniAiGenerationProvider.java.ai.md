### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T06:22:33Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Enables local AI text generation using GGUF models via llama.cpp JNI bindings in a Maven-based indexing pipeline.

#### Purpose
- Provides lazy-loaded, local AI text generation using llama.cpp JNI bindings.
- Integrates prompt rendering and completion parsing for structured AI response handling.

#### Type
class final LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable

#### Input
- LlamaCppJniConfig (model path, context size, threads, etc.)
- AiPromptSupport (for building prompts from request)

#### Output
- String (generated AI response text)
- Native model handle closed on shutdown

#### Core logic
- Lazily loads GGUF model on first generate() call to minimize startup cost.
- Constructs inference parameters from config and request with temperature, stop strings, top-p/k, and context settings.
- Builds prompt chain using user message and system message (optional).
- Invokes native chat completion via model().chatCompleteText().
- Parses output using completion parser.

#### Public API
generate(AiGenerationRequest) -> String: generates response from request  
generate(AiGenerationRequest, float) -> String: generates with override temperature  
close() -> void: releases native model resources

#### Dependencies
LlamaModel, InferenceParameters, ModelParameters, Pair, AiPromptSupport, AiCompletionParser, LlamaCppJniConfig

#### Exceptions / Errors
- IOException on model loading or native call failure  
- Null handling via requireNonNull on config and promptSupport

#### Concurrency
- Immutable inference parameters; thread-safe only if model access is serialized (not explicitly guarded)  
- Model loaded lazily; concurrent calls may result in duplicated model creation unless synchronized — not observed here
