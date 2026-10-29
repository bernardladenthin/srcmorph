### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:13:43Z
- G: 
- A: 0.0.0
- X: file
---
> Parses and extracts model answers from raw LLM completion text by removing internal reasoning blocks.

#### Purpose
- Extracts clean model answers from completion text
- Handles Gemma-4 specific thinking block format

#### Type
- Class
- Non-final for subclassing and testing
- Uses Lombok @ToString annotation

#### Input
- Raw completion text (String)
- Null input treated as empty response

#### Output
- Cleaned answer text (String)
- IOException thrown if token budget exhausted

#### Core logic
- Checks for null input
- Trims input
- Identifies and removes Gemma-4 thinking block
- Handles cases where block is absent or incomplete
- Returns cleaned answer or throws exception

#### Public API
- `AiCompletionParser()` -> Constructor
- `parseCompletion(String) -> String` -> Strips thinking block and returns answer

#### Dependencies
- java.io.IOException
- lombok.ToString

#### Exceptions / Errors
- IOException when token budget exhausted
- Null input treated as empty response

#### Concurrency
- Thread-safe due to immutable static final fields
