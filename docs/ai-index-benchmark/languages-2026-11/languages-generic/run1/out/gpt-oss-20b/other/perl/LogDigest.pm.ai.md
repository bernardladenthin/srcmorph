### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:29:21Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web‑server access logs into per‑host and per‑status counts.

#### Purpose
- Parses log lines and aggregates statistics per host and status.
- Tracks slow requests and limits processed lines.

#### Type
- Class `LogDigest` (Perl module). Implements constructor, `add`, `summarize`, `top_n`, and helper `parse_line`, `_normalize_host`. Exports `parse_line` and `top_n`.

#### Input
- `new(level)` – log level string (`DEBUG`, `INFO`, `WARN`, `ERROR`).
- `add($line)` – raw log line string.
- `parse_line($line)` – raw log line string.
- `top_n($counts, $n)` – hashref of counts, integer n.

#### Output
- `new` returns blessed object with counters.
- `add` returns 1 on successful parse, 0 otherwise; updates internal state.
- `summarize` returns hashref: `generated`, `total`, `hosts`, `busiest`, `slow`, `statuses`.
- `top_n` returns list of top n keys sorted by count descending, then lexicographically.

#### Core logic
- `parse_line`: regex extracts host, time, method, path, status, millis; normalizes host.
- `add`: increments line count, updates host and status hashes, counts slow requests (>1500 ms).
- `summarize`: totals hosts, finds busiest host count, records current UTC timestamp, returns status hash copy.
- `top_n`: sorts keys by count descending, ties by key ascending, slices top n.

#### Public API
- `new(level)` → `LogDigest` object – create digest with log level.
- `add(line)` → `int` – add log line, update stats.
- `summarize()` → `hashref` – aggregate summary.
- `parse_line(line)` → `hashref` – parse single line.
- `top_n(counts, n)` → `list` – top n keys by count.

#### Constants and configuration
- `MAX_LINES` = 50000
- `DEFAULT_LEVEL` = 'WARN'
- `SLOW_REQUEST_MS` = 1500
- `%LEVEL_RANK` mapping levels to numeric rank.

#### Dependencies
- `POSIX qw(strftime)`
- `List::Util qw(max sum)`
- `Carp qw(croak)`

#### Exceptions / Errors
- `croak` on unknown level in `new`.
- `croak` if more than `MAX_LINES` added in `add`.
