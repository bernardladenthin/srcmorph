### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:26:48Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web-server access logs into normalized per-host and per-status request counts, including slow requests.

#### Purpose
- Module for parsing access-log lines and accumulating traffic summaries.
- Supports ranking count keys with `top_n`.

#### Type
- Module; package `LogDigest`.
- Declares `@EXPORT_OK` names `parse_line`, `top_n`; no `Exporter` import is present.
- Object-oriented via `new` and `bless`; also provides procedural helpers.

#### Input
- `new` accepts `level` in its named arguments; defaults to `DEFAULT_LEVEL`.
- `add` accepts one access-log line.
- `parse_line` accepts a line matching the expected host, timestamp, method, path, status, and elapsed-milliseconds format.
- `top_n` accepts a count hash reference and a requested number.
- No command-line options, `@ARGV`, environment variables, or file/handle reads.

#### Output
- `parse_line` returns a hash reference containing `host`, `time`, `method`, `path`, `status`, and `millis`; returns false for nonmatching lines.
- `add` returns `1` for a parsed line, `0` for a nonmatching line; updates object counts.
- `summarize` returns a hash reference with UTC `generated` timestamp (`%Y-%m-%dT%H:%M:%SZ`), `total`, distinct-host count (`hosts`), maximum per-host count (`busiest`), slow-request count (`slow`), and copied status counts (`statuses`).
- `top_n` returns keys sorted by descending count, then ascending key.
- No direct printing or file writes.

#### Core logic
- Constructor validates the requested level against the configured level ranks and initializes host/status counters, slow count, and line count.
- `parse_line` extracts access-log fields and normalizes the host to lowercase without a trailing dot.
- `add` increments the line count, enforces `MAX_LINES`, ignores malformed lines, then increments host and status counts.
- Counts a request as slow when its elapsed milliseconds exceed `SLOW_REQUEST_MS`.
- `summarize` derives total requests, distinct hosts, busiest-host count, slow count, status totals, and generation time.
- `top_n` sorts count keys by count descending with lexical tie-breaking, then returns up to `n` keys.

#### Subs
- `new(class, %args) -> object` — validate level and initialize counters.
- `parse_line($line) -> hashref or false` — parse and normalize an access-log entry.
- `add($self, $line) -> 1 or 0` — enforce limit and accumulate parsed counts.
- `summarize($self) -> hashref` — build aggregate traffic summary.
- `top_n($counts, $n) -> list` — rank count keys and return top entries.
- `_normalize_host($host) -> host` — lowercase host and remove trailing dot.

#### Constants and configuration
- `MAX_LINES => 50_000`.
- `DEFAULT_LEVEL => 'WARN'`.
- `SLOW_REQUEST_MS => 1500`.
- `$VERSION = '1.4'`.
- `@EXPORT_OK = qw(parse_line top_n)`.
- `%LEVEL_RANK = (DEBUG => 0, INFO => 1, WARN => 2, ERROR => 3)`.

#### Dependencies
- `POSIX`: imports `strftime`.
- `List::Util`: imports `max`, `sum`.
- `Carp`: imports `croak`.
- `strict`, `warnings`: enable strictness and warnings.

#### Exceptions / Errors
- `new` croaks with `unknown level '<level>'` for an unrecognized level.
- `add` croaks with `more than 50000 lines` after the line limit is exceeded.
