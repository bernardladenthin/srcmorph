### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:49:59Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completion requests and enforces safe API-key delivery while interpreting HTTP results and generation timings.

#### Purpose
- Supports the non-network behavior of `OpenAiCompatibleAiGenerationProvider`: request construction, endpoint and API-key handling, status/error interpretation, and timing calculation.
- Keeps provider support logic independently testable.

#### Type
- Package-private `final` class `OpenAiCompatibleProviderSupport`.
- Annotated `@ToString`; excludes `promptSupport` and `environment` from generated string output.
- Holds `LlamaCppJniConfig`, `AiPromptSupport`, an environment lookup function, and a `Java8CompatibilityHelper`.

#### Input
- Constructor receives model configuration, prompt definitions, and an environment-variable lookup function; rejects null arguments.
- `requestBody` consumes `AiGenerationRequest` fields and configured model/sampling settings.
- `apiKeyFor` consumes an endpoint and looks up the configured environment variable.
- `timings` consumes completion text, `ChatResponse`, and elapsed seconds.
- Uses Jackson JSON nodes to construct request data.

#### Output
- Produces serialized chat-completion JSON, endpoint URLs, trimmed API keys or `null`, HTTP success decisions, error messages, and `AiGenerationTimings`.
- Refuses to send configured keys over plain HTTP to non-loopback hosts.

#### Core logic
- Builds a non-streaming request with system and user messages, token limit, temperature, top-p, and repetition penalty.
- Includes `model` and `reasoning.effort` only when non-blank; includes `top_k` and `min_p` only when positive, `seed` when non-negative, and `stop` when non-empty.
- Trims `baseUrl`, accepts only `http://` or `https://`, removes trailing slashes, and appends `/chat/completions`.
- Resolves `apiKeyEnv`; returns no key when the variable name or value is blank or unset.
- For plain HTTP, permits a configured API key only for exact `localhost`, IPv4 `127.x.x.x`, or bracketed IPv6 `[::1]` hosts.
- Treats HTTP statuses 200–299 as successful; formats failures with endpoint, status, and up to 500 body characters, appending `...` when truncated.
- Derives completion and prompt token counts from `ChatResponse.usage`; computes decode rate as completion tokens per positive elapsed second, otherwise zero; prefill rate is zero.

#### Dependencies
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.
- Lombok: `@ToString`.
- Llama values: `ChatResponse`, `Usage`.
- Project types: `LlamaCppJniConfig`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationTimings`.
- JSpecify: `@Nullable`.

#### Exceptions / Errors
- Constructor throws `NullPointerException` for null configuration, prompt support, or environment lookup.
- `requestBody` propagates `IOException` from JSON serialization.
- `endpoint` throws `IllegalArgumentException` when the trimmed base URL does not start with HTTP or HTTPS.
- `apiKeyFor` throws `IOException` for an unparsable endpoint or when a configured key would be sent over plain HTTP to a non-loopback host.
