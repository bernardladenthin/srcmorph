### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:10:30Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web-server access-log entries into per-host and per-status counts, with slow-request tracking.

#### Purpose
- Module for parsing access-log lines and summarizing accepted requests.
- Tracks host totals, status totals, busiest host, and requests slower than a threshold.

#### Type
- Module; package `LogDigest`.
- `@EXPORT_OK`: `parse_line`, `top_n` (no `Exporter` use shown).
- Object-oriented via `new` and `bless`; also provides procedural functions.

#### Input
- `new` accepts a named `level` argument; defaults to `DEFAULT_LEVEL`.
- `add` accepts a log line as its argument; no command-line options, `@ARGV`, environment variables, or files are read.

#### Output
- `parse_line` returns an entry hash reference on a matching line, otherwise an undefined value.
- `add` returns `1` for a parsed line or `0` for an unparseable line.
- `summarize` returns a hash reference containing `generated`, `total`, `hosts`, `busiest`, `slow`, and a copy of `statuses`.
- `top_n` returns sorted keys as a list.
- No printed output or files written.

#### Core logic
- `new` validates the requested level and initializes host counts, status counts, slow-request count, and line count.
- `parse_line` extracts host, timestamp, method, path, status, and elapsed milliseconds from the expected access-log format; normalizes the host.
- `add` increments the line count, rejects counts beyond `MAX_LINES`, ignores malformed lines, and updates host/status counts and slow-request count.
- `summarize` computes total requests, distinct hosts, maximum per-host count, slow-request count, status totals, and a UTC generation timestamp.
- `top_n` sorts keys by descending count, then alphabetically, and returns up to `n` keys.
- `_normalize_host` lowercases the host and removes one trailing dot.

#### Subs
- `new(class, args) -> object` — validate level and initialize counters.
- `parse_line(line) -> hashref or undef` — parse one access-log line.
- `add(self, line) -> 0 or 1` — count a valid log entry.
- `summarize(self) -> hashref` — build aggregate summary.
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
- `POSIX`: `strftime`
- `List::Util`: `max`, `sum`
- `Carp`: `croak`
- Pragmas: `strict`, `warnings`

#### Exceptions / Errors
- `new` croaks with `unknown level '<level>'` when the level is not in `%LEVEL_RANK`.
- `add` croaks with `more than 50000 lines` when the line limit is exceeded.
- `parse_line` returns undef for lines that do not match its pattern.
