### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:47:23Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines AI model parameters by key for reuse in field generation configurations

#### Purpose
- Stores AI model configuration parameters (e.g., context size, temperature) tied to a unique lookup key  
- Enables reusable model settings across multiple field-generation entries  

#### Type
- class: public, final (not final), mutable JavaBean  
- implements: none  
- extends: none  
- generics: none  
- annotations: @ToString, @Nullable, @SuppressWarnings("NullAway.Init", "initialization.fields.uninitialized")  

#### Input
- constructor: default values from AiGenerationConfig  
- parameters: key (String), modelPath (String), and various numeric/boolean fields via setters  
- injected dependencies: none directly; relies on external configuration via reflection  

#### Output
- returns: key, model path, and all model-specific parameters (e.g., context size, temperature)  
- produces: immutable view of stopStrings list via unmodifiableList  
- side effects: sets internal state through setters; no mutations beyond field assignment  

#### Core logic
- Defines a reusable AI model configuration with default values from AiGenerationConfig  
- Uses key-based lookup for reference in field-generation configs  
- All numeric and boolean fields inherit defaults from AiGenerationConfig  
- stopStrings are managed as a nullable list, converted to immutable view on access  
- setters update internal state; no validation or transformation logic  

#### Public API
- getKey() → String (returns unique model key)  
- setKey(String) → void (assigns key for lookup)  
- getModelPath() → String (returns model file path)  
- setModelPath(String) → void (sets GGUF model path)  
- getContextSize() → int (returns context window size)  
- setContextSize(int) → void (configures input tokens)  
- getMaxOutputTokens() → int (returns max output tokens)  
- setMaxOutputTokens(int) → void (limits generated output)  
- getTemperature() → float (returns sampling temperature)  
- setTemperature(float) → void (controls randomness)  
- getThreads() → int (returns CPU threads for inference)  
- setThreads(int) → void (configures parallelism)  
- getCharsPerToken() → int (returns chars per token for input trimming)  
- setCharsPerToken(int) → void (enables dynamic max input calculation)  
- isWarnOnTrim() → boolean (checks trim warning flag)  
- setWarnOnTrim(boolean) → void (toggles input trim warning)  
- getMaxRetries() → int (returns retry attempts on empty response)  
- setMaxRetries(int) → void (configures retry policy)  
- getRetryTemperatureIncrement() → float (returns retry temp increment)  
- setRetryTemperatureIncrement(float) → void (adjusts temperature on retries)  
- getTopP() → float (returns nucleus sampling threshold)  
- setTopP(float) → void (configures top-p filtering)  
- getTopK() → int (returns top-k token limit)  
- setTopK(int) → void (limits sampling to top-k tokens)  
- getRepeatPenalty() → float (returns repetition penalty)  
- setRepeatPenalty(float) → void (penalizes repeated tokens)  
- isChatTemplateEnableThinking() → boolean (checks thinking mode enable)  
- setChatTemplateEnableThinking(boolean) → void (controls chain-of-thought reasoning)  
- getStopStrings() → List<String> (returns stop strings, null if not set)  
- setStopStrings(Collection<String>) → void (configures generation termination strings)  

#### Dependencies
- AiGenerationConfig  
- java.util.List, java.util.Collections, java.util.ArrayList  
- lombok.ToString  
- org.jspecify.annotations.Nullable  

#### Exceptions / Errors
- no explicit throws; no error handling or validation logic  
- null fields allowed via @Nullable; stopStrings may be null  
- defaults used for all fields; no bounds checking or input sanitization  

#### Concurrency
- thread-safe by design: immutable views (e.g., stopStrings) and state updates via setters  
- no synchronization or shared mutable state  
- safe for concurrent access due to immutability of returned values and no shared state mutations
