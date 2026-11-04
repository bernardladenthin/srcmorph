### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:09:39Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Strips a Gemma‑4 style “thinking block” from raw LLM completion text, returning only the model’s final answer.

#### Purpose
- Removes internal reasoning from Gemma‑4 completions.
- Validates presence of closing marker; throws if truncated.

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
- If `response` is `null` → return empty string.  
- Trim `response` → `trimmed`.  
- If `trimmed` does not start with `THINKING_BLOCK_START_MARKER` → return `trimmed`.  
- Compute `thinking` = substring after start marker.  
- Find `thinkingEnd` = index of `THINKING_BLOCK_END_MARKER` in `thinking`.  
- If `thinkingEnd < 0` → throw `IOException` with message about token budget.  
- Return substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` → constructor, no-op.  
- `String parseCompletion(String response) throws IOException` → strip thinking block.

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when start marker present but end marker missing, with actionable message.

#### Concurrency
- No concurrency constructs; class is stateless.
