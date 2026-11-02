### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:33:42Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes web-server access logs into per-host and per-status request counts, including slow requests and busiest-host statistics.

#### Purpose
- Module for parsing access-log lines and accumulating request statistics.
- Produces a summary with totals, host counts, status counts, slow-request count, and generation time.

#### Type
- Module; package `LogDigest`; procedural API with constructor `new` and blessed objects.
- Optional exports: `@EXPORT_OK` contains `parse_line` and `top_n`.

#### Input
- `new` accepts the named argument `level`; defaults to `DEFAULT_LEVEL`.
- `add` accepts an access-log line string.
- `parse_line` accepts an access-log line string.
- `top_n` accepts a count hash reference and a number.
- Reads no command-line arguments, environment variables, files, or handles.

#### Output
- `parse_line` returns a hash reference with `host`, `time`, `method`, `path`, `status`, and `millis`; returns false for malformed lines.
- `add` returns `1` for a parsed line and `0` for a malformed line.
- `summarize` returns a hash reference containing UTC `generated` time (`%Y-%m-%dT%H:%M:%SZ`), `total`, unique-host count, `busiest`, `slow`, and a copy of `statuses`.
- `top_n` returns keys sorted by descending count, then ascending key.
- Prints no output and writes no files.

#### Core logic
- `new` validates the configured level and initializes host/status counts, slow-request count, and line count.
- `add` increments the line count and rejects input beyond `MAX_LINES`; parses the line, normalizes its host, increments host and status counts, and counts requests exceeding `SLOW_REQUEST_MS`.
- `parse_line` matches a common access-log-shaped record: host, timestamp, quoted method/path, three-digit status, and numeric duration in milliseconds.
- `summarize` computes total requests, unique hosts, busiest-host count, slow requests, copied status counts, and current UTC generation time.
- `top_n` orders count keys by count descending, with lexical ascending order as the tie-breaker.
- `_normalize_host` lowercases the host and removes one trailing dot.

#### Subs
- `new(args) -> object` — validate level and initialize counters.
- `parse_line(line) -> hashref|false` — parse access-log record.
- `add(line) -> 1|0` — accumulate parsed request statistics.
- `summarize() -> hashref` — build aggregate statistics.
- `top_n(counts, n) -> list` — return highest-count keys.
- `_normalize_host(host) -> host` — lowercase and trim trailing dot.

#### Constants and configuration
- `MAX_LINES => 50_000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`
- `$VERSION = '1.4'`
- `@EXPORT_OK = qw(parse_line top_n)`
- `%LEVEL_RANK`: `DEBUG => 0`, `INFO => 1`, `WARN => 2`, `ERROR => 3`

#### Dependencies
- `strict`, `warnings` — enable strict checks and warnings.
- `POSIX` — imports `strftime`.
- `List::Util` — imports `max`, `sum`.
- `Carp` — imports `croak`.

#### Exceptions / Errors
- `new` croaks with `unknown level '<level>'` when `level` is not in `%LEVEL_RANK`.
- `add` croaks with `more than 50000 lines` when the line limit is exceeded.
