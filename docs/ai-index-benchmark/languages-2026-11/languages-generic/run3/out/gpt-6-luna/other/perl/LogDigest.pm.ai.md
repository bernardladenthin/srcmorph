### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:38:24Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web-server access logs into per-host request counts, per-status totals, and slow-request metrics.

#### Purpose
- Parse access-log lines and accumulate host, HTTP status, and slow-request counts.
- Produce a timestamped aggregate summary and rank hosts by count.

#### Type
- Perl package `LogDigest`; no declared superclass or interfaces.

#### Input
- `new(%args)`: optional `level` setting.
- `parse_line($line)` and `add($line)`: access-log line with host, timestamp, method, path, three-digit status, and elapsed milliseconds.
- `top_n($counts, $n)`: count hash reference and requested result count.

#### Output
- `parse_line` returns a hash reference with `host`, `time`, `method`, `path`, `status`, and `millis`, or an undefined value for a nonmatching line.
- `add` returns `1` for a parsed line or `0` for an unparsed line; updates object counts.
- `summarize` returns a hash reference with `generated`, `total`, `hosts`, `busiest`, `slow`, and copied `statuses` counts.
- `top_n` returns host keys sorted by descending count, then ascending name.
- `new` returns a blessed digest object.

#### Core logic
- `new` validates `level` against the level-rank map and initializes empty host/status counts, slow count, and line count.
- `parse_line` matches the access-log format; lowercases the host and removes one trailing dot.
- `add` increments the line counter before parsing; rejects input beyond the line limit, ignores malformed lines, and counts parsed hosts and statuses.
- `add` increments `slow` when elapsed time is greater than the slow-request threshold.
- `summarize` computes total requests from host counts, distinct host count, busiest-host count, UTC generation time, slow count, and status counts.
- `top_n` orders count keys by descending values with lexical tie-breaking.

#### Public API
- `new($class, %args) -> LogDigest` — validate level and initialize counters.
- `parse_line($line) -> hashref|undef` — parse one access-log line.
- `add($self, $line) -> integer` — count one valid log entry.
- `summarize($self) -> hashref` — build aggregate metrics.
- `top_n($counts, $n) -> list` — rank count keys.
- Internal: `_normalize_host($host)`.

#### Constants and configuration
- `MAX_LINES = 50_000` — maximum lines accepted by an object.
- `DEFAULT_LEVEL = 'WARN'` — default constructor level.
- `SLOW_REQUEST_MS = 1500` — threshold; only greater durations count as slow.
- `%LEVEL_RANK`: `DEBUG => 0`, `INFO => 1`, `WARN => 2`, `ERROR => 3`.
- `$VERSION = '1.4'`.
- `@EXPORT_OK = qw(parse_line top_n)` — optional exports; no `Exporter` setup is present.
- `level` — constructor argument, defaults to `DEFAULT_LEVEL`; stored on the object but not otherwise used.

#### Dependencies
- `POSIX::strftime`, `POSIX::gmtime`.
- `List::Util::max`, `List::Util::sum`.
- `Carp::croak`.

#### Exceptions / Errors
- `new` croaks with `unknown level '$level'` for an unrecognized level.
- `add` croaks with `more than 50000 lines` when the line limit is exceeded.
- `parse_line` returns undef for lines that do not match its pattern.
