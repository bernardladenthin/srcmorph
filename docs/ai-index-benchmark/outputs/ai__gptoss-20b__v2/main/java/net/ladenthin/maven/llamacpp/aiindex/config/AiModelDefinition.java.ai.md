### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:03:03Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines mutable configuration for an LLM model used by a Maven plugin.

#### Purpose
- Holds model lookup key and parameters  
- Allows reuse across field‑generation goals

#### Type
- public class AiModelDefinition; Lombok @ToString; defaults from AiGenerationConfig

#### Input
- Constructor AiModelDefinition() – no‑op  
- setKey(String) – assigns lookup key  
- setModelPath(String) – assigns GGUF model path  
- setContextSize(int) – assigns context window size  
- setMaxOutputTokens(int) – assigns max tokens per inference  
- setTemperature(float) – assigns base temperature  
- setThreads(int) – assigns CPU thread count  
- setCharsPerToken(int) – assigns char‑per‑token ratio  
- setWarnOnTrim(boolean) – enables trim warning  
- setMaxRetries(int) – assigns retry limit  
- setRetryTemperatureIncrement(float) – sets retry temperature delta  
- setTopP(float) – assigns nucleus‑sampling threshold  
- setTopK(int) – assigns top‑k limit  
- setRepeatPenalty(float) – assigns repetition penalty  
- setChatTemplateEnableThinking(boolean) – toggles chat‑template thinking  
- setStopStrings(Collection<String>) – assigns stop‑strings list

#### Output
- getKey() -> lookup key  
- getModelPath() -> model file path  
- getContextSize() -> context token count  
- getMaxOutputTokens() -> max output tokens  
- getTemperature() -> sampling temperature  
- getThreads() -> CPU thread count  
- getCharsPerToken() -> char‑per‑token value  
- isWarnOnTrim() -> trim‑warning flag  
- getMaxRetries() -> retry limit  
- getRetryTemperatureIncrement() -> retry temperature delta  
- getTopP() -> nucleus‑sampling threshold  
- getTopK() -> top‑k limit  
- getRepeatPenalty() -> repetition penalty  
- isChatTemplateEnableThinking() -> chat‑template flag  
- getStopStrings() -> immutable stop‑string list  

#### Core logic
- Initializes numeric fields with AiGenerationConfig defaults  
- Stores fields; setters perform shallow assignments  
- getStopStrings() returns unmodifiable view to preserve immutability

#### Public API
- AiModelDefinition() -> no‑op constructor  
- getKey() -> String key lookup  
- setKey(String) -> assign key  
- getModelPath() -> String path  
- setModelPath(String) -> assign path  
- getContextSize() -> int context size  
- setContextSize(int) -> assign context size  
- getMaxOutputTokens() -> int max output  
- setMaxOutputTokens(int) -> assign max output  
- getTemperature() -> float temperature  
- setTemperature(float) -> assign temperature  
- getThreads() -> int thread count  
- setThreads(int) -> assign threads  
- getCharsPerToken() -> int chars per token  
- setCharsPerToken(int) -> assign chars per token  
- isWarnOnTrim() -> boolean trim warning flag  
- setWarnOnTrim(boolean) -> set trim warning flag  
- getMaxRetries() -> int retries  
- setMaxRetries(int) -> set retry limit  
- getRetryTemperatureIncrement() -> float retry delta  
- setRetryTemperatureIncrement(float) -> set retry delta  
- getTopP() -> float top‑p value  
- setTopP(float) -> set top‑p value  
- getTopK() -> int top‑k value  
- setTopK(int) -> set top‑k value  
- getRepeatPenalty() -> float repeat penalty  
- setRepeatPenalty(float) -> set repeat penalty  
- isChatTemplateEnableThinking() -> boolean chat template flag  
- setChatTemplateEnableThinking(boolean) -> set chat template flag  
- getStopStrings() -> List<String> stop strings  
- setStopStrings(Collection<String>) -> assign stop strings  

#### Dependencies
- AiGenerationConfig  
- java.util.ArrayList  
- java.util.Collection  
- java.util.Collections  
- java.util.List  
- lombok.ToString  
- org.jspecify.annotations.Nullable  

#### Exceptions / Errors
- None declared; all setters accept null where allowed  

#### Concurrency
- No explicit thread‑safety mechanisms; instance is not immutable.
