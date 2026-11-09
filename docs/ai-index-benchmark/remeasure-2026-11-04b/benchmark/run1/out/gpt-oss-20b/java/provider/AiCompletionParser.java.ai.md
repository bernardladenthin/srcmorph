### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:52:27Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Strips a leading Gemma‑4 thinking block from LLM completion text, returning only the model’s final answer.

#### Purpose
- Removes internal reasoning from Gemma‑4 responses before persisting.
- Validates presence of closing marker, throws `IOException` if missing.

#### Type
- `public class AiCompletionParser`  
  - Modifiers: public  
  - Annotations: `@ToString`  
  - Supertypes: none  
  - Constants:  
    - `public static final String THINKING_BLOCK_START_MARKER`  
    - `public static final String THINKING_BLOCK_END_MARKER`

#### Input
- `parseCompletion(String response)`  
  - `response`: raw LLM completion text, may be `null`.

#### Output
- `parseCompletion` returns a non‑null `String` containing the trimmed answer text.
- Throws `IOException` if a start marker is present but no end marker is found.

#### Core logic
- If `response` is `null`, return `""`.
- Trim `response` to `trimmed`.
- If `trimmed` does not start with `THINKING_BLOCK_START_MARKER`, return `trimmed`.
- Extract substring after the start marker into `thinking`.
- Find first occurrence of `THINKING_BLOCK_END_MARKER` in `thinking` as `thinkingEnd`.
- If `thinkingEnd < 0`, throw `IOException` with message indicating token budget exhaustion.
- Return substring of `thinking` after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` -> no-op constructor  
- `String parseCompletion(String response) throws IOException` -> clean answer text

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when a start marker is present but the end marker is missing, with a detailed message including response length.

#### Concurrency
- No concurrency constructs; class is stateless.
