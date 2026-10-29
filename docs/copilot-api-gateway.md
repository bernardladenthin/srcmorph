# GitHub Copilot models through the Copilot API Gateway

A GitHub Copilot subscription gives VS Code access to a list of hosted models (Claude, GPT, Gemini,
Grok and others). VS Code exposes them to extensions through its Language Model API only; there is
no public HTTP endpoint. The **GitHub Copilot API Gateway** extension
(`suhaibbinyounis.github-copilot-api-vscode`) puts an OpenAI-compatible server in front of that API
on the local machine, and srcmorph's `openai-compatible` provider talks to it like to any other
server.

Checked with gateway 2.15.3 in VS Code 1.143.1 on 2026-10-29.

> **Read this before you start.**
> - **The source leaves the machine.** Every file goes to GitHub Copilot and the vendor of the
>   model. srcmorph is local-first; this route is an explicit opt-in per model, like OpenRouter.
> - **Terms of use.** Copilot is licensed for interactive use in the editor. Accounts have been
>   restricted for automated or heavy use and for proxies; that has been reported for paid plans
>   too. A gateway built on the Language Model API is the lower-risk way, but it uses the same
>   quota and the same terms. Index small trees, not a monorepo overnight.
> - **Premium requests.** Every file is one request. Most models cost premium requests (the
>   multiplier, e.g. `0x`, `0.33x`, `1x`, is shown in the model picker of the Copilot chat), and a
>   plan has a monthly allowance.

## 1. Install and configure the gateway

Install the extension from the VS Code marketplace, then put this into the user `settings.json`:

```json
{
  "githubCopilotApi.server.host": "127.0.0.1",
  "githubCopilotApi.server.port": 3030,
  "githubCopilotApi.server.apiKey": "<a long random value>",
  "githubCopilotApi.server.maxConcurrentRequests": 2,
  "githubCopilotApi.server.rateLimitPerMinute": 10,
  "githubCopilotApi.mcp.enabled": false
}
```

Why each one:

| Setting | Why |
|---|---|
| `host` `127.0.0.1` | The default; only this machine can connect. Never bind it to `0.0.0.0`. |
| `apiKey` | **Mandatory.** The gateway answers every origin with `Access-Control-Allow-Origin: *`, so without a key any web page open in a browser on this machine can use it, including `POST /v1/tools/call`, which runs VS Code tools. With a key, a request without it gets `401`. |
| `maxConcurrentRequests` | srcmorph sends one request at a time; `2` leaves room for the one request that waits for the consent dialog (below). |
| `rateLimitPerMinute` | A cap against a runaway run; at a few seconds per file, 10 per minute is no brake. |
| `mcp.enabled` `false` | srcmorph needs no MCP servers. |

Do not start the gateway's Cloudflare tunnel: it publishes the server on the internet.

Generate the key once and keep it outside any repository, for example in a file only you can read.
srcmorph never reads a key from a configuration; it reads the environment variable the model names
in `apiKeyEnv`:

```bash
export COPILOT_GATEWAY_API_KEY="$(tr -d '\r\n ' < /path/to/your/key-file)"
```

Start the server from the gateway's panel and check it:

```bash
curl -s http://127.0.0.1:3030/health
# {"status":"ok","service":"github-copilot-api-vscode","copilot":"available","models":21}
curl -s -o /dev/null -w "%{http_code}\n" http://127.0.0.1:3030/v1/models   # 401 without the key
```

**The first request asks for consent.** VS Code shows a dialog asking whether the extension may use
the Copilot language models, possibly only after a while. The request that triggered it waits and
holds one of the `maxConcurrentRequests` slots; with `1`, every further request fails with
`429 Too many concurrent requests` until you allow it.

## 2. Configure srcmorph

[`examples/copilot-api-gateway/srcmorph.yaml`](../examples/copilot-api-gateway/srcmorph.yaml)
indexes `src/main/java` of one project with one model. Its model definition:

```yaml
aiDefinitions:
  - key: copilot
    provider: openai-compatible
    baseUrl: "http://127.0.0.1:3030/v1"
    remoteModel: claude-haiku-4.5
    apiKeyEnv: COPILOT_GATEWAY_API_KEY
    stream: true
    contextSize: 32768
    maxOutputTokens: 2048
    temperature: 0.2
```

Set `baseDirectory` and `outputDirectory`, then run it with the jar of
[`examples/build-your-own-jar`](../examples/build-your-own-jar): first as it is (`command: Plan`,
which lists the files and sends nothing), then with `command: GenerateFileIndex`.

