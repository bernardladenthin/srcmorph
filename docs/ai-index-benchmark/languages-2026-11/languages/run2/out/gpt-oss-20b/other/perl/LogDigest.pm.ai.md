### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:34:19Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web‑server access logs into per‑host and per‑status counts.

#### Purpose
- Provides a class to parse log lines and aggregate statistics.
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
- `add` returns `1` on successful parse, `0` if line ignored, dies on exceeding `MAX_LINES`.
- `summarize` returns a hashref:
  - `generated` – UTC timestamp.
  - `total` – total parsed lines.
  - `hosts` – number of distinct hosts.
  - `busiest` – max requests per host.
  - `slow` – count of requests > `SLOW_REQUEST_MS`.
  - `statuses` – hashref of status code counts.
- `top_n` returns an array of keys sorted by count descending, ties broken alphabetically.

#### Core logic
- `new`: validate level, initialize counters.
- `parse_line`: regex extracts host, time, method, path, status, millis; normalizes host.
- `add`: enforce `MAX_LINES`; parse line; increment host/status counters; increment slow counter if millis > `SLOW_REQUEST_MS`.
- `summarize`: compute totals, busiest host, generate UTC timestamp.
- `top_n`: sort counts by value descending, key ascending; slice top `n`.

#### Subs
- `new(%args) -> object` – create digest with level.
- `parse_line($line) -> hashref` – parse log line.
- `add($line) -> 1|0` – add line to digest.
- `summarize() -> hashref` – aggregate statistics.
- `top_n($counts, $n) -> array` – top keys by count.
- `_normalize_host($host) -> host` – lowercase, strip trailing dot.

#### Constants and configuration
- `MAX_LINES => 50000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`

#### Dependencies
- `POSIX qw(strftime)`
- `List::Util qw(max sum)`
- `Carp qw(croak)`

#### Exceptions / Errors
- `new`: `croak "unknown level '$level'"` if level invalid.
- `add`: `croak 'more than ' . MAX_LINES . ' lines'` when limit exceeded.
