### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:35:51Z
- G: 
- A: 0.0.0
- X: file
---
> Configures parameters for a single AI generation step including model path, sampling settings, and resource limits.

#### Purpose
- Mutable configuration object for a single AI generation step carrying model path, sampling parameters, retry policy, input trimming limits, and stop strings.
- Bridges Maven configuration layer to AI provider implementations for indexing and summarization.

#### Type
- Class, public, mutable, no abstract, no inheritance.

#### Input
- No constructor parameters; no injected dependencies; fields hold runtime state.

#### Output
- Getters return configuration values; setters mutate state; no side effects.

#### Core logic
- Stores configuration defaults; provides read/write access via getters and setters; no computation beyond defaults.

#### Public API
- Getters and setters for 44 configuration fields (88 methods total).

#### Dependencies
- java.util.ArrayList, java.util.Collections, java.util.List, lombok.ToString, org.jspecify.annotations.Nullable; package net.ladenthin.srcmorph.config.

#### Exceptions / Errors
- No checked exceptions; @Nullable fields may be null; no explicit error conditions.

#### Concurrency
- Not thread-safe; mutable state managed by Maven plugin framework.