```bash
java -jar examples/build-your-own-jar/target/srcmorph.jar examples/copilot-api-gateway/srcmorph.yaml
```

The key goes out over plain `http`, which srcmorph allows only to a loopback host; that is why
`baseUrl` names `127.0.0.1`.

### `stream: true` is required

The gateway has two paths for `POST /v1/chat/completions`, and only the streaming one is usable
(read in the 2.15.3 source and confirmed with requests):

- **Without streaming**, the gateway appends **every language-model tool registered in VS Code** to
  the request, the tools of Copilot and of other extensions, and **runs the tool calls** the model
  answers with (up to five rounds), without a chat session to confirm them in. The tool
  definitions alone overflow small models
  (`502 No lowest priority node found` from `gpt-4o-mini` with its 12k input) and exceed Gemini's
  limit (`502 Cannot have more than 128 tools per request`). Worse, srcmorph sends source code: a
  comment in an indexed file that reads like an instruction could make the model call one of
  those tools, which, depending on the installed extensions, read or edit files or run commands.
- **With streaming**, the gateway sends only the tools of the request itself (srcmorph sends none)
  and executes nothing.

So every Copilot model in srcmorph sets `stream: true`. srcmorph then reads the Server-Sent Events,
assembles them into one answer and fails the file if the stream ends without `data: [DONE]`.

## 3. Choose a model

The ids and their input limits come from the gateway:

```bash
curl -s -H "Authorization: Bearer $COPILOT_GATEWAY_API_KEY" http://127.0.0.1:3030/v1/models
```

Set `contextSize` to at most the model's `max_input_tokens`; srcmorph uses it to decide when a file
is too large. The premium multiplier is not in that list; look it up in the model picker of the
Copilot chat.

On 2026-10-29 these models answered through the streaming path with a well-formed summary of the
same C header (`ringbuf.h` of the benchmark corpus, 13 identifiers):

| `remoteModel` | `max_input_tokens` | Seconds (whole CLI run) | Identifiers named |
|---|---:|---:|---:|
| `gpt-4o-mini` | 12,078 | 4 | 11 of 13 |
| `gpt-6-luna` | 871,793 | 6 | 11 of 13 |
| `claude-sonnet-5.5` | 871,793 | 8 | 13 of 13 |
| `claude-haiku-4.5` | 127,790 | 9 | 11 of 13 |
| `gemini-3.8-flash` | 982,833 | 12 | 11 of 13 |

One file shows that a model works, not which one is best. For that, run the model-card benchmark
([ai-index-benchmark/MODEL-CARD.md](ai-index-benchmark/MODEL-CARD.md)) on the models you consider.
`mai-code-1.1-flash` (MAI-Code-1.1-Flash, `max_input_tokens` 127,790) ran it through this gateway on
2026-10-30: all 37 summaries of the three phases, 11.2 s per file, quality score 93 % (SQL and the
other files at the top, Java recall 75 %: it groups the accessors of large bean classes instead of
naming them).
Rules of thumb:

- **`gpt-4o-mini`** takes only about 12k input tokens: fine for small files, too small for long
  Java classes. Set `contextSize` to `12000` or less.
- **A cheap model for the file summaries**, a stronger one for the package and project summaries:
  the routing rules (`fieldGenerations`) can name a different model per phase.
- Prefer models with a `0x` or low multiplier for whole trees.

## 4. Limits of this route

- **Not reproducible.** The Language Model API has no seed parameter, and which sampling settings
  reach the model is up to the gateway and Copilot; expect two runs to give different summaries.
- **`maxOutputTokens` is not enforced.** A test request with `max_tokens` 20 got 89 tokens back, so
  srcmorph's cut-off warning (finish reason `length`) is not to be relied on either.
- **No retries.** A `429` from the gateway's rate limit or concurrency cap fails the file; run
  again with `force` off and only the missing files are generated.
- **Usage as the gateway reports it.** The timings use the token counts of the gateway's usage
  chunk; cached tokens are not reported.

## Troubleshooting

| Answer | Cause |
|---|---|
| `429 Too many concurrent requests` right at the start | The consent dialog in VS Code is waiting; allow it, or restart the server. |
| `401` | `apiKeyEnv` names no variable, the variable is empty, or the key differs from the gateway's `apiKey`. |
| `502 No lowest priority node found` / `Cannot have more than 128 tools` | `stream` is not `true`. |
| `The stream ended without 'data: [DONE]'` | The gateway or VS Code dropped the connection mid-answer; run again. |
| `Refusing to send the API key ... in cleartext` | `baseUrl` is plain `http` to a host that is not loopback. |
