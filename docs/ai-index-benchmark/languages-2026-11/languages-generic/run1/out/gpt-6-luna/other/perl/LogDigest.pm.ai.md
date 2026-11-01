### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:27:42Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web-server access logs into request totals by host and HTTP status, with slow-request counts and busiest-host summaries.

#### Purpose
- Parse access-log lines and accumulate per-host, per-status, and slow-request statistics.
- Produce a timestamped summary and rank hosts by request count.

#### Type
- Perl package `LogDigest`; exports are listed in `@EXPORT_OK`.
- No declared parent class or implemented interface.

#### Input
- `new(%args)`: optional `level` argument.
- `parse_line($line)` and `add($line)`: access-log lines matching the package’s regular expression.
- `top_n($counts, $n)`: hash reference of counts and requested result count.
- `add` consumes parsed host, status, and request duration; `summarize` reads accumulated object state.

#### Output
- `parse_line` returns a hash reference with `host`, `time`, `method`, `path`, `status`, and `millis`, or returns false for a nonmatching line.
- `add` returns `1` for a parsed line and `0` for a nonmatching line; updates host/status counts and slow-request count.
- `summarize` returns a hash reference with `generated`, `total`, `hosts`, `busiest`, `slow`, and copied `statuses`.
- `top_n` returns host keys sorted by descending count, then lexically ascending on ties.

#### Core logic
- `new` validates the configured log level and initializes empty host/status counts, slow count, and line count.
- `parse_line` extracts host, timestamp, method, path, three-digit status, and numeric duration; normalizes the host to lowercase and removes one trailing dot.
- `add` increments the line count before parsing, rejects counts above `MAX_LINES`, then updates host and status counts and counts durations greater than `SLOW_REQUEST_MS`.
- `summarize` calculates total requests from host counts, distinct host count, busiest-host count, slow requests, and a UTC generation timestamp.
- `top_n` orders count keys by descending value with lexical tie-breaking.

#### Public API
- `new(%args) -> LogDigest` — initialize digest with validated level.
- `parse_line($line) -> HASHREF|undef` — parse one access-log line.
- `add($line) -> 1|0` — add a parsed request to counts.
- `summarize() -> HASHREF` — return aggregate request statistics.
- `top_n($counts, $n) -> LIST` — rank count keys.
- Internal: `_normalize_host($host)`.

#### Constants and configuration
- `MAX_LINES = 50_000` — maximum lines accepted by `add`.
- `DEFAULT_LEVEL = 'WARN'` — default `level` for `new`.
- `SLOW_REQUEST_MS = 1500` — slow requests require duration strictly greater than this value.
- `%LEVEL_RANK`: `DEBUG => 0`, `INFO => 1`, `WARN => 2`, `ERROR => 3`.
- `$VERSION = '1.4'`.
- `@EXPORT_OK = qw(parse_line top_n)`.
- `level` is accepted and validated by `new`, then stored; no further use appears in this file.

#### Dependencies
- `POSIX::strftime`, `POSIX::gmtime`.
- `List::Util::max`, `List::Util::sum`.
- `Carp::croak`.

#### Exceptions / Errors
- `new` calls `croak` with `unknown level '$level'` for levels absent from `%LEVEL_RANK`.
- `add` calls `croak` with `more than 50000 lines` when the incremented line count exceeds `MAX_LINES`.
- `parse_line` returns without a parsed entry when the line does not match the access-log pattern.
