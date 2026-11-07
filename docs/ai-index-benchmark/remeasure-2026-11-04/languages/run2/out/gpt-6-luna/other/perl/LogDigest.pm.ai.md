### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:00:37Z
- G: 
- A: 0.0.0
- X: file
- P: 4F9ECAEF
---
> Aggregates access-log entries into normalized per-host and per-status counts, including slow-request totals and busiest-host statistics.

#### Purpose
- Processes web-server access-log lines and produces aggregate traffic summaries.
- Provides helpers to parse entries and rank count keys.

#### Type
- Module: `LogDigest`; procedural functions and blessed-object API via `new`.
- `@EXPORT_OK`: `parse_line`, `top_n`; no default exports.

#### Input
- `new(level => ...)`: optional `level` argument; defaults to `DEFAULT_LEVEL`.
- `add($line)`: accepts an access-log line matching the module’s fixed pattern.
- `parse_line($line)`: accepts one access-log line.
- `top_n($counts, $n)`: accepts a count hash reference and requested number of keys.
- No command-line options, `%ENV` reads, or file reads.

#### Output
- `parse_line` returns a hash reference with `host`, `time`, `method`, `path`, `status`, and `millis`; returns false for non-matching lines.
- `add` returns `1` for a parsed entry and `0` for a non-matching line; updates object counts.
- `summarize` returns a hash reference with UTC `generated` timestamp, `total`, distinct `hosts`, `busiest` host count, `slow` request count, and copied `statuses` counts.
- `top_n` returns keys sorted by descending count, then ascending key.
- Prints nothing; writes no files.

#### Core logic
- `new` validates the requested log level and initializes host/status counts, slow-request count, and line count.
- `parse_line` extracts host, timestamp, method, path, status, and response time using a fixed access-log regular expression; lowercases the host and removes one trailing dot.
- `add` increments the line count, rejects input beyond `MAX_LINES`, skips malformed lines, and updates host and status counts.
- Counts a request as slow when its response time exceeds `SLOW_REQUEST_MS`.
- `summarize` computes total requests, distinct hosts, and maximum requests for one host; adds the UTC generation time and status-count copy.
- `top_n` orders keys by count descending with lexical tie-breaking.

#### Subs
- `new($class, %args) -> object` — validate level; initialize counters.
- `parse_line($line) -> hashref or false` — parse one access-log entry.
- `add($self, $line) -> 1 or 0` — count a valid entry.
- `summarize($self) -> hashref` — produce aggregate statistics.
- `top_n($counts, $n) -> list` — rank count keys.
- `_normalize_host($host) -> string` — lowercase and trim trailing dot.

#### Constants and configuration
- `MAX_LINES => 50_000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`
- `$VERSION = '1.4'`
- `@EXPORT_OK = qw(parse_line top_n)`
- `%LEVEL_RANK = (DEBUG => 0, INFO => 1, WARN => 2, ERROR => 3)`

#### Dependencies
- `strict`, `warnings` — enable strictness and warnings.
- `POSIX`: imports `strftime`.
- `List::Util`: imports `max`, `sum`.
- `Carp`: imports `croak`.
- `parent`: inherits from `Exporter`.

#### Exceptions / Errors
- `new` croaks with `unknown level '$level'` for an unrecognized level.
- `add` croaks with `more than 50000 lines` after the line limit is exceeded.
