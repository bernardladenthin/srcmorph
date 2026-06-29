### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T06:18:09Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines AI model configurations for reuse across field-generation tasks in a Maven plugin

#### Purpose
- Stores AI model parameters with a unique key for lookup and reuse  
- Enables consistent, configurable model behavior via defaults inherited from AiGenerationConfig  

#### Type
class public @ToString

#### Input
- None (initialized via constructor or setters)

#### Output
- Key, model path, and all model-specific parameters (context size, temperature, threads, etc.)  
- Unmodifiable stop strings list on access  

#### Core logic
- Provides default values from AiGenerationConfig for all numeric and boolean fields  
- Ensures immutability of stopStrings via unmodifiableList wrapper  
- Supports dynamic configuration through public setters with defaults applied  

#### Public API
setKey(String) → void: sets unique lookup key  
setModelPath(String) → void: sets GGUF model file path  
setContextSize(int) → void: sets context window size in tokens  
setMaxOutputTokens(int) → void: sets max output tokens per call  
setTemperature(float) → void: sets base sampling temperature  
setThreads(int) → void: sets CPU threads for inference  
setCharsPerToken(int) → void: configures chars per token for input trimming  
setWarnOnTrim(boolean) → void: enables warnings on input trim  
setMaxRetries(int) → void: sets max retry attempts on empty responses  
setRetryTemperatureIncrement(float) → void: increments temperature on retries  
setTopP(float) → void: sets nucleus sampling threshold  
setTopK(int) → void: sets top-k token sampling limit  
setRepeatPenalty(float) → void: applies repetition penalty  
setChatTemplateEnableThinking(boolean) → void: enables/disable thinking mode in chat templates  
setStopStrings(Collection<String>) → void: sets generation stop strings  

#### Dependencies
AiGenerationConfig

#### Exceptions / Errors
- None explicitly thrown; null handling via @Nullable and defensive checks on stopStrings  

#### Concurrency
- Immutable fields except for mutable stopStrings (thread-safe if accessed via immutable views)
