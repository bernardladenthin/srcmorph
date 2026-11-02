### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:57:19Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web‑server access logs into per‑host and per‑status counts.

#### Purpose
- Provides a class to parse log lines and aggregate statistics.
- Offers a helper `top_n` to rank counts.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: `new`, `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – a single log line string.
- `parse_line($line)` – same format as `add`.
- `top_n($counts, $n)` – hashref of counts, integer `n`.

#### Output
- `add` returns `1` on successful parse, `0` if line ignored, dies on too many lines.
- `summarize` returns hashref:
  - `generated` – UTC timestamp.
  - `total` – total parsed lines.
  - `hosts` – number of distinct hosts.
  - `busiest` – max requests per host.
  - `slow` – count of requests > 1500 ms.
  - `statuses` – hashref of status → count.
- `top_n` returns array of top `n` keys sorted by count descending, then key ascending.

#### Core logic
- `new`: validate level, initialize counters.
- `parse_line`: regex extracts host, time, method, path, status, millis; normalizes host.
- `add`: enforce `MAX_LINES`; parse line; increment host/status counters; count slow requests.
- `summarize`: compute totals, busiest host, format timestamp.
- `top_n`: sort counts by value then key; slice top `n`.

#### Subs
- `new(%args) -> object` – create digest with level.
- `parse_line($line) -> hashref` – parse log entry.
- `add($line) -> 1/0` – add entry, update stats.
- `summarize() -> hashref` – aggregate summary.
- `top_n($counts, $n) -> list` – top keys by count.
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
