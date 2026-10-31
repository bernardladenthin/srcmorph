### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:11:44Z
- G: 
- A: 0.0.0
- X: file
---
> Builds safe OpenAI-compatible chat-completions requests and converts remote responses and errors into generation results.

#### Purpose
- Provides network-independent support for an OpenAI-compatible AI provider: request serialization, endpoint construction, API-key handling, HTTP status checks, error messages, and timing calculation.
- Keeps key transport restrictions and response calculations separately testable from socket handling.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private class; annotated `@ToString`.
- No superclass or implemented interfaces.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup function.
- Request construction consumes `AiGenerationRequest` fields and model configuration, including sampling settings and prompt definitions.
- API-key lookup reads the configured environment variable; key transport validation consumes an endpoint.
- Timing calculation consumes completion text, `ChatResponse` usage, and elapsed seconds.

#### Output
- Produces serialized JSON request bodies, chat-completions URLs, optional API keys, status and loopback decisions, error-message strings, and `AiGenerationTimings`.
- No network calls or mutable result state.

#### Core logic
- Builds a non-streaming chat-completions JSON body with system and user messages; includes model and reasoning effort only when non-blank.
- Always includes `max_tokens`, `temperature`, `top_p`, and `repetition_penalty`; includes `top_k` and `min_p` only when positive, `seed` when nonnegative, and `stop` when non-empty.
- Trims `baseUrl`, accepts only `http://` or `https://`, removes trailing slashes, then appends `/chat/completions`.
- Looks up and trims the configured API key; returns `null` when the variable name or value is blank or unset.
- Allows a configured key over HTTPS or plain HTTP only for recognized loopback hosts; rejects cleartext key transmission to other hosts.
- Classifies HTTP 200–299 as success and limits error-body excerpts to 500 characters, appending `...` when truncated.
- Computes decode rate as completion tokens divided by elapsed seconds when positive; reports prefill rate as zero.

#### Dependencies
- `LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`
- `ChatResponse`, `Usage`, `Java8CompatibilityHelper`
- Jackson `ObjectMapper`, `ArrayNode`, `ObjectNode`
- Java `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`
- Lombok `ToString`; JSpecify `Nullable`

#### Exceptions / Errors
- Constructor rejects null configuration, prompt support, or environment lookup with `NullPointerException`.
- `requestBody` may throw `IOException` during JSON serialization.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` may throw `IOException` for invalid endpoints or when a key would be sent over plain HTTP to a non-loopback host.
- `isLoopbackHost` recognizes exactly lowercase `localhost`, hosts matching the IPv4-shaped `127.x.x.x` regex, and `[::1]`; the regex does not validate octet ranges.
- `errorMessage` expects a non-null body; timing calculation expects a non-null response and usage.

#### Concurrency
- Instance dependencies and the `ObjectMapper` are held in final fields; no synchronization or shared mutable per-call state is shown.
