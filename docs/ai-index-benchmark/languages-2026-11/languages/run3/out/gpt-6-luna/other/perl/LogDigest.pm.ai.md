### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:53:32Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web-server access-log entries into per-host and per-status request counts, including slow requests and busiest-host statistics.

#### Purpose
- Module for parsing access-log lines and accumulating a digest.
- Produces a summary with total requests, host count, busiest-host count, slow-request count, and status counts.

#### Type
- Module; package `LogDigest`.
- Procedural/object-oriented: `new` blesses a hash-based object; `add` and `summarize` operate on it.
- `@EXPORT_OK`: `parse_line`, `top_n`; no `@EXPORT`.

#### Input
- `new` accepts `level` as a named argument; defaults to `DEFAULT_LEVEL`.
- `add($line)` accepts one log line as an argument.
- `parse_line($line)` accepts one log line as an argument.
- No command-line options, `@ARGV`, `%ENV`, or file reads.

#### Output
- `parse_line` returns a hash reference with `host`, `time`, `method`, `path`, `status`, and `millis`; returns false for an unmatched line.
- `add` returns `1` for a parsed line and `0` for an unmatched line.
- `summarize` returns a hash reference containing `generated`, `total`, `hosts`, `busiest`, `slow`, and a copied `statuses` hash.
- `top_n` returns a list of keys ordered by descending count, then ascending key.
- No printed output or files written.

#### Core logic
- `new` validates the requested level and initializes host/status counters, slow-request count, and line count.
- `parse_line` matches the expected access-log format; extracts host, timestamp, method, path, status, and elapsed milliseconds.
- Parsed hosts are lowercased and have one trailing period removed.
- `add` increments the line count, rejects counts above `MAX_LINES`, ignores unmatched lines, and updates host/status counts.
- Requests taking more than `SLOW_REQUEST_MS` increment the slow-request count.
- `summarize` computes totals and busiest-host count, timestamps generation in UTC, and copies the status counts.
- `top_n` sorts count keys by count descending, with lexical tie-breaking.

#### Subs
- `new($class, %args) -> object` — initialize digest and validate level.
- `parse_line($line) -> hashref or false` — parse and normalize a log entry.
- `add($self, $line) -> 1 or 0` — count a valid entry.
- `summarize($self) -> hashref` — build aggregate statistics.
- `top_n($counts, $n) -> list` — rank count keys.
- `_normalize_host($host) -> string` — lowercase and trim trailing period.

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
- Pragmas: `strict`, `warnings`.

#### Exceptions / Errors
- `new` croaks with `unknown level '$level'` for a level absent from `%LEVEL_RANK`.
- `add` croaks with `more than 50000 lines` when the line count exceeds `MAX_LINES`.
