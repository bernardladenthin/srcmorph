### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T06:15:05Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines configuration parameters for AI generation steps in a Maven plugin using llama.cpp, including model settings, sampling behavior, and input/output limits.

#### Purpose
- Configures AI generation parameters such as temperature, max tokens, context size, and retry policies.
- Manages prompt trimming, stop sequences, and model-specific thinking modes.

#### Type
class AiGenerationConfig + modifiers; extends/implements: none; generics: none; annotations: @ToString, @Nullable, @SuppressWarnings("NullAway.Init", "initialization.fields.uninitialized")

#### Input
- Model path (String), context size (int), output tokens (int), temperature (float), threads (int), chars per token (int), max input chars (int), retry settings (int/float), stop strings (List<String>)

#### Output
- Configured generation parameters via getters; mutable state updated through setters; unmodifiable stop string list returned

#### Core logic
- Holds all AI inference configuration fields with defaults.
- Automatically calculates maxInputChars based on context size, output tokens, chars per token, and safety margin when charsPerToken > 0.
- Uses Lombok's @ToString for readable instance representation.
- Provides immutability for stopStrings via unmodifiableList wrapper.

#### Public API
getModelPath() -> String: returns model path  
setModelPath(String) -> void: sets model path  
getContextSize() -> int: returns context size  
setContextSize(int) -> void: sets context size  
getMaxOutputTokens() -> int: returns max output tokens  
setMaxOutputTokens(int) -> void: sets max output tokens  
getTemperature() -> float: returns sampling temperature  
setTemperature(float) -> void: sets sampling temperature  
getThreads() -> int: returns thread count  
setThreads(int) -> void: sets thread count  
getCharsPerToken() -> int: returns chars per token  
setCharsPerToken(int) -> void: sets chars per token  
getMaxInputChars() -> int: returns max input characters  
setMaxInputChars(int) -> void: sets max input characters  
isWarnOnTrim() -> boolean: checks trim warning setting  
setWarnOnTrim(boolean) -> void: enables/disables trim warnings  
getMaxRetries() -> int: returns retry count  
setMaxRetries(int) -> void: sets retry count  
getRetryTemperatureIncrement() -> float: returns retry temperature increment  
setRetryTemperatureIncrement(float) -> void: sets retry temperature increment  
getTopP() -> float: returns top-p value  
setTopP(float) -> void: sets top-p value  
getTopK() -> int: returns top-k limit  
setTopK(int) -> void: sets top-k limit  
getRepeatPenalty() -> float: returns repetition penalty  
setRepeatPenalty(float) -> void: sets repetition penalty  
isChatTemplateEnableThinking() -> boolean: checks thinking mode  
setChatTemplateEnableThinking(boolean) -> void: enables/disables thinking mode  
getStopStrings() -> List<String>: returns stop strings (null-safe)  
setStopStrings(List<String>) -> void: sets or clears stop strings

#### Dependencies
net.ladenthin.llama.parameters.ModelParameters, org.jspecify.annotations.Nullable

#### Exceptions / Errors
- No exceptions thrown; null handling via @Nullable and defensive checks in setters.

#### Concurrency
- Immutable stopStrings list; all fields are mutable but no concurrent access or synchronization shown.
