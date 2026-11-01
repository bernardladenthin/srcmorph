### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:38:58Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web‑server access logs into per‑host and per‑status counts.

#### Purpose
- Provides a class to aggregate log entries.
- Offers helper `parse_line` and `top_n` for external use.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: `new`, `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – a single log line string.
- `parse_line($line)` – same format as `add`.
- No command‑line options, no environment variables.

#### Output
- `new` returns blessed object.
- `add` returns `1` on successful parse, `0` if line ignored, dies on exceeding `MAX_LINES`.
- `summarize` returns hashref:
  - `generated` – UTC timestamp.
  - `total` – total entries counted.
  - `hosts` – number of distinct hosts.
  - `busiest` – max count per host.
  - `slow` – number of requests > `SLOW_REQUEST_MS`.
  - `statuses` – hashref of status code counts.
- `top_n($counts, $n)` returns array of top `$n` keys sorted by count descending, then lexicographically.

#### Core logic
- `new`: validate level, initialize counters.
- `parse_line`: regex extracts host, time, method, path, status, millis; normalizes host.
- `add`: enforce `MAX_LINES`; parse line; increment host and status counters; increment `slow` if millis > `SLOW_REQUEST_MS`.
- `summarize`: compute totals, generate timestamp, return aggregated stats.
- `top_n`: sort hash by value descending, key ascending; slice top `$n`.

#### Subs
- `new(%args) -> object` – create digest with level.
- `parse_line($line) -> hashref` – parse log line.
- `add($line) -> 1/0` – add entry.
- `summarize() -> hashref` – aggregate stats.
- `top_n($counts, $n) -> list` – top N keys.
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
- `add`: `croak 'more than ' . MAX_LINES . ' lines'` if line count exceeds limit.
