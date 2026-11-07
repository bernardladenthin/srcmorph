### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:53:56Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Parses raw LLM completion text to extract the model answer by stripping any model‑internal thinking block before the result is stored in an AI index file.

#### Purpose
- Removes Gemma‑4 thinking blocks from model responses.
- Validates presence of closing marker; throws error if missing.

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
  - `response`: raw completion text, may be `null`.

#### Output
- `parseCompletion` returns `String` (never `null`).  
- Throws `IOException` if a start marker is present but no end marker.

#### Core logic
- If `response` is `null`, return empty string.  
- Trim `response` to `trimmed`.  
- If `trimmed` does not start with `THINKING_BLOCK_START_MARKER`, return `trimmed`.  
- Extract substring after start marker into `thinking`.  
- Find first occurrence of `THINKING_BLOCK_END_MARKER` in `thinking` → `thinkingEnd`.  
- If `thinkingEnd < 0`, throw `IOException` with message about token budget exhaustion.  
- Return substring of `thinking` after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` → no-op constructor.  
- `String parseCompletion(String response) throws IOException` → strip leading thinking block.

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` thrown when start marker present but end marker missing, with actionable message.

#### Concurrency
- No concurrency constructs; class is stateless.
