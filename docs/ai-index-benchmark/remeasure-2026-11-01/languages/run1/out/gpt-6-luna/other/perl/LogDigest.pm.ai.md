### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:15:19Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes web-server access logs into normalized per-host counts, per-status counts, and slow-request totals.

#### Purpose
- Module for parsing access-log lines and aggregating request statistics.
- Tracks host and status counts, busiest host count, and requests exceeding the slow-request threshold.

#### Type
- Module; package `LogDigest`.
- Procedural API with blessed `LogDigest` objects created by `new`.
- Optional exports: `parse_line`, `top_n` (`@EXPORT_OK`).

#### Input
- `new` accepts a `level` argument; defaults to `DEFAULT_LEVEL`.
- `add` accepts one access-log line per call.
- `parse_line` accepts a log line matching the module’s fixed pattern: host, timestamp, method, path, three-digit status, and integer milliseconds.
- No command-line options, `@ARGV`, `%ENV`, or files/handles read.

#### Output
- `parse_line` returns a hash reference containing `host`, `time`, `method`, `path`, `status`, and `millis`; returns false for nonmatching lines.
- `add` returns `1` for a parsed line and `0` for a nonmatching line.
- `summarize` returns a hash reference with UTC `generated` timestamp, `total`, distinct `hosts`, `busiest`, `slow`, and copied `statuses` counts.
- `top_n` returns keys sorted by descending count, then ascending key.
- No printed output or files written.

#### Core logic
- `new` validates the requested log level and initializes empty host/status counts, slow-request count, and line count.
- `parse_line` extracts access-log fields; lowercases the host and removes one trailing dot.
- `add` increments the line counter and rejects input beyond `MAX_LINES`; valid lines increment host and status counts.
- A request is slow only when its `millis` value is greater than `SLOW_REQUEST_MS`.
- `summarize` calculates totals, distinct hosts, maximum per-host count, slow count, copied status counts, and current UTC generation time.
- `top_n` sorts count keys by descending count with lexical tie-breaking.

#### Subs
- `new(args) -> object` — validate level and initialize counters.
- `parse_line(line) -> hashref or false` — parse and normalize one log line.
- `add(line) -> boolean` — count one valid request.
- `summarize() -> hashref` — build aggregate statistics.
- `top_n(counts, n) -> list` — return highest-count keys.
- `_normalize_host(host) -> string` — lowercase host and strip trailing dot.

#### Constants and configuration
- `MAX_LINES => 50_000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`
- `$VERSION = '1.4'`
- `@EXPORT_OK = qw(parse_line top_n)`
- `%LEVEL_RANK = (DEBUG => 0, INFO => 1, WARN => 2, ERROR => 3)`

#### Dependencies
- `strict`, `warnings` — enable compile-time and runtime checks.
- `POSIX` — imports `strftime`.
- `List::Util` — imports `max`, `sum`.
- `Carp` — imports `croak`.
- `parent` — declares `Exporter` inheritance.

#### Exceptions / Errors
- `new` croaks with `unknown level '<level>'` if the level is not in `%LEVEL_RANK`.
- `add` croaks with `more than 50000 lines` when the line count exceeds `MAX_LINES`.
