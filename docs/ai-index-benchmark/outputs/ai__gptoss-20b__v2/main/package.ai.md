### main
- H: 1.0
- C: 0D4DE298
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T22:36:13Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
---
> A Maven‑based AI field‑generation framework mapping prompts to Llama‑Cpp models with pluggable JNI or mock providers.

#### Purpose
- Enables field‑generation in AI applications using Llama‑Cpp.  
- Supports flexible provider configuration (JNI, mock).

#### Responsibilities
- **Prompt‑Model Mapping** – translates user prompts into specific Llama‑Cpp model calls.  
- **Provider Abstraction** – offers a common interface for JNI or mock implementations.

#### Key units
- **Framework Core** – orchestrates prompt mapping and provider selection.  
- **Provider Interface** – defines contract for JNI and mock providers.

#### Data flow
1. Prompt received from user/API.  
2. Framework Core selects appropriate model.  
3. Provider Interface invokes Llama‑Cpp via JNI or mock.  
4. Result returned to caller.

#### Dependencies
- Llama‑Cpp native library via JNI.  
- Maven build system for dependency management.  
- Optional mock provider for testing.

#### Cross-cutting
- Pluggable provider pattern for extensibility.  
- Mock support for isolated testing and CI.
