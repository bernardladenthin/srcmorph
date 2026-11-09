### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:03:41Z
- G: 
- A: 0.0.0
- X: file
- P: 4F9ECAEF
---
> Condenses web-server access-log entries into per-host, per-status, and slow-request counts.

#### Purpose
- Accumulates access-log statistics and produces a timestamped summary.
- Parses individual log lines and ranks count keys.

#### Type
- Module; package `LogDigest`; procedural API with object constructor `new`.
- `@EXPORT_OK`: `parse_line`, `top_n`; no default exports.

#### Input
- `new(level => ...)`: optional `level` argument; defaults to `DEFAULT_LEVEL`.
- `add($line)`: access-log line string; increments the line limit before parsing.
- `parse_line($line)`: access-log line string.
- `top_n($counts, $n)`: hash reference of counts and requested result count.
- No command-line options, environment variables, or file/handle reads.

#### Output
- `parse_line`: hash reference with normalized `host`, `time`, `method`, `path`, `status`, and `millis`; returns false for an unmatched line.
- `add`: `1` for a parsed entry, `0` for an unmatched line.
- `summarize`: hash reference containing UTC `generated` timestamp, `total`, distinct `hosts`, maximum per-host count `busiest`, `slow` count, and copied `statuses` counts.
- `top_n`: list of keys sorted by descending count, then ascending key.
- No printed output or files written.

#### Core logic
- `new` validates the requested log level and initializes host/status counts, slow-request count, and line count.
- `parse_line` matches the expected access-log format; lowercases the host and removes one trailing dot.
- `add` rejects input beyond `MAX_LINES`, ignores lines that do not parse, then updates host and status counts and increments `slow` when `millis > SLOW_REQUEST_MS`.
- `summarize` calculates total requests and busiest-host count, then returns those metrics, distinct-host count, statuses, and current UTC timestamp.
- `top_n` orders keys by descending count with lexical tie-breaking, then returns up to `n` keys.

#### Subs
- `new($class, %args) -> object` — validate level and initialize counters.
- `parse_line($line) -> hashref or false` — parse and normalize one access-log line.
- `add($self, $line) -> 1 or 0` — count a parsed entry.
- `summarize($self) -> hashref` — build aggregate statistics.
- `top_n($counts, $n) -> list` — rank count keys and select top entries.
- `_normalize_host($host) -> string` — lowercase host and trim trailing dot.

#### Constants and configuration
- `MAX_LINES => 50_000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`
- `$VERSION = '1.4'`
- `%LEVEL_RANK = (DEBUG => 0, INFO => 1, WARN => 2, ERROR => 3)`

#### Dependencies
- `POSIX`: imports `strftime`.
- `List::Util`: imports `max`, `sum`.
- `Carp`: imports `croak`.
- `parent`: inherits from `Exporter`.
- Pragmas: `strict`, `warnings`.

#### Exceptions / Errors
- `new` calls `croak` for a level not present in `%LEVEL_RANK`.
- `add` calls `croak` when the line count exceeds `MAX_LINES`.
