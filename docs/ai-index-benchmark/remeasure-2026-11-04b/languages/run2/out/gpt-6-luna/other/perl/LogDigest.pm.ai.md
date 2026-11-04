### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:16:01Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Aggregates web-server access-log entries into per-host, per-status, busiest-host, and slow-request counts.

#### Purpose
- Provides access-log parsing and aggregation for host and HTTP status statistics.
- Tracks requests exceeding a fixed duration threshold and produces a timestamped summary.

#### Type
- Module; package `LogDigest`.
- Exports `parse_line` and `top_n` via `@EXPORT_OK`.
- Procedural functions plus object-oriented aggregation through `new`.

#### Input
- `new` accepts named arguments; `level` sets the log level, defaulting to `DEFAULT_LEVEL`.
- `add` accepts a log line as a string; no file or handle is read by the module.
- No command-line options or `%ENV` keys.

#### Output
- `parse_line` returns a hash reference with `host`, `time`, `method`, `path`, `status`, and `millis`, or false for a non-matching line.
- `add` returns `1` for a parsed line, `0` for a non-matching line.
- `summarize` returns a hash reference containing `generated`, `total`, `hosts`, `busiest`, `slow`, and a copied `statuses` hash.
- `top_n` returns keys sorted by descending count, then alphabetically.
- No printed output, files written, or explicit exit statuses.

#### Core logic
- `new` validates the configured level and initializes host/status counts, slow-request count, and line count.
- `parse_line` matches a combined-style access-log line with a three-digit status and numeric duration; normalizes the host before returning parsed fields.
- `add` increments the line count, rejects input beyond `MAX_LINES`, ignores unparsable lines, and updates host, status, and slow-request counts.
- `summarize` computes total requests, distinct hosts, busiest-host count, slow requests, and status counts; stamps the result with current UTC time.
- `top_n` orders count keys by descending count and alphabetical tie-break, then selects up to `n` keys.
- `_normalize_host` lowercases the host and removes one trailing dot.

#### Subs
- `new($class, %args) -> object` — validate level; initialize counters.
- `parse_line($line) -> hashref or false` — parse and normalize one log line.
- `add($self, $line) -> 1 or 0` — count a valid log entry.
- `summarize($self) -> hashref` — build aggregate statistics.
- `top_n($counts, $n) -> list` — return highest-count keys.
- `_normalize_host($host) -> string` — lowercase and trim trailing dot.

#### Constants and configuration
- `MAX_LINES => 50_000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`
- `$VERSION = '1.4'`
- `@EXPORT_OK = qw(parse_line top_n)`
- `%LEVEL_RANK`: `DEBUG => 0`, `INFO => 1`, `WARN => 2`, `ERROR => 3`

#### Dependencies
- `strict`, `warnings` — enable strict checks and warnings.
- `POSIX`: imports `strftime`.
- `List::Util`: imports `max`, `sum`.
- `Carp`: imports `croak`.
- `parent`: inherits from `Exporter`.

#### Exceptions / Errors
- `new` croaks with `unknown level '<level>'` when `level` is not in `%LEVEL_RANK`.
- `add` croaks with `more than 50000 lines` when the line count exceeds `MAX_LINES`.
