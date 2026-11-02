### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:23:29Z
- G: 
- A: 0.0.0
- X: file
- P: B953BC65
---
> Aggregates web-server access-log entries into per-host and per-status counts, with slow-request totals and busiest-host summaries.

#### Purpose
- Parses access-log lines and accumulates traffic statistics.
- Produces aggregate summaries and ranks count keys.

#### Type
- Module; package `LogDigest`.
- `@EXPORT_OK`: `parse_line`, `top_n`; no default exports.
- Procedural parsing/ranking functions and object-oriented aggregation via `new`.

#### Input
- `new(level => ...)`: optional `level` argument; defaults to `DEFAULT_LEVEL`.
- `add($line)`: access-log line matching the module’s expected format.
- `parse_line($line)`: access-log line.
- `top_n($counts, $n)`: count hash reference and requested count.
- No command-line options, `@ARGV`, environment variables, or file reads.

#### Output
- `parse_line`: hash reference with normalized `host`, `time`, `method`, `path`, `status`, and `millis`; returns false for nonmatching lines.
- `add`: `1` for a parsed entry, `0` for a nonmatching line.
- `summarize`: hash reference with UTC `generated` timestamp, total requests, distinct host count, busiest-host count, slow-request count, and copied status counts.
- `top_n`: list of keys sorted by descending count, then ascending key.
- No printed output or files written.

#### Core logic
- `new` validates the requested log level and initializes host/status counters, slow-request count, and line count.
- `parse_line` extracts host, timestamp, method, path, three-digit status, and request duration from a matching access-log line.
- Hostnames are lowercased and have one trailing dot removed.
- `add` increments the line count, rejects input beyond `MAX_LINES`, ignores unparseable lines, and updates host, status, and slow-request counts.
- Requests exceeding `SLOW_REQUEST_MS` count as slow.
- `summarize` calculates total requests, distinct hosts, maximum per-host count, and returns the status-count copy with a UTC generation timestamp.
- `top_n` sorts keys by descending counts with lexical tie-breaking, then returns up to `n` keys.

#### Subs
- `new($class, %args) -> object` — validate level and initialize counters.
- `parse_line($line) -> hashref or false` — parse one access-log entry.
- `add($self, $line) -> 1 or 0` — count a valid entry.
- `summarize($self) -> hashref` — build aggregate statistics.
- `top_n($counts, $n) -> list` — rank count keys.
- `_normalize_host($host) -> string` — lowercase and trim trailing dot.

#### Constants and configuration
- `MAX_LINES => 50_000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`
- `$VERSION = '1.4'`
- `@EXPORT_OK = qw(parse_line top_n)`
- `%LEVEL_RANK`: `DEBUG => 0`, `INFO => 1`, `WARN => 2`, `ERROR => 3`

#### Dependencies
- `POSIX`: imports `strftime`.
- `List::Util`: imports `max`, `sum`.
- `Carp`: imports `croak`.
- `Exporter`: loaded through `parent`.
- Pragmas: `strict`, `warnings`.

#### Exceptions / Errors
- `new` croaks with `unknown level '$level'` when the level is not in `%LEVEL_RANK`.
- `add` croaks with `more than 50000 lines` when input exceeds `MAX_LINES`.
