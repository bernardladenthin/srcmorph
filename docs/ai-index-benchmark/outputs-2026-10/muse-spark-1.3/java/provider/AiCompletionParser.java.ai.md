### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:14:49Z
- G: 
- A: 0.0.0
- X: file
---
> Strips LLM chain-of-thought thinking blocks to persist only clean model answers in AI index files.

#### Purpose
- Parses raw LLM completion text for AI index storage.
- Removes leading Gemma-4 thinking-block reasoning, keeps final answer.

#### Type
- class, non-final, allows subclassing/mocking.
- Notable annotation: Lombok @ToString.

#### Input
- response String raw LLM completion text, may be null.
- Consumed constants THINKING_BLOCK_START_MARKER `<|channel>thought`, THINKING_BLOCK_END_MARKER `<channel|>`.

#### Output
- Return String cleaned answer text trimmed, never null.
- Empty String for null input.
- Throws on truncated reasoning to prevent persisting garbage.

#### Core logic
- Null response returns empty AiCompletionParser answer.
- Trims response, checks startswith THINKING_BLOCK_START_MARKER.
- Non-leading markers returned trimmed as part of answer text.
- Leading block: finds first THINKING_BLOCK_END_MARKER after start marker.
- Strips start marker through first end marker inclusive, returns remainder trimmed.
- Missing end marker after leading start marker signals token-budget exhaustion.

#### Public API
- `THINKING_BLOCK_START_MARKER -> String` - opens Gemma-4 thinking block.
- `THINKING_BLOCK_END_MARKER -> String` - closes Gemma-4 thinking block.
- `AiCompletionParser() -> AiCompletionParser` - creates new parser.
- `parseCompletion(String) -> String` - strips leading thinking block.

#### Dependencies
- java.io.IOException
- lombok.ToString
- net.ladenthin.srcmorph.provider.AiCompletionParser

#### Exceptions / Errors
- IOException when leading thinking block lacks closing marker, includes response length and maxOutputTokens hint.
- Null input handled as empty response, no exception.
