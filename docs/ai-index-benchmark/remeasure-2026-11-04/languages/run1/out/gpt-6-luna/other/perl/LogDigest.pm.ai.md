### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:57:30Z
- G: 
- A: 0.0.0
- X: file
- P: 4F9ECAEF
---
> Summarizes access-log requests into per-host and per-status counts, including slow-request totals and busiest-host statistics.

#### Purpose
- Parses web-server access-log lines and accumulates request statistics.
- Provides host normalization and count-ranking helpers.

#### Type
- Module; package `LogDigest`.
- Exports `parse_line` and `top_n` via `@EXPORT_OK`.
- Object-oriented: `new` blesses a statistics object; `add` and `summarize` operate on it.

#### Input
- `new` accepts a `level` key in `%args`; defaults to `DEFAULT_LEVEL` (`WARN`).
- `add` accepts one access-log line.
- `parse_line` accepts one access-log line.
- `top_n` accepts a count hash reference and a requested count.
- No command-line options, `@ARGV` usage, environment reads, or file reads.

#### Output
- `parse_line` returns a hash reference with `host`, `time`, `method`, `path`, `status`, and `millis`, or an undefined value for a nonmatching line.
- `add` returns `1` for a parsed line, `0` for an unparsed line.
- `summarize` returns a hash reference with UTC `generated` timestamp (`YYYY-MM-DDTHH:MM:SSZ`), request `total`, distinct-host count `hosts`, maximum per-host count `busiest`, `slow` request count, and a copied `statuses` count hash.
- `top_n` returns host/count keys ordered by descending count, then ascending key.
- No printed output or files written.

#### Core logic
- `new` validates the requested log level and initializes host/status counts, slow-request count, and line count.
- `parse_line` matches a combined-log-style line with a three-digit status and integer duration; returns normalized host and parsed fields.
- `add` increments the line limit counter, ignores malformed lines, then updates host and status counts and increments `slow` when duration exceeds `SLOW_REQUEST_MS`.
- `summarize` computes total requests from host counts, distinct hosts, busiest-host count, current UTC timestamp, and status counts.
- `top_n` sorts count keys by descending value with lexical tie-breaking, then returns up to `n` keys.
- `_normalize_host` lowercases the host and removes one trailing dot.

#### Subs
- `new(%args) -> object` — validate level and initialize counters.
- `parse_line($line) -> hashref|undef` — parse and normalize one log line.
- `add($line) -> 1|0` — count a valid request.
- `summarize() -> hashref` — assemble aggregate statistics.
- `top_n($counts, $n) -> list` — rank count keys and return top entries.
- `_normalize_host($host) -> host` — lowercase and trim trailing dot.

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
- `parent`: `Exporter`.
- Pragmas: `strict`, `warnings`.

#### Exceptions / Errors
- `new` croaks with `unknown level '$level'` for levels absent from `%LEVEL_RANK`.
- `add` croaks after the line count exceeds `MAX_LINES`, with `more than 50000 lines`.
