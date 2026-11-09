### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:19:27Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Condenses web-server access-log entries into normalized per-host traffic counts, HTTP status totals, and slow-request metrics.

#### Purpose
- Aggregates parsed access-log lines into a summary of hosts, statuses, busiest host, and slow requests.
- Provides line parsing and count-ranking helpers.

#### Type
- Module; package `LogDigest`.
- Exports `parse_line` and `top_n` via `@EXPORT_OK`.
- Object-oriented: `new` blesses an instance; `add` and `summarize` operate on it.

#### Output
- `parse_line` returns a hash reference with `host`, `time`, `method`, `path`, `status`, and `millis`, or an undefined value for malformed input.
- `add` returns `1` for a parsed line and `0` for a malformed line.
- `summarize` returns a hash reference containing UTC `generated` timestamp, `total`, distinct `hosts`, `busiest` host count, `slow` request count, and a copied `statuses` hash.
- `top_n` returns host/count keys sorted by descending count, then ascending key.
- No printed output or files written.

#### Core logic
- `new` validates the requested log level and initializes host/status counts, slow-request count, and line count.
- `parse_line` extracts host, timestamp, method, path, three-digit status, and duration from a combined-log-style line; lowercases the host and removes one trailing dot.
- `add` increments the line limit counter, ignores unparsable lines, then updates per-host and per-status counts; durations above `SLOW_REQUEST_MS` increment the slow count.
- `summarize` calculates total requests and busiest-host count, counts distinct hosts, and adds a UTC generation timestamp.
- `top_n` sorts keys by count descending with lexical tie-breaking, then returns up to `n` keys.

#### Subs
- `new($class, %args) -> blessed object` — validate level and initialize counters.
- `parse_line($line) -> hashref or undef` — parse and normalize a log entry.
- `add($self, $line) -> 1 or 0` — count a valid entry or skip malformed input.
- `summarize($self) -> hashref` — build aggregate metrics and timestamp.
- `top_n($counts, $n) -> list` — rank keys by count and name.
- `_normalize_host($host) -> string` — lowercase host and strip trailing dot.

#### Constants and configuration
- `MAX_LINES => 50_000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`
- `$VERSION = '1.4'`
- `@EXPORT_OK = qw(parse_line top_n)`
- `%LEVEL_RANK`: `DEBUG => 0`, `INFO => 1`, `WARN => 2`, `ERROR => 3`

#### Dependencies
- `strict`, `warnings` — enable compile-time checks and warnings.
- `POSIX`: `strftime`
- `List::Util`: `max`, `sum`
- `Carp`: `croak`
- `parent`: `Exporter`

#### Exceptions / Errors
- `new` croaks with `unknown level '$level'` when `level` is not in `%LEVEL_RANK`.
- `add` croaks with `more than 50000 lines` when the line count exceeds `MAX_LINES`.
