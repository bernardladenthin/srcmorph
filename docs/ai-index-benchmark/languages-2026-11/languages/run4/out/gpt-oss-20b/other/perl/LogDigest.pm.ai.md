### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:28:44Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web‑server access logs into per‑host and per‑status counts.

#### Purpose
- Create a digest of web‑server logs.
- Count requests per host and status.
- Track slow requests.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: constructor `new`, methods `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – a single log line string.
- `parse_line($line)` – parses a log line.
- `top_n($counts, $n)` – helper for ranking.

#### Output
- `add` returns `1` on successful parse, `0` if line ignored.
- `summarize` returns hashref:
  - `generated` – UTC timestamp.
  - `total` – total requests.
  - `hosts` – number of distinct hosts.
  - `busiest` – max requests from a single host.
  - `slow` – count of requests > 1500 ms.
  - `statuses` – hashref of status counts.
- `top_n` returns list of top `$n` keys.

#### Core logic
- `new` validates log level, initializes counters.
- `parse_line` uses regex to extract host, time, method, path, status, millis; normalizes host.
- `add` limits to `MAX_LINES` (50 000); updates host, status, slow counters.
- `summarize` aggregates totals, computes busiest host, formats timestamp.
- `top_n` sorts counts descending, ties by key lexicographically.

#### Subs
- `new(%args) -> LogDigest` – create instance.
- `parse_line($line) -> hashref` – parse log entry.
- `add($line) -> 1|0` – add entry to digest.
- `summarize() -> hashref` – produce summary.
- `top_n($counts, $n) -> list` – return top N keys.
- `_normalize_host($host) -> string` – lowercase, strip trailing dot.

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
- `new` dies with `croak` if unknown level.
- `add` dies with `croak` if line count exceeds `MAX_LINES`.
