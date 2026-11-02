# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: Apache-2.0
"""Package 8i: a logging proxy in front of OpenRouter for the gpt-oss-20b runaway measurement.

srcmorph posts its chat completions here (baseUrl http://127.0.0.1:PORT/api/v1); the proxy merges the
JSON object in the file named by --variant into the body (provider routing, reasoning), forwards it to
openrouter.ai with the caller's Authorization header and appends one line per call to --log:
the provider that answered, finish_reason, the usage (reasoning tokens included) and the content length.

Usage: python -I proxy.py --port 8765 --variant variant.json --log calls.jsonl
The variant file is re-read on every request, so the runner switches variants without a restart.
"""
import argparse
import json
import threading
import time
import urllib.error
import urllib.request
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

UPSTREAM = "https://openrouter.ai"
LOCK = threading.Lock()


def merge(body, extra):
    for key, value in extra.items():
        if key.startswith("_"):
            continue
        if value is None:
            body.pop(key, None)
        elif isinstance(value, dict) and isinstance(body.get(key), dict):
            merge(body[key], value)
        else:
            body[key] = value
    return body


class Handler(BaseHTTPRequestHandler):
    def log_message(self, *args):
        pass

    def do_POST(self):
        raw = self.rfile.read(int(self.headers.get("Content-Length", 0)))
        with open(self.server.variant, encoding="utf-8") as f:
            variant = json.load(f)
        body = merge(json.loads(raw), variant)
        data = json.dumps(body).encode("utf-8")
        request = urllib.request.Request(UPSTREAM + self.path, data=data, method="POST", headers={
            "Authorization": self.headers.get("Authorization", ""),
            "Content-Type": "application/json",
            "Accept": self.headers.get("Accept", "application/json")})
        started = time.time()
        try:
            with urllib.request.urlopen(request, timeout=900) as response:
                status, payload = response.status, response.read()
                headers = response.headers
        except urllib.error.HTTPError as e:
            status, payload, headers = e.code, e.read(), e.headers
        except Exception as e:  # network: answer 502 so srcmorph retries
            status, payload, headers = 502, json.dumps({"error": {"message": str(e)}}).encode(), {}
        record = {"t": round(started, 1), "s": round(time.time() - started, 1), "status": status,
                  "variant": variant.get("_name", ""), "promptChars": sum(len(m.get("content", ""))
                                                                          for m in body.get("messages", []))}
        try:
            answer = json.loads(payload)
            choice = (answer.get("choices") or [{}])[0]
            message = choice.get("message") or {}
            usage = answer.get("usage") or {}
            record.update(provider=answer.get("provider"), finish=choice.get("finish_reason"),
                          nativeFinish=choice.get("native_finish_reason"),
                          prompt=usage.get("prompt_tokens"), completion=usage.get("completion_tokens"),
                          reasoning=(usage.get("completion_tokens_details") or {}).get("reasoning_tokens"),
                          cost=usage.get("cost"), contentChars=len(message.get("content") or ""),
                          reasoningChars=len(message.get("reasoning") or ""),
                          error=(answer.get("error") or {}).get("message"))
        except ValueError:
            record["error"] = payload[:300].decode("utf-8", "replace")
        with LOCK, open(self.server.log, "a", encoding="utf-8") as f:
            f.write(json.dumps(record) + "\n")
        self.send_response(status)
        content_type = headers.get("Content-Type", "application/json") if headers else "application/json"
        self.send_header("Content-Type", content_type)
        retry = headers.get("Retry-After") if headers else None
        if retry:
            self.send_header("Retry-After", retry)
        self.send_header("Content-Length", str(len(payload)))
        self.end_headers()
        self.wfile.write(payload)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--port", type=int, default=8765)
    parser.add_argument("--variant", required=True)
    parser.add_argument("--log", required=True)
    args = parser.parse_args()
    server = ThreadingHTTPServer(("127.0.0.1", args.port), Handler)
    server.variant, server.log = args.variant, args.log
    server.serve_forever()


if __name__ == "__main__":
    main()
