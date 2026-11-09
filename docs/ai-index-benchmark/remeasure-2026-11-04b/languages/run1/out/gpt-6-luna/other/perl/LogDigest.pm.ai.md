### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:12:32Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Aggregates web-server access-log entries into per-host, per-status, busiest-host, and slow-request statistics.

#### Purpose
- Parses access-log lines and accumulates digest statistics.
- Provides host normalization and count ranking.

#### Type
- Module; package `LogDigest`.
- Exportable names: `@EXPORT_OK` contains `parse_line`, `top_n`.
- Procedural functions and blessed-object methods; constructor `new`.

#### Input
- `new` accepts a `level` named argument.
- `add` accepts an access-log line; `parse_line` accepts one line; `top_n` accepts a count hash reference and rank limit.
- No command-line options, environment variables, or file reads.

#### Output
- `parse_line` returns a hash reference with `host`, `time`, `method`, `path`, `status`, and `millis`, or returns false for an unmatched line.
- `add` returns `1` for a parsed line and `0` for an unmatched line.
- `summarize` returns a hash reference containing UTC `generated` timestamp, `total`, distinct `hosts`, maximum per-host count as `busiest`, `slow` count, and a copied `statuses` hash.
- `top_n` returns host/count keys sorted by descending count, then ascending key.
- No printed output or files written.

#### Core logic
- `new` validates the requested log level and initializes host/status counts, slow-request count, and processed-line count.
- `add` increments the line counter, rejects input beyond `MAX_LINES`, parses the line, then increments normalized-host and status counts.
- A parsed request increments `slow` when `millis` exceeds `SLOW_REQUEST_MS`.
- `parse_line` matches a log-line pattern, extracts request fields, and lowercases the host while removing one trailing dot.
- `summarize` derives total requests and busiest-host count, counts distinct hosts, timestamps the result, and copies status counts.
- `top_n` sorts keys by count descending, with lexical tie-breaking, and returns up to the requested number.

#### Subs
- `new($class, %args) -> blessed object` — validate level and initialize counters.
- `parse_line($line) -> hash reference or false` — extract fields from a log line.
- `add($self, $line) -> 1 or 0` — parse and accumulate one entry.
- `summarize($self) -> hash reference` — build aggregate statistics.
- `top_n($counts, $n) -> list` — rank count keys.
- `_normalize_host($host) -> normalized host` — lowercase and trim trailing dot.

#### Constants and configuration
- `MAX_LINES => 50_000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`
- `$VERSION = '1.4'`
- `@EXPORT_OK = qw(parse_line top_n)`
- `%LEVEL_RANK = (DEBUG => 0, INFO => 1, WARN => 2, ERROR => 3)`

#### Dependencies
- `strict`, `warnings` — enable compile-time and runtime checks.
- `POSIX` — imports `strftime`.
- `List::Util` — imports `max`, `sum`.
- `Carp` — imports `croak`.
- `parent` — loads `Exporter` as parent.

#### Exceptions / Errors
- `new` croaks with `unknown level '<level>'` when `level` is not in `%LEVEL_RANK`.
- `add` croaks with `more than 50000 lines` when the line limit is exceeded.
