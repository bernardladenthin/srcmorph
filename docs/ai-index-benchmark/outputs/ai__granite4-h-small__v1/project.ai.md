### llamacpp-ai-index-maven-plugin
- H: 1.0
- C: BFEC59A9
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:38:26Z
- G: 1.0.3-SNAPSHOT
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
The llamacpp‑ai‑index‑maven‑plugin provides a Maven‑centric framework for AI‑driven code insights. It analyses Java source files, applies declarative rules to select an appropriate LlamaCPP model, and runs inference via JNI while extracting deterministic facts that drive prompt construction. A pluggable provider layer exposes local llama.cpp bindings, a deterministic mock, and utilities for parsing responses, recording timing metrics, and building providers from configuration. The plugin aggregates these components to automatically generate clean AI completions and capture detailed performance metrics for each source file.

#### Packages
- main/java/net/ladenthin/maven/llamacpp/aiindex/config — A Maven plugin configuration library that models AI‑generation rules, file‑matching conditions, fact extraction, and runtime AI settings for the LlamaCPP indexer.
- main/java/net/ladenthin/maven/llamacpp/aiindex — A Maven plugin that indexes source files for AI‑generation, routing each file to a suitable LlamaCPP model based on declarative rules and extracting deterministic facts to drive prompt construction.
- main/java/net/ladenthin/maven/llamacpp/aiindex/provider — Provides a pluggable AI generation engine for Maven AI‑index, exposing local llama.cpp bindings and a deterministic mock, along with utilities to parse responses, record timing metrics, and build providers from configuration.
- main/java/net/ladenthin/maven/llamacpp — A Maven plugin that analyses Java source files, selects a suitable LlamaCPP model based on declarative rules, extracts deterministic facts, runs inference via JNI, and returns cleaned AI completions with timing data.
- main/java/net/ladenthin/maven — A Maven plugin that automatically selects and runs a LlamaCPP model to generate AI‑based code insights, returning clean completions and timing metrics.
- main/java/net/ladenthin — A Maven plugin that selects and runs a LlamaCPP model to generate AI‑based code insights, returning clean completions and timing metrics.
- main/java/net — A Maven plugin that selects a suitable LlamaCPP model for each source file, runs inference via JNI, parses the completion, and records timing metrics for AI‑based code insights.
- main/java — A Maven plugin that selects an appropriate LlamaCPP model for each source file, runs inference via JNI, parses the completion, and records timing metrics for AI‑based code insights.
- main — A Maven plugin that automatically selects and runs LlamaCPP models on Java source files, parses their completions, and captures detailed performance metrics for AI‑driven code insights.
- . — A Maven plugin that selects and runs LlamaCPP models on Java source files, parses their completions, and records detailed performance metrics for AI‑driven code insights.
