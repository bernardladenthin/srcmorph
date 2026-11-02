### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:34:04Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web‑server access logs into per‑host and per‑status counts.

#### Purpose
- Parses log lines and aggregates host, status, and slow request statistics.
- Provides summary and top‑N host listings.

#### Type
- Perl module (class) `LogDigest` with public methods `new`, `add`, `summarize`, `top_n`; internal helper `_normalize_host`.

#### Input
- `new(level)` – optional log level string (`DEBUG`, `INFO`, `WARN`, `ERROR`).
- `add($line)` – raw log line string.
- `top_n($counts, $n)` – hashref of counts, integer N.
- `parse_line($line)` – raw log line string.

#### Output
- `new` – blessed object with `level`, `hosts`, `statuses`, `slow`, `line_count`.
- `add` – boolean success; updates object state.
- `summarize` – hashref: `generated`, `total`, `hosts`, `busiest`, `slow`, `statuses`.
- `top_n` – list of keys sorted by count descending, then alphabetically.
- `parse_line` – hashref of parsed fields or `undef`.

#### Core logic
- `parse_line`: regex extracts host, time, method, path, status, millis; normalizes host.
- `add`: limits to `MAX_LINES`; increments host/status counters; counts slow requests (`millis > SLOW_REQUEST_MS`).
- `summarize`: totals hosts, counts busiest host, records slow count, copies status map, timestamps generation.
- `top_n`: sorts counts by value descending, key ascending; returns top N keys.

#### Public API
- `new(level)` → `LogDigest` object – create digest with log level.
- `add(line)` → `bool` – add log line, update stats.
- `summarize()` → `hashref` – aggregate summary.
- `top_n(counts, n)` → `list` – top N keys by count.

#### Constants and configuration
- `MAX_LINES` = 50_000
- `DEFAULT_LEVEL` = 'WARN'
- `SLOW_REQUEST_MS` = 1500
- `%LEVEL_RANK` = { DEBUG=>0, INFO=>1, WARN=>2, ERROR=>3 }

#### Dependencies
- `POSIX qw(strftime)`
- `List::Util qw(max sum)`
- `Carp qw(croak)`

#### Exceptions / Errors
- `croak` on unknown level in `new`.
- `croak` if `add` exceeds `MAX_LINES`.
