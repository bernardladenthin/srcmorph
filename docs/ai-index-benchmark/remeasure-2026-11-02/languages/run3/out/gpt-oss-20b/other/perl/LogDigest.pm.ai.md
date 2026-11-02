### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:56:05Z
- G: 
- A: 0.0.0
- X: file
- P: B953BC65
---
> Condenses web‑server access logs into per‑host and per‑status counts.

#### Purpose
- Provides a log‑digesting object for summarizing access logs.
- Exposes `parse_line` and `top_n` for external use.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: `new`, `add`, `summarize`.

#### Output
- `new` returns a blessed hashref.
- `add` returns `1` on success, `0` if line unparsable.
- `summarize` returns a hashref with keys: `generated`, `total`, `hosts`, `busiest`, `slow`, `statuses`.
- `top_n` returns a list of keys sorted by count.

#### Core logic
- `new`: validate log level, initialize counters.
- `parse_line`: regex‑parse a log line into fields; normalize host.
- `add`: enforce `MAX_LINES`; parse line; increment host and status counts; count slow requests.
- `summarize`: compute totals, busiest host count, current UTC timestamp.
- `top_n`: sort hash by value descending, key ascending; slice top `n`.
- `_normalize_host`: lowercase, strip trailing dot.

#### Subs
- `new(%args) -> object` – create digest with level.
- `parse_line($line) -> hashref` – parse log entry.
- `add($line) -> 1|0` – add entry, update stats.
- `summarize() -> hashref` – produce summary.
- `top_n($counts, $n) -> list` – top keys by count.
- `_normalize_host($host) -> string` – lowercase, strip dot.

#### Constants and configuration
- `MAX_LINES => 50000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`

#### Dependencies
- `POSIX qw(strftime)`
- `List::Util qw(max sum)`
- `Carp qw(croak)`
- `parent 'Exporter'`

#### Exceptions / Errors
- `croak "unknown level '$level'"` if level not in `%LEVEL_RANK`.
- `croak 'more than ' . MAX_LINES . ' lines'` when exceeding limit.
- `parse_line` returns `undef` on regex mismatch; `add` returns `0` in that case.
