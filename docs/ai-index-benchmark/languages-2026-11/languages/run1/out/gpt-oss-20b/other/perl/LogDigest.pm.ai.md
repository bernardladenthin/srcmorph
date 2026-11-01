### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:12:08Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web‑server access logs into per‑host and per‑status counts.

#### Purpose
- Provides a class to aggregate log entries.
- Offers helper functions for parsing and ranking counts.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: constructor `new`, instance methods `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – a single log line string.
- `parse_line($line)` – parses a log line.
- `top_n($counts, $n)` – ranks a hash of counts.

#### Output
- `add` returns `1` on successful parse, `0` if line ignored, dies on too many lines.
- `summarize` returns hashref:
  - `generated` – UTC timestamp.
  - `total` – total entries counted.
  - `hosts` – number of distinct hosts.
  - `busiest` – max count per host.
  - `slow` – number of requests > 1500 ms.
  - `statuses` – hashref of status counts.
- `top_n` returns list of keys sorted by count descending, then alphabetically.

#### Core logic
- `new` validates log level, initializes counters.
- `parse_line` uses regex to extract host, time, method, path, status, millis; normalizes host.
- `add` increments line counter, updates host/status counts, tracks slow requests.
- `summarize` aggregates totals, computes busiest host, formats timestamp.
- `top_n` sorts counts and slices top `n`.

#### Subs
- `new(%args) -> object` – create digest with level.
- `parse_line($line) -> hashref` – parse log line.
- `add($line) -> 1/0` – add entry.
- `summarize() -> hashref` – aggregate stats.
- `top_n($counts, $n) -> list` – top N keys.
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
- `add` dies if line count exceeds `MAX_LINES`.
