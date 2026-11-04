### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:40:26Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Strips a Gemma‑4 style “thinking” block from an LLM completion before persisting the answer.

#### Purpose
- Removes internal reasoning from raw LLM responses.
- Validates presence of closing marker; throws on truncated output.

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
- `String` – cleaned answer, never `null`.  
- Throws `IOException` if a start marker is present but no end marker.

#### Core logic
- If `response` is `null`, return empty string.  
- Trim whitespace from `response`.  
- If trimmed text does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.  
- Extract substring after start marker.  
- Find first occurrence of `THINKING_BLOCK_END_MARKER`.  
- If absent, throw `IOException` with message indicating token budget exhaustion.  
- Return substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – instantiate parser.  
- `String parseCompletion(final String response) throws IOException` – parse and clean completion.

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` thrown when a start marker is found but the end marker is missing, indicating token budget exhaustion.

#### Concurrency
- Stateless; no shared mutable state. Thread‑safe.
