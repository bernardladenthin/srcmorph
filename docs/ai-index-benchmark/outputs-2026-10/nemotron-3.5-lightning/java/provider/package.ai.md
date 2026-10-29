### java/provider
- H: 1.0
- C: 13DE6834
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:10:56Z
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
> Pluggable AI text generation and completion parsing infrastructure for srcmorph document workflows, integrating local model inference and OpenAI-compatible model interactions.

#### Purpose
- Pluggable AI text generation and completion parsing for srcmorph document workflows
- Supports local GGUF model inference via llama.cpp and OpenAI-compatible model interactions
- Factory-driven provider instantiation with thinking-block-aware completion parsing

#### Responsibilities
- AI completion parsing: strips thinking blocks from LLM outputs via `AiCompletionParser`; null input yields empty string, missing end marker throws `IOException`
- Provider contract: `AiGenerationProvider` interface extending `AutoCloseable` with `generate`/`generateWithTimings`/`close`; default `generateWithTimings` delegates to `generate` with zero timings; default `close` is no-op
- Provider factory: `AiGenerationProviderFactory` resolves effective provider key via `effectiveProvider`, dispatches to mock, LlamaCppJni, or OpenAI-compatible backend; returns `MockAiGenerationProvider` for blank/null keys
- Llama.cpp JNI backend: `LlamaCppJniAiGenerationProvider` runs GGUF inference with lazy native `LlamaModel` init, consumes `LlamaCppJniConfig` and `AiPromptSupport`
- OpenAI-compatible infrastructure: `OpenAiCompatibleProviderSupport` builds JSON request bodies, resolves chat-completions endpoints, manages API keys (loopback/https validation), validates HTTP statuses, extracts `AiGenerationTimings`
- Provider key resolution and null/blank handling via `Java8CompatibilityHelper.isBlank`

#### Key units
- `AiCompletionParser` – parses raw LLM completion text; returns empty string for null input, strips thinking blocks, throws `IOException` if thinking block started without end marker
- `AiGenerationProvider` – generation contract interface extending `AutoCloseable`; `generate` returns never-null but may-be-blank string; `generateWithTimings` returns `AiGenerationTimings`; `close` is no-argument no-throw
- `AiGenerationProviderFactory` – creates providers by name (`PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`); `effectiveProvider` resolves effective key; switch dispatches to concrete implementations
- `LlamaCppJniAiGenerationProvider` – runs GGUF model inference via llama.cpp; native `LlamaModel` handle lazily initialized on first `generate` call; constructor requires non-null `LlamaCppJniConfig` and `AiPromptSupport`; model config includes path, context size, threads, GPU layers, flash attention, cache type KV/V, batch size, ubatch size, threads batch, main GPU, devices, lazy mode, SWA full, cache reuse, CPU MoE/FFN layers
- `OpenAiCompatibleProviderSupport` – final class providing non-network infrastructure for OpenAI-compatible model interactions; constructs JSON request bodies with model, system/user prompts, max_tokens, temperature, top_p, top_k, min_p, repetition_penalty, seed, stop strings, reasoning.effort, stream false; resolves endpoints, validates API key transmission, checks loopback hosts, extracts token usage and decode rates
- `AiGenerationRequest`, `AiGenerationTimings` – data types carrying generation parameters and token/rate metrics

#### Data flow
- Client code calls `AiGenerationProviderFactory.create(providerName, config, promptSupport)` → effective key resolution → concrete provider instance (`MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`)
- Provider's `generate(AiGenerationRequest)` executes backend inference: for LlamaCppJni, lazy native init then GGUF inference with configured parameters; for OpenAI-compatible, `OpenAiCompatibleProviderSupport` builds JSON request body, resolves endpoint, validates API key (loopback or https), and processes response with timing extraction
- `AiCompletionParser.parseCompletion(rawText)` independently processes raw LLM output: null→empty, trim, strip thinking blocks if present, throw `IOException` if block started without end marker
- `AiGenerationTimings` flows through `generateWithTimings` with prompt/cached/completion tokens and decode rate

#### Dependencies
- Internal types: `AiCompletionParser`, `AiGenerationProvider`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig`, `OpenAiCompatibleProviderSupport`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `Java8CompatibilityHelper`
- External modules/packages: `lombok.ToString`, `org.jspecify.annotations.Nullable`, `com.fasterxml.jackson.databind.ObjectMapper`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `java.io.IOException`, `java.net.URL`, `java.util.regex.Pattern`, `java.util.List`, `java.util.Objects`, `java.util.function.Function`
- No SQL tables; all Java types and infrastructure

#### Cross-cutting
- `@ToString` Lombok annotation on classes/fields (with `@ToString.Exclude` where needed)
- `@Nullable`/`Objects.requireNonNull` for null-safety across constructors and method parameters
- `IOException` thrown for missing thinking blocks, invalid endpoints, unset API keys, non-loopback key transmission, and non-2xx HTTP statuses
- Stateless/final where possible; thread-safety: factory and parser stateless; providers control synchronization
- Switch/dispatch on effective provider key for backend dispatch
- Constructor patterns requiring non-null `config` and `promptSupport`; final fields where applicable
- Default no-op `close()` and delegating `generateWithTimings` implementations
- JSON serialization via `ObjectMapper` for request bodies
- Pattern-based endpoint validation (http(s) scheme, trailing slash stripping)
- Loopback host checking (localhost, 127.x.x.x, [::1]) for API key transmission safety
