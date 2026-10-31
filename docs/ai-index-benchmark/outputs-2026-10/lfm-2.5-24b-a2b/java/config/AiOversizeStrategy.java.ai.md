### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:39:50Z
- G: 
- A: 0.0.0
- X: file
---
> This file defines strategies for handling source files that exceed the context window of their routed model, including abort, head sampling, chunked summarization, and deterministic emission.

#### Purpose
- Determines the action to take when a source file exceeds the context window of its routed model.

#### Type
- Kind: enum (public, implicitly final). No extends or implements. No generics. No annotations.

#### Input
- Constructor AiOversizeStrategy(String configValue)
- Static method fromConfig(String value)
- Private field configValue

#### Output
- configValue() returns the config token string
- fromConfig returns an AiOversizeStrategy instance
- DEFAULT constant (FAIL) as the default strategy

#### Core logic
- fromConfig: null or blank yields DEFAULT; trimmed value matched case-insensitively against each strategy's configValue; IllegalArgumentException for unknown values
- Enum constants store config strings: FAIL("fail"), SAMPLE("sample"), MAP_REDUCE("mapReduce"), DETERMINISTIC("deterministic")
- DEFAULT is set to FAIL

#### Public API
- configValue() -> String: returns the config token
- fromConfig(String) -> AiOversizeStrategy: parses the config value
- FAIL: abort strategy
- SAMPLE: head sampling strategy
- MAP_REDUCE: chunked summarization strategy
- DETERMINISTIC: deterministic body strategy
- DEFAULT: default strategy (FAIL)
- FAIL..DETERMINISTIC (4 total)

#### Dependencies
- org.jspecify.annotations.Nullable
- package net.ladenthin.srcmorph.config

#### Exceptions / Errors
- IllegalArgumentException for unknown non-blank config values
- No null pointer risks; configValue() always returns non-null

#### Concurrency
- Enum is immutable and thread-safe; no explicit synchronization needed
