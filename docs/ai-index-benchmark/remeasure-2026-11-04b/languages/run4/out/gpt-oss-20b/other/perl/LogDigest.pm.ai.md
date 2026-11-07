### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:43:29Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Condenses web‑server access logs into per‑host and per‑status counts, tracking slow requests.

#### Purpose
- Provides a log‑digesting object for summarizing access logs.
- Offers utility to retrieve top N hosts or statuses.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: `new`, `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR` (default `WARN`).
- `add($line)` – a single log line string.
- `parse_line($line)` – parses a log line.
- `top_n($counts, $n)` – receives a hashref of counts and integer `n`.

#### Output
- `add` returns `1` on successful parse, `0` if line ignored, dies on too many lines.
- `summarize` returns hashref:
  - `generated` – UTC timestamp.
  - `total` – total entries counted.
  - `hosts` – number of distinct hosts.
  - `busiest` – max count for any host.
  - `slow` – number of requests > 1500 ms.
  - `statuses` – hashref of status counts.
- `top_n` returns list of keys sorted by count descending, ties broken alphabetically.

#### Core logic
- `new`: validate level, initialize counters.
- `parse_line`: regex extracts host, time, method, path, status, millis; normalizes host.
- `add`: enforce `MAX_LINES`; parse line; increment host and status counters; count slow requests.
- `summarize`: compute totals, busiest host, generate UTC timestamp.
- `top_n`: sort counts descending, return top `n` keys.
- `_normalize_host`: lowercase, strip trailing dot.

#### Subs
- `new(%args) -> object` – create digest with level.
- `parse_line($line) -> hashref` – parse log line.
- `add($line) -> 1|0` – add entry, update stats.
- `summarize() -> hashref` – return summary.
- `top_n($counts, $n) -> list` – top N keys.
- `_normalize_host($host) -> host` – lowercase, strip dot.

#### Constants and configuration
- `MAX_LINES => 50000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`
- `%LEVEL_RANK = (DEBUG => 0, INFO => 1, WARN => 2, ERROR => 3)`

#### Dependencies
- `POSIX qw(strftime)`
- `List::Util qw(max sum)`
- `Carp qw(croak)`
- `Exporter` (parent)

#### Exceptions / Errors
- `croak` if unknown level in `new`.
- `croak` if more than `MAX_LINES` added in `add`.
