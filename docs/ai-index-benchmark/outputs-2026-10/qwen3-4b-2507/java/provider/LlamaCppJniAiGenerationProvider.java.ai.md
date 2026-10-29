### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:12:25Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI text using local GGUF models via llama.cpp JNI with full timing and prompt reuse support

#### Purpose
- Provides AI text generation using locally loaded GGUF models via llama.cpp JNI  
- Supports configurable model parameters, GPU offloading, KV cache reuse, and detailed timing metrics  

#### Type
final class implements AiGenerationProvider, AutoCloseable  
extends: none  
implements: AiGenerationProvider, AutoCloseable  
generics: none  
notable annotations: @ToString, @Nullable, @ToString.Exclude  

#### Input
- LlamaCppJniConfig (model path, context size, threads, GPU settings, cache policies)  
- AiPromptSupport (for rendering request prompts)  
- AiGenerationRequest (prompt, parameters, request metadata)  

#### Output
- Generated text string from AI model response  
- AiGenerationTimings (token counts, prompt/predicted usage, throughput in tokens/second)  
- Side-effect: native model loaded lazily on first use  

#### Core logic
- Lazily loads GGUF model on first generate() call to reduce startup cost  
- Builds model parameters from config (context size, threads, GPU layers, cache reuse, flash attention)  
- Applies prompt template and inference settings via support layer  
- Executes native chat completion and parses full response (including timing data)  
- Extracts and returns completion text and performance metrics  
- Closes native model handle on provider shutdown  

#### Public API
generate(AiGenerationRequest) -> String (produces AI-generated text)  
generateWithTimings(AiGenerationRequest) -> AiGenerationTimings (returns text + performance metrics)  
close() -> void (releases native model resources)  

#### Dependencies
LlamaModel, ModelParameters, FlashAttn, ReasoningFormat, ChatResponseParser, AiCompletionParser, Java8CompatibilityHelper, LlamaCppJniConfig, AiPromptSupport, AiGenerationRequest, AiGenerationTimings, Timings  

#### Exceptions / Errors
- Throws IOException on native model load or inference failure  
- No checked exceptions in native calls; handled via unchecked IOException  
- Null checks on config and promptSupport in constructor  

#### Concurrency
- Thread-safe only via lazy model loading and single-threaded native access  
- No shared mutable state beyond model handle (guarded by lazy init)  
- Not designed for concurrent generation across threads without external synchronization
