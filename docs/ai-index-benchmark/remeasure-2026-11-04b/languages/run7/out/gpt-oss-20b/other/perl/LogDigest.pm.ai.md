### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:46:19Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Condenses web‑server access logs into per‑host and per‑status counts, tracking slow requests.

#### Purpose
- Provides a log‑digesting class for summarizing access logs.
- Offers helper `top_n` for ranking counts.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: constructor `new`, methods `add`, `summarize`.

#### Input
- `new(level => $level)` – `$level` must be one of `DEBUG`, `INFO`, `WARN`, `ERROR`; defaults to `WARN`.
- `add($line)` – expects a log line matching the regex in `parse_line`.
- `parse_line($line)` – parses a single log line.
- `top_n($counts, $n)` – ranks hash values.

#### Output
- `add` returns `1` on successful parse, `0` otherwise; dies if line count exceeds `MAX_LINES`.
- `summarize` returns hashref:
  - `generated` – UTC timestamp.
  - `total` – total entries processed.
  - `hosts` – number of distinct hosts.
  - `busiest` – max requests per host.
  - `slow` – count of requests > `SLOW_REQUEST_MS`.
  - `statuses` – hashref of status counts.
- `top_n` returns array of top `$n` keys.

#### Core logic
- `new`: validate level, initialize counters.
- `parse_line`: regex extracts host, time, method, path, status, millis; normalizes host.
- `add`: enforce `MAX_LINES`; parse line; increment host and status counts; increment `slow` if millis > `SLOW_REQUEST_MS`.
- `summarize`: compute totals, busiest host, generate UTC timestamp.
- `top_n`: sort keys by count descending, then alphabetically; slice top `$n`.

#### Subs
- `new(%args) -> LogDigest` – create instance with level.
- `parse_line($line) -> hashref` – parse log line.
- `add($line) -> 1|0` – add entry, update stats.
- `summarize() -> hashref` – return digest summary.
- `top_n($counts, $n) -> @keys` – rank counts.
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
