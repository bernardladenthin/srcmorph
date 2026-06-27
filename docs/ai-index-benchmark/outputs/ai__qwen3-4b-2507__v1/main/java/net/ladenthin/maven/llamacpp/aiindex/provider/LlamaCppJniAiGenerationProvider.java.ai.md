### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:53:43Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Generates AI responses from GGUF models using llama.cpp via JNI, with lazy loading and prompt templating

#### Purpose
- Executes AI text generation using local GGUF models via llama.cpp JNI binding  
- Delays model loading until first generation to reduce startup overhead  

#### Type
- final class implements AiGenerationProvider, AutoCloseable  
- extends none, implements AiGenerationProvider, AutoCloseable  
- generics: none  
- key annotations: @ToString, @Nullable, Lombok @ToString.Exclude  

#### Input
- LlamaCppJniConfig (model path, context size, threads, temperature, etc.)  
- AiPromptSupport (for building prompts from request)  
- AiGenerationRequest (input prompt and metadata)  
- float temperatureOverride (optional override for generation temperature)  

#### Output
- String (generated AI response text)  
- Model closed on dispose (native resources freed)  

#### Core logic
- Lazily loads GGUF model on first generate() call using provided config  
- Builds prompt from request via prompt support  
- Constructs chat message list with user prompt  
- Sets inference parameters: temperature, max tokens, top-p, top-k, stop strings  
- Executes chat completion via native llama.cpp model  
- Parses and returns generated response text  

#### Public API
- generate(AiGenerationRequest) -> String (generates response using default temperature)  
- generate(AiGenerationRequest, float) -> String (generates with custom temperature)  
- close() -> void (releases native model handle)  

#### Dependencies
- net.ladenthin.llama.LlamaModel  
- net.ladenthin.llama.parameters.InferenceParameters  
- net.ladenthin.llama.parameters.ModelParameters  
- net.ladenthin.llama.value.Pair  
- net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest  
- net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport  
- net.ladenthin.maven.llamacpp.aiindex.prompt.AiCompletionParser  

#### Exceptions / Errors
- Throws IOException on model load or generation failure  
- Null checks on config and promptSupport at construction  
- No explicit error handling beyond standard JNI/IO exceptions  

#### Concurrency
- Thread-safe only via lazy initialization and model caching  
- No shared state; each generate() call is independent  
- Model loaded per instance, not shared across threads  
- Immutable inference parameters prevent mutation during use
