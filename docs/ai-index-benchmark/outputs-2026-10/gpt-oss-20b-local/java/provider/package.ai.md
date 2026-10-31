### java/provider
- H: 1.0
- C: 962F0B37
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:20:44Z
- G: 
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [OpenAiCompatibleProviderSupport.java](OpenAiCompatibleProviderSupport.java.ai.md)
---
> Provides a pluggable AI‑generation layer for source‑code transformation, supporting local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with configurable prompts, timing metrics, and robust completion parsing.  

#### Purpose  
- Central AI text generation engine for code‑morphing workflows.  
- Abstracts provider selection, request construction, and response handling.  

#### Responsibilities  
- **Provider Factory** – resolves and instantiates the correct `AiGenerationProvider` (mock, llama.cpp JNI, or OpenAI‑compatible).  
- **Provider Implementations** – generate text, optionally return timing data, and manage native resources.  
- **Prompt & Request Support** – build chat‑completion payloads, validate endpoints, and secure API keys.  
- **Completion Parsing** – strip internal reasoning blocks from LLM outputs.  

#### Key units  
- `AiGenerationProviderFactory` – creates providers based on run or model config.  
- `AiGenerationProvider` (interface) – contract for `generate` and `generateWithTimings`.  
- `MockAiGenerationProvider` – stub provider for testing.  
- `LlamaCppJniAiGenerationProvider` – local inference via llama.cpp JNI, lazy model loading, timing extraction.  
- `OpenAiCompatibleAiGenerationProvider` – HTTP client for OpenAI‑compatible endpoints, request body builder, key validation.  
- `LlamaCppJniConfig` – immutable configuration holder for llama.cpp parameters.  
- `AiCompletionParser` – removes leading chain‑of‑thought blocks from raw completions.  

#### Data flow  
1. **Request** – `AiGenerationRequest` (source file, prompt key).  
2. **Factory** – `AiGenerationProviderFactory.create()` selects provider.  
3. **Provider** – `generate()` or `generateWithTimings()` builds prompt via `AiPromptSupport`, sends to model or API, receives raw completion.  
4. **Parser** – `AiCompletionParser.parseCompletion()` cleans the text.  
5. **Result** – plain string or `AiGenerationTimings` returned to caller.  

#### Dependencies  
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.  
- **External** – `net.ladenthin.llama` (JNI model, parameters, JSON parsing), Jackson (`ObjectMapper`), `org.jspecify.annotations.Nullable`.  
- **Runtime** – native llama.cpp library, HTTP client for OpenAI‑compatible endpoints.  

#### Cross-cutting  
- **Immutability** – `LlamaCppJniConfig` and provider support classes are immutable, thread‑safe.  
- **Error handling** – IOExceptions for missing reasoning blocks, endpoint validation, or key safety; IllegalArgumentException for unsupported provider keys.  
- **Timing metrics** – `AiGenerationTimings` captures prompt/cache tokens, decode rates, and predicted token counts.  
- **Configuration** – fluent builder pattern in `LlamaCppJniConfig` with defaults from `AiGenerationConfig`.  
- **Logging & warnings** – provider implementations log truncation and cache reuse.  

---
