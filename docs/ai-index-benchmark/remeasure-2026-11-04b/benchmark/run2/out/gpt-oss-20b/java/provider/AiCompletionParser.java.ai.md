### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:01:28Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Strips a Gemma‑4 model’s internal “thinking” block from a raw LLM completion so only the final answer is stored.

#### Purpose
- Removes leading Gemma‑4 thinking block from model output.
- Validates presence of closing marker; throws `IOException` if missing.
- Returns trimmed answer text.

#### Type
- `public class AiCompletionParser`  
  - Modifiers: public  
  - Annotations: `@ToString`  
  - Supertypes: none  
  - Fields:  
    - `public static final String THINKING_BLOCK_START_MARKER`  
    - `public static final String THINKING_BLOCK_END_MARKER`  
  - Methods:  
    - `public AiCompletionParser()` – no‑op constructor  
    - `public String parseCompletion(final String response) throws IOException`

#### Input
- `parseCompletion(String response)`  
  - `response`: raw completion text, may be `null`.

#### Output
- `parseCompletion` returns a non‑null `String` containing the cleaned answer.
- Throws `IOException` if a start marker is present but no end marker is found.

#### Core logic
- If `response` is `null`, return `""`.
- Trim `response` to `trimmed`.
- If `trimmed` does not start with `THINKING_BLOCK_START_MARKER`, return `trimmed`.
- Compute `thinking` as substring after the start marker.
- Find `thinkingEnd` as index of `THINKING_BLOCK_END_MARKER` in `thinking`.
- If `thinkingEnd < 0`, throw `IOException` with message indicating token budget exhaustion.
- Return substring of `thinking` after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructs a parser instance.  
- `String parseCompletion(String response) throws IOException` – cleans a raw LLM completion.

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when a start marker is found but the end marker is missing, indicating token budget exhaustion.

#### Concurrency
- No concurrency constructs; class is stateless and thread‑safe.
