### llamacpp-ai-index-maven-plugin
- H: 1.0
- C: 632FEE1C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T18:43:34Z
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
The project orchestrates local Large Language Model text generation using native C++ bindings via JNI to index and summarize Java source files while bridging Maven POM definitions with runtime inference configurations. This architecture manages native Java-GGUF inference through a pluggable Llama.cpp backend that enforces deterministic mock testing and supports configurable sampling parameters alongside context window management. Field-specific generation rules are applied during the processing of Java code to produce accurate summaries, ensuring seamless integration between model definitions and file extension mappings for AI document indexing.

#### Packages
- main/java/net/ladenthin/maven/llamacpp/aiindex/config — Provides Llama.cpp inference configuration and field generation selection logic for Maven plugins, mapping file extensions to AI models while managing context windows, sampling parameters, and retry policies.
- main/java/net/ladenthin/maven/llamacpp/aiindex — Provides local AI document indexing via JNI-backed Llama.cpp inference, configuring model paths, sampling parameters, and field-specific generation rules for Java source code processing.
- main/java/net/ladenthin/maven/llamacpp/aiindex/provider — Provides a pluggable local LLM inference backend using JNI-backed llama.cpp for AI document processing, supporting deterministic mock testing and configurable sampling parameters.
- main/java/net/ladenthin/maven/llamacpp — Provides local AI document indexing via JNI-backed Llama.cpp inference, configuring model paths, sampling parameters, and field-specific generation rules for Java source code processing.
- main/java/net/ladenthin/maven — Provides local AI document indexing via JNI-backed Llama.cpp inference, configuring model paths, sampling parameters, and field-specific generation rules for Java source code processing.
- main/java/net/ladenthin — Orchestrates local LLM text generation using native llama.cpp bindings to index and summarize source files, bridging Maven POM definitions with runtime inference configurations.
- main/java/net — Orchestrates local LLM text generation using native llama.cpp bindings to index and summarize source files, bridging Maven POM definitions with runtime inference configurations.
- main/java — Manages native Java-GGUF inference via JNI to generate text from source files with configurable sampling parameters.
- main — Orchestrates local Large Language Model (LLM) text generation and summarization using native C++ bindings via JNI, bridging Maven model definitions with runtime inference configurations.
- . — Orchestrates local Large Language Model (LLM) text generation and summarization using native C++ bindings via JNI, bridging Maven model definitions with runtime inference configurations.
