### llamacpp-ai-index-maven-plugin
- H: 1.0
- C: 7529C57E
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T06:12:45Z
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
The project implements an AI-driven metadata generation pipeline for Maven plugin workflows, where source files are analyzed by type to trigger dynamic field creation. Core functionality is centered around AI text inference via local LLMs such as llama.cpp, with prompt configurations and execution scopes tailored per file type. A pluggable provider architecture enables flexible integration of AI generation pipelines, including mock providers for deterministic testing. The system organizes these capabilities into layered components: from high-level routing and configuration to low-level model execution and prompt mapping.

#### Packages
- main/java/net/ladenthin/maven/llamacpp/aiindex/config — This package enables AI-driven field generation in Maven plugin workflows by defining model configurations, prompt mappings, and execution scopes tailored to source file types.
- main/java/net/ladenthin/maven/llamacpp/aiindex — This package enables AI-driven field generation and text inference in Maven plugin workflows by configuring and executing local LLMs (like llama.cpp) to generate source metadata based on file type and prompt templates.
- main/java/net/ladenthin/maven/llamacpp/aiindex/provider — This package enables pluggable, configurable AI text generation from document prompts using local LLMs like llama.cpp, with support for mocking and deterministic testing of AI response pipelines.
- main/java/net/ladenthin/maven/llamacpp — This package enables AI-driven source metadata generation in Maven plugin workflows by routing file-specific prompts to local LLMs or mock providers for dynamic field creation based on file type.
- main/java/net/ladenthin/maven — This package enables AI-driven source metadata generation in Maven plugin workflows by routing file-specific prompts to local LLMs or mock providers for dynamic field creation based on file type.
- main/java/net/ladenthin — This package enables AI-driven source metadata generation in Maven plugin workflows by routing file-specific prompts to local LLMs or mock providers for dynamic field creation based on file type.
- main/java/net — This package enables AI-driven source metadata generation in Maven plugin workflows by routing file-specific prompts to local LLMs or mock providers for dynamic field creation based on file type.
- main/java — This package enables AI-driven metadata generation for source files in Maven plugin workflows by dynamically creating field definitions based on file type using local LLMs or mock providers.
- main — This package enables AI-driven metadata generation for source files in Maven plugin workflows by dynamically creating field definitions based on file type using local LLMs or mock providers.
- . — This package enables AI-driven metadata generation for source files in Maven plugin workflows by dynamically creating field definitions based on file type using local LLMs or mock providers.
