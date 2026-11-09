### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:25:21Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
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
    - `public String parseCompletion(final String response) throws IOException` – core parsing logic

#### Input
- `response: String` (may be `null`) – raw LLM completion text.

#### Output
- `String` – cleaned answer text, never `null`.  
- Throws `IOException` if a start marker is present but no end marker is found.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace from `response`.
- If trimmed text does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Extract substring after the start marker.
- Find first occurrence of `THINKING_BLOCK_END_MARKER` in that substring.
- If not found, throw `IOException` with message indicating token budget exhaustion.
- Return substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructs parser.  
- `String parseCompletion(final String response) throws IOException` – parses completion, removes leading thinking block.

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when a start marker is present but the end marker is missing, indicating token budget exhaustion.

#### Concurrency
- No thread‑safety concerns; class is stateless.
