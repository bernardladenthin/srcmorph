### llamacpp-ai-index-maven-plugin
- H: 1.0
- C: C0C95CB3
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T06:40:27Z
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
The project implements an AI-driven source code indexing pipeline for Maven plugins, centered around local Llama.cpp inference for field generation and code summarization. Core functionality is organized into a provider layer that handles text generation and response parsing via llama.cpp JNI bindings or mock implementations, enabling pluggable backends. Configuration and orchestration of model settings, language-specific prompts, and per-file or per-package summarization are managed through the AI index configuration subsystem, which integrates with the main execution flow to drive AI-driven field creation. Together, these components form a modular, extensible pipeline for AI-powered source code analysis within Maven-based builds.

#### Packages
- main/java/net/ladenthin/maven/llamacpp/aiindex/config — This package configures and orchestrates AI-driven field generation and model settings for Maven plugins using llama.cpp, enabling language-specific prompts and per-file or per-package summarization.
- main/java/net/ladenthin/maven/llamacpp/aiindex — This package enables AI-driven source code field generation and summarization in Maven plugins using llama.cpp, configuring model parameters and orchestrating local LLM inference through pluggable providers.
- main/java/net/ladenthin/maven/llamacpp/aiindex/provider — Enables local AI text generation and response parsing in a Maven-based indexing pipeline using llama.cpp JNI bindings or mock providers
- main/java/net/ladenthin/maven/llamacpp — Enables AI-driven field generation and source code summarization in Maven plugins using local Llama.cpp inference with configurable model parameters and pluggable generation backends.
- main/java/net/ladenthin/maven — Enables AI-driven field generation and source code summarization in Maven plugins using local Llama.cpp inference with configurable model parameters and pluggable generation backends.
- main/java/net/ladenthin — Enables AI-driven field generation and source code summarization in Maven plugins using local Llama.cpp inference with configurable model parameters and pluggable generation backends.
- main/java/net — Enables AI-driven field generation and source code summarization in Maven plugins using local Llama.cpp inference with configurable model parameters and pluggable generation backends.
- main/java — Enables AI-driven field generation and source code summarization in Maven plugins using local Llama.cpp inference with configurable model parameters and pluggable generation backends.
- main — Enables AI-driven field generation and source code summarization in Maven plugins using local Llama.cpp inference with configurable model parameters and pluggable generation backends.
- . — Enables AI-driven field generation and source code summarization in Maven plugins using local Llama.cpp inference with configurable model parameters and pluggable generation backends.
