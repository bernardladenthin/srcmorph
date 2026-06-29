### llamacpp-ai-index-maven-plugin
- H: 1.0
- C: 07339891
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T19:18:17Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: project
- F: [main/java/net/ladenthin/maven/llamacpp/aiindex/config](main/java/net/ladenthin/maven/llamacpp/aiindex/config/package.ai.md)
- F: [main/java/net/ladenthin/maven/llamacpp/aiindex](main/java/net/ladenthin/maven/llamacpp/aiindex/package.ai.md)
- F: [main/java/net/ladenthin/maven/llamacpp/aiindex/provider](main/java/net/ladenthin/maven/llamacpp/aiindex/provider/package.ai.md)
- F: [main/java/net/ladenthin/maven/llamacpp](main/java/net/ladenthin/maven/llamacpp/package.ai.md)
- F: [main/java/net/ladenthin/maven](main/java/net/ladenthin/maven/package.ai.md)
- F: [main/java/net/ladenthin](main/java/net/ladenthin/package.ai.md)
- F: [main/java/net](main/java/net/package.ai.md)
- F: [main/java](main/java/package.ai.md)
- F: [main](main/package.ai.md)
- F: [.](package.ai.md)
---
> Project index of llamacpp-ai-index-maven-plugin: one line per package; leads here, clickable links in the header F list.

#### Overview
The `llamacpp-ai-index-maven-plugin` subsystems orchestrate a localized Llama.cpp inference pipeline within Maven builds to generate AI-driven code indices. Core components handle the mapping of source file extensions to custom prompt templates and execute native GGUF model generation for field extraction. The architecture includes a pluggable provider layer that sanitizes raw reasoning outputs from the model, ensuring clean, indexed data while maintaining extensible configuration for different build contexts.

#### Packages
- main/java/net/ladenthin/maven/llamacpp/aiindex/config — Executes AI-driven field generation for Maven plugin builds by mapping source file extensions to prompt templates and model configurations.
- main/java/net/ladenthin/maven/llamacpp/aiindex — Orchestrates local Llama.cpp inference and field generation for Maven plugin builds by mapping source files to AI prompts and sanitizing reasoning outputs.
- main/java/net/ladenthin/maven/llamacpp/aiindex/provider — Provides a pluggable local LLM inference engine via llama.cpp JNI, extracting clean model answers by stripping internal reasoning blocks for AI indexing.
- main/java/net/ladenthin/maven/llamacpp — Orchestrates local Llama.cpp inference for Maven builds by mapping source files to AI prompts, executing native GGUF generation, and sanitizing reasoning outputs before indexing.
- main/java/net/ladenthin/maven — Orchestrates local Llama.cpp inference for Maven builds by mapping source files to AI prompts, executing native GGUF generation, and sanitizing reasoning outputs before indexing.
- main/java/net/ladenthin — Orchestrates local Llama.cpp inference for Maven builds by mapping source files to AI prompts, executing native GGUF generation, and sanitizing reasoning outputs before indexing.
- main/java/net — Orchestrates local Llama.cpp inference for Maven builds by mapping source files to AI prompts, executing native GGUF generation, and sanitizing reasoning outputs before indexing.
- main/java — Orchestrates local Llama.cpp inference for Maven builds by mapping source files to AI prompts, executing native GGUF generation, and sanitizing reasoning outputs before indexing.
- main — Orchestrates local Llama.cpp inference for Maven builds by mapping source files to AI prompts, executing native GGUF generation, and sanitizing reasoning outputs before indexing.
- . — Orchestrates local Llama.cpp inference for Maven builds by mapping source files to AI prompts, executing native GGUF generation, and sanitizing reasoning outputs before indexing.
