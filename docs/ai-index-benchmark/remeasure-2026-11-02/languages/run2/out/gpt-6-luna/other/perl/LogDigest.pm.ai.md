### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:39:51Z
- G: 
- A: 0.0.0
- X: file
- P: B953BC65
---
> Aggregates web-server access-log entries into per-host, per-status, busiest-host, and slow-request statistics.

#### Purpose
- Parses access-log lines and accumulates traffic statistics.
- Returns a timestamped summary and supports ranking counts.

#### Type
- Module; package `LogDigest`.
- Exporter module; `@EXPORT_OK`: `parse_line`, `top_n`.
- Procedural interface plus object-oriented instances via `new`.

#### Input
- `new(level => ...)`: optional `level` argument; defaults to `DEFAULT_LEVEL`.
- `add($line)`: access-log line, expected to match the module’s log-entry pattern.
- `parse_line($line)`: access-log line.
- `top_n($counts, $n)`: hash reference of counts and requested number of keys.
- Reads no command-line options, `@ARGV`, `%ENV`, files, or handles.

#### Output
- `parse_line` returns a hash reference with `host`, `time`, `method`, `path`, `status`, and `millis`; returns false for nonmatching lines.
- `add` returns `1` for a parsed entry or `0` for a nonmatching line.
- `summarize` returns a hash reference with UTC `generated` timestamp (`%Y-%m-%dT%H:%M:%SZ`), `total`, unique-host count (`hosts`), maximum per-host count (`busiest`), slow-request count (`slow`), and copied `statuses` counts.
- `top_n` returns keys ordered by descending count, then ascending lexical order.
- Prints and writes no output.

#### Core logic
- `new` validates the requested logging level and initializes host/status counts, slow count, and line count.
- `add` increments the line count before parsing; rejects input beyond `MAX_LINES`.
- For valid entries, `parse_line` extracts the host, timestamp, method, path, status, and milliseconds; normalizes the host to lowercase and removes one trailing dot.
- `add` increments host and status counts and counts requests whose duration exceeds `SLOW_REQUEST_MS`.
- `summarize` computes total requests from host counts, unique hosts, busiest-host count, and status totals; adds the current UTC timestamp.
- `top_n` sorts count keys by descending values, breaking ties lexically.

#### Subs
- `new($class, %args) -> object` — validate level and initialize counters.
- `parse_line($line) -> hashref or false` — extract and normalize log entry.
- `add($self, $line) -> 1 or 0` — count a valid entry.
- `summarize($self) -> hashref` — build aggregate statistics.
- `top_n($counts, $n) -> list` — return highest-count keys.
- `_normalize_host($host) -> string` — lowercase and trim trailing dot.

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
- `parent`: loads `Exporter`.
- Pragmas: `strict`, `warnings`.

#### Exceptions / Errors
- `new` croaks with `unknown level '$level'` for levels absent from `%LEVEL_RANK`.
- `add` croaks with `more than 50000 lines` when the incremented line count exceeds `MAX_LINES`.
- `parse_line` returns false for lines that fail its pattern match.
