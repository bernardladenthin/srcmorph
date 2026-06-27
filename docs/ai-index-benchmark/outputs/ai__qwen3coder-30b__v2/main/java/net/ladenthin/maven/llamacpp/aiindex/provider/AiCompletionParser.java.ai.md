### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T23:16:16Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Parses LLM completion text to extract model answers by stripping Gemma-4 thinking blocks.

#### Purpose
- Strips internal reasoning blocks from LLM responses
- Extracts clean model answers for AI index storage

#### Type
class public final

#### Input
- `response` String parameter in `parseCompletion` method

#### Output
- Cleaned response String from `parseCompletion` method
- IOException thrown when thinking block is incomplete

#### Core logic
- Check if response contains thinking block end marker
- If found, return text after end marker trimmed
- If start marker present but no end marker, throw IOException
- Otherwise return trimmed response

#### Public API
- `parseCompletion(response) -> String` extract clean answer from LLM response
- `AiCompletionParser()` create new parser instance

#### Dependencies
none

#### Exceptions / Errors
- IOException thrown when thinking block starts but doesn't end

#### Concurrency
none
