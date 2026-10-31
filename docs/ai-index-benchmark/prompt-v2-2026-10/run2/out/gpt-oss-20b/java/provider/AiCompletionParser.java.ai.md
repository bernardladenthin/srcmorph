### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:50:44Z
- G: 
- A: 0.0.0
- X: file
---
> Strips Gemma‑4 style chain‑of‑thought blocks from LLM completions, returning only the final answer.

#### Purpose
- Cleans raw LLM completion text.
- Removes internal reasoning blocks before persisting results.

#### Type
`public class AiCompletionParser`  
- Modifiers: public  
- Annotations: `@ToString`  
- No superclass or interfaces.

#### Input
- `parseCompletion(String response)`  
  - `response`: raw completion text, may be `null`.

#### Output
- `parseCompletion` returns a non‑null trimmed answer string.  
- Throws `IOException` if a thinking block starts but no closing marker is found.

#### Core logic
- If `response` is `null`, return empty string.  
- Trim whitespace; if it does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.  
- Otherwise, locate first `THINKING_BLOCK_END_MARKER` after the start marker.  
- If absent, throw `IOException` with explanatory message.  
- Return substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` → constructs parser.  
- `String parseCompletion(String response) throws IOException` → cleans completion.

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block is started but not closed due to token budget exhaustion.

#### Concurrency
- Stateless; thread‑safe.
