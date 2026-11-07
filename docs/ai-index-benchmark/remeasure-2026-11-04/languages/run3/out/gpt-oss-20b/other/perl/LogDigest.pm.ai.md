### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:05:32Z
- G: 
- A: 0.0.0
- X: file
- P: 4F9ECAEF
---
> Condenses web‑server access logs into per‑host and per‑status counts, tracking slow requests.

#### Purpose
- Provides a log‑digesting class for web‑server access logs.
- Counts per‑host, per‑status, and slow requests.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: `new`, `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – a single log line string.
- `parse_line($line)` – parses a log line.
- `top_n($counts, $n)` – helper for ranking counts.

#### Output
- `add` returns `1` on successful parse, `0` if line ignored, dies on too many lines.
- `summarize` returns hashref:
  - `generated` timestamp UTC.
  - `total` total entries.
  - `hosts` number of distinct hosts.
  - `busiest` max requests per host.
  - `slow` count of requests > 1500 ms.
  - `statuses` hashref of status → count.
- `top_n` returns array of top `$n` keys sorted by count descending, then alphabetically.

#### Core logic
- `new`: validate level, initialize counters.
- `parse_line`: regex extracts host, time, method, path, status, millis; normalizes host.
- `add`: enforce `MAX_LINES`; parse line; increment host/status counters; increment slow counter if millis > `SLOW_REQUEST_MS`.
- `summarize`: compute totals, busiest, and assemble summary hash.
- `top_n`: sort counts by value desc, key asc; slice top `$n`.

#### Subs
- `new(%args) -> object` – create digest with level.
- `parse_line($line) -> hashref` – parse log line.
- `add($line) -> 1|0` – add entry.
- `summarize() -> hashref` – summary stats.
- `top_n($counts, $n) -> array` – top keys.
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
