### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:17:55Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Condenses web‑server access logs into per‑host and per‑status counts, tracking slow requests.

#### Purpose
- Provides a log‑digesting utility for web‑server access logs.
- Counts requests per host, per status, and flags slow requests.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: constructor `new`, instance methods `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – a single log line string.
- `parse_line($line)` – parses a log line.
- `top_n($counts, $n)` – selects top `$n` keys from a hash of counts.

#### Output
- `add` returns `1` on successful parse, `0` if line ignored, dies if line count exceeds `MAX_LINES`.
- `summarize` returns a hashref:
  - `generated` – UTC timestamp.
  - `total` – total request count.
  - `hosts` – number of distinct hosts.
  - `busiest` – max requests from a single host.
  - `slow` – count of requests > `SLOW_REQUEST_MS`.
  - `statuses` – hashref of status code counts.
- `top_n` returns an array of keys sorted by count descending, then lexicographically.

#### Core logic
- `new` validates log level, initializes counters.
- `parse_line` extracts host, time, method, path, status, millis via regex; normalizes host.
- `add` increments line counter, updates host/status counts, tracks slow requests.
- `summarize` aggregates totals, computes busiest host, formats timestamp.
- `top_n` sorts hash keys by value descending, ties by key ascending, slices top `$n`.

#### Subs
- `new(%args) -> LogDigest` – create digest object.
- `parse_line($line) -> hashref` – parse log line.
- `add($line) -> 1|0` – add entry to digest.
- `summarize() -> hashref` – produce summary.
- `top_n($counts, $n) -> @keys` – top N keys by count.
- `_normalize_host($host) -> $host` – lowercase, strip trailing dot.

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
- `add` dies if more than `MAX_LINES` lines added.
- `parse_line` silently returns `undef` on regex mismatch.
