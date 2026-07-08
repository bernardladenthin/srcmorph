### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-07-02T23:10:45Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 5; TODO/FIXME: 0; @Override: 0; methods (approx): 2; constructors: 1; field declarations (w/ modifier): 2

> Parses raw LLM completion text to extract the model answer by stripping any model‑internal thinking block before the result is stored in an AI index file.

#### Purpose
- Extracts final model answer from Gemma‑4 style completions.  
- Removes internal chain‑of‑thought blocks.  
- Validates token budget exhaustion.

#### Type
- Class, non‑final, public.  
- Annotated `@ToString` (Lombok).  
- No inheritance or interfaces.

#### Input
- `parseCompletion(String response)` receives raw model output, may be `null`.  
- No injected dependencies or external resources.

#### Output
- Returns a non‑null, trimmed answer string.  
- May throw `IOException` when a thinking block is started but not closed.

#### Core logic
- If `response` is `null` → return empty string.  
- Find last index of `THINKING_BLOCK_END_MARKER`; if found → return substring after it, trimmed.  
- If only `THINKING_BLOCK_START_MARKER` present → throw `IOException` with explanatory message.  
- Otherwise → return trimmed `response`.

#### Public API
- `AiCompletionParser()` → constructs an empty parser.  
- `String parseCompletion(String response) throws IOException` → cleans completion text.

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` if a thinking block is started but not closed, indicating token budget exhaustion.  

#### Concurrency
- Stateless; safe for concurrent use.
