### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:32:23Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web-server access logs into per-host traffic counts, per-status counts, and slow-request totals.

#### Purpose
- Parse access-log lines and aggregate valid requests by normalized host and HTTP status.
- Produce a timestamped summary and rank count keys.

#### Type
- Perl package `LogDigest`; no declared inheritance or interface implementation.

#### Input
- `new(%args)`: optional `level`.
- `parse_line($line)` and `add($line)`: access-log line with host, timestamp, method, path, three-digit status, and elapsed milliseconds.
- `top_n($counts, $n)`: hash reference of counts and requested result count.

#### Output
- `parse_line` returns a hash reference with `host`, `time`, `method`, `path`, `status`, and `millis`, or returns nothing for a non-matching line.
- `add` returns `1` for a parsed line, `0` for an invalid line; updates host, status, slow-request, and line-count state.
- `summarize` returns a hash reference with `generated`, `total`, `hosts`, `busiest`, `slow`, and copied `statuses`.
- `top_n` returns keys ordered by descending count, then ascending key.

#### Core logic
- `new` validates the configured level and initializes empty host/status counts and zeroed counters.
- `parse_line` matches the expected access-log format; lowercases the host and removes one trailing dot.
- `add` increments the line counter, rejects input beyond `MAX_LINES`, ignores malformed lines, and counts valid requests by host and status.
- A request is slow when its `millis` value exceeds `SLOW_REQUEST_MS`.
- `summarize` totals requests from host counts, counts distinct hosts, finds the largest per-host count, and adds a UTC generation timestamp.
- `top_n` sorts keys by count descending, using lexical order to break ties.

#### Public API
- `new($class, %args) -> LogDigest` — initialize digest state.
- `parse_line($line) -> hashref or undef` — parse an access-log line; listed in `@EXPORT_OK`.
- `add($self, $line) -> 1 or 0` — accumulate one valid request.
- `summarize($self) -> hashref` — return aggregate counts.
- `top_n($counts, $n) -> list` — rank count keys; listed in `@EXPORT_OK`.
- Internal helpers: `_normalize_host($host)`.

#### Constants and configuration
- `MAX_LINES` = `50_000`.
- `DEFAULT_LEVEL` = `'WARN'`.
- `SLOW_REQUEST_MS` = `1500`.
- `%LEVEL_RANK`: `DEBUG` = 0, `INFO` = 1, `WARN` = 2, `ERROR` = 3.
- `level`: optional constructor argument; defaults to `DEFAULT_LEVEL`, must be a key in `%LEVEL_RANK`.
- `$VERSION` = `'1.4'`.
- `@EXPORT_OK` = `parse_line`, `top_n`.

#### Dependencies
- `POSIX::strftime`, `POSIX::gmtime`.
- `List::Util::max`, `List::Util::sum`.
- `Carp::croak`.

#### Exceptions / Errors
- `new` calls `croak` for an unknown `level`.
- `add` calls `croak` when the line count exceeds `MAX_LINES`.
- `parse_line` silently returns nothing for lines that do not match the expected format.
