### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:44:23Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Condenses web‑server access logs into per‑host and per‑status counts, tracking slow requests.

#### Purpose
- Provides a log‑digesting object for summarizing access logs.
- Offers a helper `top_n` to rank counts.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: `new`, `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – parses a single log line.
- `parse_line($line)` – regex parses host, time, method, path, status, millis.
- `top_n($counts, $n)` – ranks hash values.

#### Output
- `add` returns `1` on successful parse, `0` if line ignored.
- `summarize` returns hashref:
  - `generated` timestamp UTC.
  - `total` total entries.
  - `hosts` number of distinct hosts.
  - `busiest` max requests per host.
  - `slow` count of requests > 1500 ms.
  - `statuses` hashref of status counts.
- `top_n` returns array of top `$n` keys.

#### Core logic
- `new` validates log level, initializes counters.
- `parse_line` extracts fields via regex; normalizes host.
- `add`:
  - Enforces `MAX_LINES` limit.
  - Increments host and status counters.
  - Increments `slow` if `millis` > `SLOW_REQUEST_MS`.
- `summarize` aggregates totals, computes busiest host, timestamps.
- `top_n` sorts keys by count descending, ties by lexical order.

#### Subs
- `new(%args) -> object` – create digest with level.
- `parse_line($line) -> hashref` – parse log line.
- `add($line) -> 1/0` – add entry, update counters.
- `summarize() -> hashref` – produce summary.
- `top_n($counts, $n) -> array` – rank keys.
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
