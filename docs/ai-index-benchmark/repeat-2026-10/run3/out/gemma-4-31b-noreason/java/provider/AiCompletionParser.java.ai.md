### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:27:15Z
- G: 
- A: 0.0.0
- X: file
---
> Extracts the final answer from LLM completion text by stripping leading chain-of-thought reasoning blocks.

#### Purpose
* Removes internal model reasoning (thinking blocks) from raw AI responses before persistence.
* Validates that reasoning blocks are properly closed to prevent storing truncated garbage.

#### Type
Class; non-final. Annotated with @ToString.

#### Input
* `response` (String): Raw completion text from the model (nullable).

#### Output
* `String`: The cleaned answer text (non-null).
* `IOException`: Thrown if a thinking block starts but never ends.

#### Core logic
* Handles `null` input by returning an empty string.
* Trims the input `response`.
* Checks if the trimmed text starts with `THINKING_BLOCK_START_MARKER` ("<thought").
* If no start marker is present, returns the trimmed text as-is.
* If a start marker is present, searches for the first occurrence of `THINKING_BLOCK_END_MARKER` ("</thought").
* If the end marker is missing, throws an `IOException` indicating token budget exhaustion.
* If the end marker is found, returns the trimmed text following the end marker.

#### Public API
* `AiCompletionParser()` -> void: Default constructor.
* `parseCompletion(String)` -> String: Strips leading thinking block from response.
* `THINKING_BLOCK_START_MARKER` -> String: Constant for block start token.
* `THINKING_BLOCK_END_MARKER` -> String: Constant for block end token.

#### Dependencies
* java.io.IOException
* lombok.ToString

#### Exceptions / Errors
* `IOException`: Thrown when `THINKING_BLOCK_START_MARKER` is found but `THINKING_BLOCK_END_MARKER` is absent.
