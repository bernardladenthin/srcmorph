### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:25:14Z
- G: 
- A: 0.0.0
- X: file
- P: B953BC65
---
> Condenses web‑server access logs into per‑host and per‑status counts, tracking slow requests.

#### Purpose
- Provides a digest of access log entries.
- Counts per host, per status, and slow requests.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: `new`, `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – a single log line string.
- `parse_line($line)` – parses a log line.
- `top_n($counts, $n)` – helper for ordering counts.

#### Output
- `add` returns `1` on successful parse, `0` if line ignored, dies on too many lines.
- `summarize` returns hashref:
  - `generated` – UTC timestamp.
  - `total` – total entries counted.
  - `hosts` – distinct host count.
  - `busiest` – max entries for a single host.
  - `slow` – number of requests > 1500 ms.
  - `statuses` – hashref of status counts.
- `top_n` returns array of keys sorted by count descending, then lexicographically.

#### Core logic
- `new` validates log level, initializes counters.
- `parse_line` extracts host, time, method, path, status, millis via regex; normalizes host.
- `add` limits to `MAX_LINES`; updates host and status counters; increments `slow` if millis > `SLOW_REQUEST_MS`.
- `summarize` aggregates totals, computes busiest host, formats timestamp.
- `top_n` sorts counts descending, ties broken alphabetically.

#### Subs
- `new(%args) -> object` – create digest with level.
- `parse_line($line) -> hashref` – parse log line.
- `add($line) -> 1|0` – add entry, update counters.
- `summarize() -> hashref` – produce summary.
- `top_n($counts, $n) -> list` – sorted keys.
- `_normalize_host($host) -> host` – lowercase, strip trailing dot.

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
- `croak` if unknown level in `new`.
- `croak` if more than `MAX_LINES` added in `add`.
