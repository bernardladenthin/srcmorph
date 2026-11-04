### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:56:45Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Extracts the model’s final answer from a raw LLM completion by removing any leading Gemma‑4 thinking block.

#### Purpose
- Removes internal reasoning blocks from Gemma‑4 completions.
- Validates presence of closing marker; throws if truncated.

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
- `parseCompletion` receives `String response` (may be `null`).

#### Output
- Returns `String` – cleaned answer, never `null`.
- Throws `IOException` if a start marker is present but no end marker.

#### Core logic
- If `response` is `null`, return `""`.
- Trim `response` → `trimmed`.
- If `trimmed` does not start with `THINKING_BLOCK_START_MARKER`, return `trimmed`.
- Extract substring after start marker → `thinking`.
- Find first occurrence of `THINKING_BLOCK_END_MARKER` in `thinking` → `thinkingEnd`.
- If `thinkingEnd < 0`, throw `IOException` with message indicating token budget exhaustion.
- Return substring of `thinking` after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.  
- `String parseCompletion(final String response) throws IOException` – strips leading thinking block, validates markers.

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` thrown when start marker present but end marker missing, with actionable message.

#### Concurrency
- No concurrency constructs; class is stateless.
