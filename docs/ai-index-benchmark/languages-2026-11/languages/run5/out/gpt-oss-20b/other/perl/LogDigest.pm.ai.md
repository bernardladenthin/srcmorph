### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:35:32Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web‑server access logs into per‑host and per‑status counts.

#### Purpose
- Provides a class to parse and aggregate HTTP access log lines.
- Exposes `parse_line` and `top_n` for external use.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n` via `@EXPORT_OK`.
- Object‑oriented: constructor `new`, instance methods `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – a single log line string.
- `parse_line($line)` – static parsing of a log line.
- `top_n($counts, $n)` – static helper for ranking counts.

#### Output
- `add` returns `1` on successful parse, `0` if line ignored, dies if line count exceeds `MAX_LINES`.
- `summarize` returns hashref:
  - `generated` – UTC timestamp.
  - `total` – total parsed lines.
  - `hosts` – number of distinct hosts.
  - `busiest` – max requests per host.
  - `slow` – count of requests > `SLOW_REQUEST_MS`.
  - `statuses` – hashref of status code counts.
- `top_n` returns array of keys sorted by count descending, then lexicographically.

#### Core logic
- `new`: validate level, initialize counters.
- `parse_line`: regex extracts host, time, method, path, status, millis; normalizes host.
- `add`: enforce `MAX_LINES`; parse line; increment host/status counters; count slow requests.
- `summarize`: compute totals, busiest host, format timestamp.
- `top_n`: sort counts descending, return top `n`.

#### Subs
- `new(%args) -> object` – create digest with level.
- `parse_line($line) -> hashref` – parse log line.
- `add($line) -> 1/0` – add entry, update stats.
- `summarize() -> hashref` – aggregate summary.
- `top_n($counts, $n) -> array` – top keys by count.
- `_normalize_host($host) -> host` – lowercase, strip trailing dot.

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
- `new`: `croak "unknown level '$level'"` if invalid level.
- `add`: `croak 'more than ' . MAX_LINES . ' lines'` when limit exceeded.
