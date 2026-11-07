### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:21:15Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Condenses web‑server access logs into per‑host and per‑status counts, tracking slow requests.

#### Purpose
- Provides a log‑digesting utility for web‑server access logs.
- Counts requests per host, per status code, and flags slow requests.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: constructor `new`, methods `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – expects a log line matching the regex in `parse_line`.
- `parse_line($line)` – parses a single log line.
- `top_n($counts, $n)` – returns top `$n` keys from a hash of counts.

#### Output
- `add` returns `1` on successful parse, `0` if line ignored, dies on too many lines.
- `summarize` returns hashref:
  - `generated` – UTC timestamp.
  - `total` – total request count.
  - `hosts` – number of distinct hosts.
  - `busiest` – max requests from a single host.
  - `slow` – count of requests > 1500 ms.
  - `statuses` – hashref of status code counts.
- `top_n` returns list of keys sorted by count descending, then lexicographically.

#### Core logic
- `new`: validate log level, initialize counters.
- `parse_line`: regex extracts host, time, method, path, status, millis; normalizes host.
- `add`: enforce `MAX_LINES`; parse line; increment host and status counters; increment slow counter if millis > `SLOW_REQUEST_MS`.
- `summarize`: compute totals, busiest host, and assemble summary hash.
- `top_n`: sort hash keys by count descending, tie‑break by key; slice top `$n`.

#### Subs
- `new(%args) -> object` – create digest with level.
- `parse_line($line) -> hashref` – parse log line.
- `add($line) -> 1|0` – add entry, update counters.
- `summarize() -> hashref` – produce summary.
- `top_n($counts, $n) -> list` – top N keys.
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
- `Exporter` (parent)

#### Exceptions / Errors
- `croak` if unknown level in `new`.
- `croak` if more than `MAX_LINES` added in `add`.
