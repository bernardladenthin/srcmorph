### llamacpp-ai-index-maven-plugin
- H: 1.0
- C: D514A04D
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T23:43:12Z
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
The project is a Maven plugin framework that orchestrates AI‑based code‑analysis tasks using llama‑cpp models. Core functionality includes configuring the model, extracting facts from source files, routing tasks through file‑matching rules, and handling oversize inputs. A pluggable provider layer supplies text completions, with a native JNI backend for production and a mock provider for testing. The architecture ties together configuration, rule routing, fact extraction, and provider timing to deliver end‑to‑end AI analysis within Maven builds.

#### Packages
- main/java/net/ladenthin/maven/llamacpp/aiindex/config — A Maven plugin that configures, evaluates, and routes AI‑based code‑analysis tasks using llama‑cpp models, applying file‑matching rules, fact extraction, and oversize handling.
- main/java/net/ladenthin/maven/llamacpp/aiindex — A Maven plugin framework that configures, evaluates, and routes AI‑based code‑analysis tasks using llama‑cpp models, applying file‑matching rules, fact extraction, and oversize handling.
- main/java/net/ladenthin/maven/llamacpp/aiindex/provider — A framework for generating and timing AI‑based text completions, with a pluggable backend that supports a mock provider for testing and a native llama.cpp JNI provider for production.
- main/java/net/ladenthin/maven/llamacpp — A Maven plugin framework that orchestrates llama‑cpp based AI code‑analysis tasks, handling model configuration, fact extraction, rule routing, and oversize strategies.
- main/java/net/ladenthin/maven — A Maven plugin framework that orchestrates llama‑cpp based AI code‑analysis tasks, handling model configuration, fact extraction, rule routing, and oversize strategies.
- main/java/net/ladenthin — A Maven plugin framework that orchestrates llama‑cpp based AI code‑analysis tasks, handling model configuration, fact extraction, rule routing, and oversize strategies.
- main/java/net — A Maven‑plugin framework that orchestrates llama‑cpp based AI code‑analysis tasks, managing model config, fact extraction, rule routing, and oversize strategies.
- main/java — A Maven‑plugin framework that orchestrates llama‑cpp based AI code‑analysis tasks, managing model config, fact extraction, rule routing, and oversize strategies.
- main — A Maven‑plugin framework that orchestrates llama‑cpp based AI code‑analysis tasks, managing model config, fact extraction, rule routing, and oversize strategies.
- . — A Maven‑plugin framework that orchestrates llama‑cpp based AI code‑analysis tasks, managing model config, fact extraction, rule routing, and oversize strategies.
