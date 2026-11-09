### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:55:02Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Strips a Gemma‑4 model’s internal thinking block from a raw LLM completion, returning only the final answer.

#### Purpose
- Removes leading Gemma‑4 thinking block from model output.
- Validates presence of closing marker; throws `IOException` if missing.
- Handles `null` input as empty string.

#### Type
- `public class AiCompletionParser`  
  - Modifiers: public  
  - Annotations: `@ToString`  
  - Supertypes: none  
  - Fields:  
    - `public static final String THINKING_BLOCK_START_MARKER` – marker for block start.  
    - `public static final String THINKING_BLOCK_END_MARKER` – marker for block end.  
  - Methods:  
    - `public AiCompletionParser()` – no‑op constructor.  
    - `public String parseCompletion(final String response) throws IOException` – core parsing logic.

#### Input
- `response: String` – raw completion text, may be `null`.

#### Output
- `String` – cleaned answer text, never `null`.  
- Throws `IOException` if a start marker is present but no end marker.

#### Core logic
- If `response` is `null`, return empty string.  
- Trim `response`.  
- If trimmed text does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.  
- Extract substring after start marker.  
- Find first occurrence of `THINKING_BLOCK_END_MARKER`.  
- If not found, throw `IOException` with detailed message.  
- Return substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructs parser.  
- `String parseCompletion(final String response) throws IOException` – parses completion.

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when a start marker is present but the end marker is missing, indicating token budget exhaustion.

#### Concurrency
- Stateless; thread‑safe.
