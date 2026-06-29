### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:15:02Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Parses LLM completion text to extract model answer, stripping internal reasoning

#### Purpose
- Extracts and cleans model answers from LLM completions
- Handles Gemma-4 specific thinking block formatting

#### Type
- Class
- Non-final for subclassing and testing

#### Input
- Raw completion text (String)

#### Output
- Cleaned answer text (String)
- IOException for incomplete reasoning blocks

#### Core logic
- Identifies start and end markers of thinking blocks
- Removes internal reasoning while preserving the actual answer
- Handles edge cases (null input, exhausted token budget)

#### Public API
- `AiCompletionParser()` -> Constructor
- `parseCompletion(String response) -> String` -> Strips thinking block from response

#### Dependencies
- java.io.IOException
- lombok.ToString

#### Exceptions / Errors
- IOException when token budget is exhausted inside a thinking block

#### Concurrency
- Thread-safe due to immutable static final markers
