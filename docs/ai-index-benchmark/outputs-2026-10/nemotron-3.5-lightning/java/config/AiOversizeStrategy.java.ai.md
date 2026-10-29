### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T22:58:54Z
- G: 
- A: 0.0.0
- X: file
---
> Configures how srcmorph handles source files that exceed an AI model's context window.

#### Purpose
- Defines strategies for handling source files larger than an AI model's context window in build-time code transformation.
- Provides configurable behaviors (fail, sample, mapReduce, deterministic) for oversized file processing.

#### Type
- enum AiOversizeStrategy with no modifiers, implicitly extends java.lang.Enum
- No generics or type bounds
- @Nullable annotation on fromConfig parameter (org.jspecify.annotations.Nullable)

#### Input
- fromConfig(@Nullable String value) receives the configured onOversize token
- null or blank input returns DEFAULT (FAIL)
- configValue field stored per constant

#### Output
- configValue() -> String returns the strategy's config token
- fromConfig(@Nullable String) -> AiOversizeStrategy returns matching strategy instance
- Throws IllegalArgumentException for unrecognized values

#### Core logic
- Four constants FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC each carry a config token string
- fromConfig trims and compares configValue.equalsIgnoreCase(trimmed) against known strategies
- null/blank yields DEFAULT; unrecognized throws IllegalArgumentException

#### Public API
- configValue() -> String returns the strategy's config token
- fromConfig(@Nullable String) -> AiOversizeStrategy parses config value into strategy
(2 public methods total)

#### Dependencies
- org.jspecify.annotations.Nullable (imported)
- net.ladenthin.srcmorph.config (declared package)

#### Exceptions / Errors
- Throws IllegalArgumentException if value is non-blank and matches no strategy
- Returns DEFAULT for null or blank input
- Null-handling via @Nullable parameter

#### Concurrency
- Immutable enum; safe for concurrent access without synchronization
