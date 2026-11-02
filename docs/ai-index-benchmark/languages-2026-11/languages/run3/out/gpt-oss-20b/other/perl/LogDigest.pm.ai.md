### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:55:28Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web‑server access logs into per‑host and per‑status counts.

#### Purpose
- Provides a `LogDigest` class for summarizing access logs.
- Exposes `parse_line` and `top_n` for external use.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n` via `@EXPORT_OK`.
- Object‑oriented: constructor `new`, methods `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – a single log line string.
- `parse_line($line)` – parses a log line; returns `undef` if format mismatch.
- `top_n($counts, $n)` – takes a hashref of counts and integer `n`.

#### Output
- `add` returns `1` on successful parse, `0` if line ignored, dies on exceeding `MAX_LINES`.
- `summarize` returns hashref:
  - `generated` – UTC timestamp.
  - `total` – total entries counted.
  - `hosts` – number of distinct hosts.
  - `busiest` – max count per host.
  - `slow` – number of requests > `SLOW_REQUEST_MS`.
  - `statuses` – hashref of status counts.
- `top_n` returns list of top `n` keys sorted by count descending, then alphabetically.

#### Core logic
- `new`: validate level, initialize counters.
- `parse_line`: regex extracts host, time, method, path, status, millis; normalizes host.
- `add`: enforce `MAX_LINES`; parse line; increment host and status counters; increment `slow` if millis > `SLOW_REQUEST_MS`.
- `summarize`: compute totals, busiest host, generate timestamp, return aggregated data.
- `top_n`: sort hash keys by value descending, then key ascending; slice top `n`.
- `_normalize_host`: lowercase, strip trailing dot.

#### Subs
- `new(%args) -> LogDigest` – constructor, validates level.
- `parse_line($line) -> hashref` – parses log line.
- `add($line) -> 1|0` – processes a line, updates counters.
- `summarize() -> hashref` – aggregates statistics.
- `top_n($counts, $n) -> list` – returns top `n` keys.
- `_normalize_host($host) -> string` – lowercases, removes trailing dot.

#### Constants and configuration
- `MAX_LINES => 50000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`
- `%LEVEL_RANK = (DEBUG => 0, INFO => 1, WARN => 2, ERROR => 3)`

#### Dependencies
- `POSIX qw(strftime)`
- `List::Util qw(max sum)`
- `Carp qw(croak)`

#### Exceptions / Errors
- `new`: `croak "unknown level '$level'"` if level invalid.
- `add`: `croak 'more than ' . MAX_LINES . ' lines'` when line count exceeds limit.
