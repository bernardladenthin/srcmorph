### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:32:29Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web-server access logs into per-host and per-status counts, including slow-request totals.

#### Purpose
- Module for parsing access-log lines and accumulating host, status, and slow-request statistics.
- Produces a summary hash and can rank count keys.

#### Type
- Module; package `LogDigest`.
- Procedural, with `new` constructing a blessed object.
- Declares `@EXPORT_OK`: `parse_line`, `top_n` (no exporter module is loaded).

#### Input
- `new` accepts `level` in its argument hash; defaults to `DEFAULT_LEVEL`.
- `parse_line` and `add` accept an access-log line matching the module’s regular expression.
- `top_n` accepts a count hash reference and a limit.
- No command-line options, `@ARGV`, `%ENV`, or file reads.

#### Output
- `parse_line` returns a hash reference with `host`, `time`, `method`, `path`, `status`, and `millis`, or undef for a nonmatching line.
- `add` returns `1` for a parsed line and `0` for an unmatched line.
- `summarize` returns a hash reference with `generated`, `total`, `hosts`, `busiest`, `slow`, and a copied `statuses` hash.
- `top_n` returns keys sorted by descending count, then ascending key.
- No printed output or files written.

#### Core logic
- `new` validates the configured log level and initializes host/status counters, slow-request count, and line count.
- `parse_line` extracts access-log fields; normalizes the host to lowercase and removes one trailing dot.
- `add` increments the line count, rejects input beyond `MAX_LINES`, ignores unmatched lines, and updates host/status counts and the slow count when `millis` exceeds `SLOW_REQUEST_MS`.
- `summarize` computes total requests, distinct hosts, busiest-host count, slow requests, UTC generation time, and status counts.
- `top_n` ranks count keys, breaking ties lexically.

#### Subs
- `new(class, args) -> object` — validate level and initialize counters.
- `parse_line(line) -> hashref or undef` — parse and normalize a log entry.
- `add(self, line) -> 1 or 0` — count a valid log entry.
- `summarize(self) -> hashref` — build aggregate statistics.
- `top_n(counts, n) -> list` — rank keys by count.
- `_normalize_host(host) -> host` — lowercase and trim trailing dot.

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
- Pragmas: `strict`, `warnings`.

#### Exceptions / Errors
- `new` calls `croak` for a level absent from `%LEVEL_RANK`.
- `add` calls `croak` when the line count exceeds `MAX_LINES`.
