### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:54:26Z
- G: 
- A: 0.0.0
- X: file
- P: B953BC65
---
> Condenses access-log records into per-host and per-status request counts, with slow-request and busiest-host metrics.

#### Purpose
- Parses web-server access-log lines and accumulates request statistics.
- Produces a summary hash or ranks count keys.

#### Type
- Module; package `LogDigest`.
- Exports `parse_line` and `top_n` via `@EXPORT_OK`.
- Object-oriented: `new` blesses a digest object; `add` and `summarize` operate on it.

#### Input
- `new(level => ...)`: optional `level` argument; defaults to `DEFAULT_LEVEL` (`WARN`).
- `add($line)`: receives a log line as an argument; counts every call toward the line limit, including malformed lines.
- `parse_line($line)`: expects a line matching the module’s access-log pattern.
- `top_n($counts, $n)`: receives a hash reference of counts and a requested number of keys.
- No command-line options, `@ARGV` processing, environment-variable reads, or file/handle reads.

#### Output
- `parse_line` returns a hash reference with `host`, `time`, `method`, `path`, `status`, and `millis`; returns false for lines that do not match.
- `add` returns `1` for a parsed line and `0` for a malformed line.
- `summarize` returns a hash reference containing UTC `generated` timestamp (`%Y-%m-%dT%H:%M:%SZ`), `total`, distinct-host count (`hosts`), maximum per-host count (`busiest`), slow-request count (`slow`), and copied status counts (`statuses`).
- `top_n` returns count keys sorted descending by count, then ascending lexically for ties.
- No direct printing or file writes.

#### Core logic
- `new` selects a logging level, validates it against the configured level ranks, and initializes host/status counts, slow count, and line count.
- `parse_line` extracts host, timestamp, method, path, three-digit status, and numeric duration; lowercases the host and removes one trailing dot.
- `add` increments the line count and rejects calls beyond `MAX_LINES`; for valid lines, increments host and status counts and counts durations greater than `SLOW_REQUEST_MS`.
- `summarize` computes total requests and busiest-host count, records the number of distinct hosts and slow requests, copies status counts, and adds the current UTC timestamp.
- `top_n` sorts keys by descending count and lexical tie-break.

#### Subs
- `new($class, %args) -> object` — validate level and initialize counters.
- `parse_line($line) -> hashref or false` — parse and normalize one log record.
- `add($self, $line) -> 1 or 0` — count a valid record within the line limit.
- `summarize($self) -> hashref` — assemble aggregate statistics.
- `top_n($counts, $n) -> list` — rank count keys and return the requested prefix.
- `_normalize_host($host) -> string` — lowercase host and strip trailing dot.

#### Constants and configuration
- `MAX_LINES => 50_000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`
- `$VERSION = '1.4'`
- `@EXPORT_OK = qw(parse_line top_n)`
- `%LEVEL_RANK = (DEBUG => 0, INFO => 1, WARN => 2, ERROR => 3)`

#### Dependencies
- `POSIX`: imports `strftime`.
- `List::Util`: imports `max`, `sum`.
- `Carp`: imports `croak`.
- `Exporter`: inherited via `parent`.
- `strict`, `warnings`: enable Perl strictness and warnings.

#### Exceptions / Errors
- `new` croaks with `unknown level '<level>'` if the requested level is not in `%LEVEL_RANK`.
- `add` croaks with `more than 50000 lines` after the line count exceeds `MAX_LINES`.
- `parse_line` returns false for a non-matching line; no exception is raised for malformed input.
