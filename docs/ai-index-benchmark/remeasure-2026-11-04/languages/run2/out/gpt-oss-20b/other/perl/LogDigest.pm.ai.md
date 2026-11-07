### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:02:31Z
- G: 
- A: 0.0.0
- X: file
- P: 4F9ECAEF
---
> Condenses web‑server access logs into per‑host and per‑status counts, tracking slow requests.

#### Purpose
- Provides per‑host and per‑status statistics from log lines.
- Tracks number of slow requests.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: constructor `new`, methods `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – expects a log line matching the regex in `parse_line`.
- `parse_line($line)` – parses a single log line.
- `top_n($counts, $n)` – receives a hashref of counts and integer `n`.

#### Output
- `new` returns blessed object.
- `add` returns `1` on successful parse, `0` if line ignored, dies on line limit.
- `summarize` returns hashref:
  - `generated` timestamp UTC ISO‑8601.
  - `total` total lines processed.
  - `hosts` number of distinct hosts.
  - `busiest` max requests per host.
  - `slow` count of requests > `SLOW_REQUEST_MS`.
  - `statuses` hashref of status counts.
- `top_n` returns list of top `n` keys sorted by count descending, then key ascending.

#### Core logic
- `new`: validate level, initialize counters.
- `parse_line`: regex extracts host, time, method, path, status, millis; normalizes host.
- `add`: enforce `MAX_LINES`; parse line; increment host and status counters; increment slow counter if millis > `SLOW_REQUEST_MS`.
- `summarize`: compute totals, busiest host, generate UTC timestamp.
- `top_n`: sort counts by value descending, key ascending; slice top `n`.

#### Subs
- `new(%args) -> object` – create digest with level.
- `parse_line($line) -> hashref` – parse log line.
- `add($line) -> 1|0` – add line to digest.
- `summarize() -> hashref` – return summary.
- `top_n($counts, $n) -> list` – top `n` keys.
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
- `new` dies with `croak` if unknown level.
- `add` dies with `croak` if line count exceeds `MAX_LINES`.
