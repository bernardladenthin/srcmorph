### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:38:20Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web-server access-log entries into per-host and per-status counts, including slow-request totals.

#### Purpose
- Provides a Perl module for parsing access-log lines and accumulating a digest.
- Produces summary counts and ranked host or status keys.

#### Type
- Module; package `LogDigest`; procedural API with a `new` constructor that blesses an object.
- `@EXPORT_OK`: `parse_line`, `top_n`; no default exports.

#### Input
- `new(level => $level)`: optional `level`; defaults to `DEFAULT_LEVEL`.
- `add($line)`: access-log line in the expected format.
- `parse_line($line)`: access-log line in the expected format.
- `top_n($counts, $n)`: hash reference of counts and requested number of keys.
- Reads no command-line options, environment variables, files, or handles.

#### Output
- `parse_line` returns a hash reference with `host`, `time`, `method`, `path`, `status`, and `millis`; returns false for a nonmatching line.
- `add` returns `1` for a parsed line or `0` for an invalid line.
- `summarize` returns a hash reference containing UTC `generated` timestamp, total parsed requests, distinct host count, busiest-host count, slow-request count, and a copy of status counts.
- `top_n` returns keys sorted by descending count, with alphabetical tie-breaking.
- No direct printing, files written, or explicit exit statuses.

#### Core logic
- `new` validates the requested level and initializes host/status counters, slow count, and line count.
- `parse_line` matches a combined-log-style line with a three-digit status and numeric duration; normalizes the host to lowercase and removes a trailing dot.
- `add` increments the line count, rejects counts above `MAX_LINES`, ignores malformed lines, and updates host/status counts and the slow count for durations above `SLOW_REQUEST_MS`.
- `summarize` computes totals and busiest-host count, formats the generation time in UTC, and copies status counts.
- `top_n` sorts count keys descending by value, then ascending alphabetically.

#### Subs
- `new($class, %args) -> object` — validate level; initialize digest.
- `parse_line($line) -> hashref or false` — parse and normalize one log entry.
- `add($self, $line) -> 1 or 0` — count a valid entry; ignore malformed lines.
- `summarize($self) -> hashref` — build aggregate digest.
- `top_n($counts, $n) -> list` — rank count keys.
- `_normalize_host($host) -> string` — lowercase and trim trailing dot.

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
- `parent`: inherits from `Exporter`.
- `strict`, `warnings`: enable compile-time and runtime warnings/strictness.

#### Exceptions / Errors
- `new` croaks with `unknown level '$level'` for a level absent from `%LEVEL_RANK`.
- `add` croaks with `more than 50000 lines` when the line count exceeds `MAX_LINES`.
