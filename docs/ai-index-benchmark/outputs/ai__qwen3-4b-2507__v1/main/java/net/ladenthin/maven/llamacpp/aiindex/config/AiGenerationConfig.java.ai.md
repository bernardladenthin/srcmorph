### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:44:23Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines default and mutable parameters for AI model inference in Maven-based AI generation steps

#### Purpose
- Configures sampling, context, and retry behavior for AI model inference  
- Bridges Maven plugin configuration with llama.cpp inference parameters  

#### Type
class, public, mutable, final fields, Lombok @ToString, static defaults  

#### Input
- Constructor: no input  
- Method parameters: modelPath (String), contextSize (int), temperature (float), threads (int), charsPerToken (int), maxInputChars (int), warnOnTrim (boolean), maxRetries (int), retryTemperatureIncrement (float), topP (float), topK (int), repeatPenalty (float), chatTemplateEnableThinking (boolean), stopStrings (List<String>)  

#### Output
- Returns configured values via getter methods  
- Produces immutable list of stop strings when requested  
- Updates internal state via setters (mutable)  

#### Core logic
- Stores model-specific inference parameters in a mutable object  
- Uses static defaults for all fields to provide consistent baseline behavior  
- Automatically calculates maxInputChars from context size, output tokens, and safety margin when charsPerToken > 0  
- Implements retry logic with temperature increment per attempt to improve model response quality  
- Supports chat-template thinking mode control to suppress or enable chain-of-thought reasoning  

#### Public API
- getModelPath() → String (get model file path)  
- getContextSize() → int (get context window size)  
- getMaxOutputTokens() → int (get max output tokens)  
- getTemperature() → float (get sampling temperature)  
- getThreads() → int (get CPU threads)  
- getCharsPerToken() → int (get chars per token)  
- getMaxInputChars() → int (get max input characters)  
- isWarnOnTrim() → boolean (check trim warning status)  
- getMaxRetries() → int (get retry count)  
- getRetryTemperatureIncrement() → float (get retry temp increment)  
- getTopP() → float (get top-p sampling)  
- getTopK() → int (get top-k sampling)  
- getRepeatPenalty() → float (get repetition penalty)  
- isChatTemplateEnableThinking() → boolean (check thinking mode)  
- getStopStrings() → List<String> (get stop strings, immutable)  

#### Dependencies
- java.util.List, java.util.ArrayList, java.util.Collections  
- lombok.ToString  
- org.jspecify.annotations.Nullable  

#### Exceptions / Errors
- No explicit exceptions thrown; null handling via nullable fields and defensive checks in setters  
- null modelPath allowed; null stopStrings resets to empty list  

#### Concurrency
- Immutable getters; mutable state only updated via setters  
- Thread-safe access via immutable views (e.g. getStopStrings returns unmodifiable list)  
- No synchronization or shared state; safe for concurrent reads
