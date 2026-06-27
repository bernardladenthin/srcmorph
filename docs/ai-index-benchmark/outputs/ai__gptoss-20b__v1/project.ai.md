### llamacpp-ai-index-maven-plugin
- H: 1.0
- C: B269F9D9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T21:51:57Z
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
The llamacpp‑ai‑index‑maven‑plugin translates XML configuration into AI prompts, selects model settings via a configurable mapping framework, and generates clean, model‑driven index fields for documentation. Core subsystems include a configuration package that maps model settings, an AI index package that orchestrates prompt selection and llama.cpp execution, and a provider package that offers pluggable providers such as a JNI local llama.cpp provider, a deterministic mock, and parsing utilities for cleaning LLM output. The plugin invokes llama.cpp locally or through the mock provider, then parses and sanitises the completion text before injecting it into the generated documentation index. This design enables developers to integrate AI‑generated index content into Maven projects while maintaining control over model selection and output formatting.

#### Packages
- main/java/net/ladenthin/maven/llamacpp/aiindex/config — Provides a framework for configuring, selecting, and mapping AI model settings used by a Maven plugin to generate index fields.
- main/java/net/ladenthin/maven/llamacpp/aiindex — Enables Maven to generate index fields by selecting AI prompts and running llama.cpp locally or via a mock, producing clean text for documentation.
- main/java/net/ladenthin/maven/llamacpp/aiindex/provider — Provides pluggable AI text generation for Maven‑based llama.cpp indexing, featuring a JNI local provider, a deterministic mock, and parsing utilities to clean LLM output.
- main/java/net/ladenthin/maven/llamacpp — Generates index documentation fields by selecting AI prompts and executing llama.cpp or a mock provider to produce clean, model‑driven text.
- main/java/net/ladenthin/maven — Generates AI‑driven index fields for Maven projects using llama.cpp or a mock provider, converting configured prompts into clean, model‑produced text for documentation.
- main/java/net/ladenthin — Generates AI‑driven index fields for Maven projects, converting configured prompts into clean, model‑produced text for documentation.
- main/java/net — Generates AI‑driven index fields for Maven projects, converting configured prompts into clean, model‑produced text for documentation.
- main/java — Generates AI‑driven index fields for Maven projects, converting configured prompts into clean, model‑produced text for documentation.
- main — Generates AI‑based documentation index fields for Maven projects, translating XML configuration into prompts and extracting clean model completions.
- . — Generates AI‑driven Maven documentation index fields by parsing XML config, selecting model definitions, and invoking llama.cpp for clean completions.
