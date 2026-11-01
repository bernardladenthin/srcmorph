### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:17:10Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web‑server access logs into per‑host and per‑status counts.

#### Purpose
- Provides a class to aggregate log statistics.
- Exports `parse_line` and `top_n` for external use.

#### Type
- Module `LogDigest`.
- Exports: `parse_line`, `top_n`.
- Object‑oriented: `new`, `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – a single log line string.
- `parse_line($line)` – parses a log line.
- `top_n($counts, $n)` – returns top `$n` keys from a hash of counts.

#### Output
- `add` returns `1` on successful parse, `0` if line ignored, dies on line count overflow.
- `summarize` returns hashref:
  - `generated` – UTC timestamp.
  - `total` – total entries counted.
  - `hosts` – number of distinct hosts.
  - `busiest` – maximum count per host.
  - `slow` – number of requests > 1500 ms.
  - `statuses` – hashref of status code counts.
- `top_n` returns list of keys.

#### Core logic
- `new`: validate level, initialize counters.
- `parse_line`: regex extracts host, time, method, path, status, millis; normalizes host.
- `add`: enforce `MAX_LINES`; parse line; increment host and status counts; increment slow counter if millis > `SLOW_REQUEST_MS`.
- `summarize`: compute totals, busiest host, generate timestamp.
- `top_n`: sort counts descending, tie‑break by key, slice top `n`.

#### Subs
- `new(%args) -> LogDigest object` – create digest.
- `parse_line($line) -> hashref` – parse log line.
- `add($line) -> 1/0` – add entry.
- `summarize() -> hashref` – aggregate stats.
- `top_n($counts, $n) -> list` – top keys.
- `_normalize_host($host) -> string` – lowercase, strip trailing dot.

#### Constants and configuration
- `MAX_LINES => 50000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`

#### Dependencies
- `POSIX qw(strftime)`
- `List::Util qw(max sum)`
- `Carp qw(croak)`
- `Exporter` (parent)

#### Exceptions / Errors
- `croak` on unknown level in `new`.
- `croak` if more than `MAX_LINES` added.
- `parse_line` returns `undef` on regex mismatch.
