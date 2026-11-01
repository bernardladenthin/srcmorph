### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:55:19Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web-server access logs into per-host, per-status, and slow-request counts.

#### Purpose
- Module for parsing access-log lines and accumulating a digest.
- Produces a timestamped summary with host and status statistics.

#### Type
- Module; package `LogDigest`; procedural/object-oriented via `new` and `bless`.
- `@EXPORT_OK`: `parse_line`, `top_n`.

#### Output
- `parse_line` returns a hashref of parsed fields or `undef` for a non-matching line.
- `add` returns `1` for a parsed line, `0` for an invalid line.
- `summarize` returns a hashref containing `generated`, `total`, `hosts`, `busiest`, `slow`, and copied `statuses`.
- `top_n` returns sorted keys as a list.

#### Core logic
- `new` validates the requested logging level and initializes host/status counts, slow-request count, and line count.
- `parse_line` matches a log line’s host, timestamp, method, path, three-digit status, and duration; normalizes the host.
- `add` enforces the line limit, ignores unparsable lines, increments host and status counts, and counts requests over `SLOW_REQUEST_MS`.
- `summarize` computes total requests, distinct hosts, busiest-host count, slow requests, status counts, and a UTC generation timestamp.
- `top_n` sorts keys by descending count, then lexicographically ascending.
- `_normalize_host` lowercases the host and removes one trailing dot.

#### Subs
- `new(%args) -> object` — validate level; initialize digest.
- `parse_line($line) -> hashref|undef` — parse and normalize log entry.
- `add($line) -> 1|0` — count valid entry or reject invalid line.
- `summarize() -> hashref` — produce aggregate statistics.
- `top_n($counts, $n) -> list` — return highest-ranked keys.
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
- `parent`: `Exporter`.
- `strict`, `warnings`: enable strictness and warnings.

#### Exceptions / Errors
- `new` croaks with `unknown level '$level'` when the level is not in `%LEVEL_RANK`.
- `add` croaks with `more than 50000 lines` when the line limit is exceeded.
