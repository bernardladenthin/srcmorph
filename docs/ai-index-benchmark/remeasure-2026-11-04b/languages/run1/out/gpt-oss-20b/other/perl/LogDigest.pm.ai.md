### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:14:31Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Condenses web‑server access logs into per‑host and per‑status counts, tracking slow requests.

#### Purpose
- Provides a log‑digesting object for summarizing access logs.
- Offers helper `top_n` for ranking counts.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: `new`, `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – expects a log line matching the regex in `parse_line`.
- `parse_line($line)` – parses a single log line; returns `undef` if format mismatches.

#### Output
- `add` returns `1` on successful parse, `0` otherwise; dies if line count exceeds `MAX_LINES`.
- `summarize` returns a hashref:
  - `generated` – UTC timestamp.
  - `total` – total parsed lines.
  - `hosts` – number of distinct hosts.
  - `busiest` – max requests per host.
  - `slow` – count of requests > `SLOW_REQUEST_MS`.
  - `statuses` – hashref of status code counts.
- `top_n($counts, $n)` returns an array of the top `$n` keys sorted by count descending, then lexicographically.

#### Core logic
- `new` validates log level, initializes counters.
- `parse_line` extracts host, time, method, path, status, millis via regex.
- `_normalize_host` lower‑cases host, strips trailing dot.
- `add` parses line, updates host and status counters, increments slow counter if millis > 1500 ms.
- `summarize` aggregates totals, computes busiest host, returns summary hashref.
- `top_n` sorts counts and slices top N.

#### Subs
- `new(%args) -> object` – create digest with level.
- `parse_line($line) -> hashref|undef` – parse log line.
- `add($line) -> 1|0` – add parsed line to digest.
- `summarize() -> hashref` – produce summary.
- `top_n($counts, $n) -> array` – rank counts.
- `_normalize_host($host) -> host` – lower‑case, strip trailing dot.

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
