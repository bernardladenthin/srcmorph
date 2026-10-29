### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:11:49Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the root configuration for AI-powered source code transformation in srcmorph, including input/output paths, AI model settings, and generation rules.

#### Purpose
- Central configuration object for srcmorph AI-driven source code generation and analysis
- Holds all parameters needed for a run, whether from Maven plugin, JSON/YAML config, or direct Java construction
- Designed to be mutable and bindable across multiple configuration surfaces

#### Type
class, public, mutable, with Lombok @ToString, extends none, implements none  
fields: private, with nullable lists and primitives  
static final defaults for key AI and path settings

#### Input
- Constructor: no parameters (creates default instance)
- Method parameters: File, List<String>, List<T>, String, int, float, long, boolean
- Injected dependencies: none directly
- Configurable via: Maven @Parameter fields, JSON/YAML config, direct Java construction

#### Output
- Returns configuration state via getters
- Produces: File paths (base, output), AI settings (provider, model, context, temperature), generation rules, exclusions, file filters
- Side effects: none (pure getter/setter model)

#### Core logic
- Provides a unified configuration model that mirrors Maven plugin field names for backward compatibility
- All values have default fallbacks (static constants) when not explicitly set
- Supports per-field AI generation routing via prompt and model definitions
- Enables fallback AI settings (llama.cpp) when field generation rules are absent
- Allows filtering of source files by size, extensions, subdirectories, and glob patterns

#### Public API
- getBaseDirectory() → File: returns project base directory  
- setBaseDirectory(File) → void: sets base directory  
- getOutputDirectory() → File: returns output directory (defaults to "src/site/ai")  
- setOutputDirectory(File) → void: sets output directory  
- isForce() → boolean: checks if AI fields should always be regenerated  
- setForce(boolean) → void: enables force regeneration  
- getSubtrees() → List<String>: returns restricted source subdirectories  
- setSubtrees(List<String>) → void: sets source subdirectory restrictions  
- getExcludes() → List<String>: returns file glob patterns to skip  
- setExcludes(List<String>) → void: sets file exclusions  
- getFileExtensions() → List<String>: returns file extensions to index  
- setFileExtensions(List<String>) → void: sets file extensions  
- getMinFileSizeBytes() → long: returns minimum file size (in bytes)  
- setMinFileSizeBytes(long) → void: sets minimum file size  
- getMaxFileSizeBytes() → long: returns maximum file size (in bytes)  
- setMaxFileSizeBytes(long) → void: sets maximum file size  
- isPlanOnly() → boolean: checks if run stops after planning  
- setPlanOnly(boolean) → void: enables plan-only mode  
- getGenerationProvider() → String: returns AI provider name  
- setGenerationProvider(String) → void: sets AI provider  
- getPromptDefinitions() → List<AiPromptDefinition>: returns prompt templates  
- setPromptDefinitions(List<AiPromptDefinition>) → void: sets prompt definitions  
- getAiDefinitions() → List<AiModelDefinition>: returns AI model definitions  
- setAiDefinitions(List<AiModelDefinition>) → void: sets model definitions  
- getModelDirectory() → File: returns model path base directory  
- setModelDirectory(File) → void: sets model directory base  
- getFieldGenerations() → List<AiFieldGenerationConfig>: returns field generation rules  
- setFieldGenerations(List<AiFieldGenerationConfig>) → void: sets field generation rules  
- getFactDefinitions() → List<AiFactDefinition>: returns reusable fact groups  
- setFactDefinitions(List<AiFactDefinition>) → void: sets fact definitions  
- getLlamaModelPath() → String: returns fallback GGUF model path  
- setLlamaModelPath(String) → void: sets fallback model path  
- getLlamaContextSize() → int: returns llama.cpp context size  
- setLlamaContextSize(int) → void: sets context size  
- getLlamaMaxOutputTokens() → int: returns max output tokens  
- setLlamaMaxOutputTokens(int) → void: sets max output tokens  
- getLlamaTemperature() → float: returns sampling temperature  
- setLlamaTemperature(float) → void: sets temperature  
- getLlamaThreads() → int: returns CPU threads for inference  
- setLlamaThreads(int) → void: sets thread count  
- getPluginVersion() → String: returns plugin version  
- setPluginVersion(String) → void: sets plugin version  
- getAiVersion() → String: returns AI summarization version  
- setAiVersion(String) → void: sets AI version  
- getProjectName() → String: returns project title  
- setProjectName(String) → void: sets project title  

#### Dependencies
- java.io.File  
- java.util.List  
- lombok.ToString  
- net.ladenthin.srcmorph.prompt.AiPromptDefinition  
- net.ladenthin.srcmorph.ai.AiModelDefinition  
- net.ladenthin.srcmorph.ai.AiFieldGenerationConfig  
- net.ladenthin.srcmorph.ai.AiFactDefinition  

#### Exceptions / Errors
- No explicit throws; all methods are safe and non-throwing  
- Null handling via @Nullable annotations on lists and strings  
- No null checks in core logic; defaults applied when null passed

#### Concurrency
- Immutable state via getters; mutable via setters  
- No synchronization or thread safety guarantees  
- Safe for single-threaded use; not designed for concurrent access  
- No shared mutable state beyond instance fields
