### LogDigest.pm
- H: 1.0
- C: 63E90A09
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:48:12Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Condenses web‑server access logs into per‑host and per‑status counts, tracking slow requests.

#### Purpose
- Parse and aggregate web‑server log lines.
- Provide per‑host, per‑status summaries and slow‑request statistics.

#### Type
- Module `LogDigest`.
- Exports `parse_line`, `top_n`.
- Object‑oriented: constructor `new`, methods `add`, `summarize`.

#### Input
- `parse_line($line)` expects a log line matching  
  `host date method path status millis`.
- `add($line)` reads a single log line.
- `new(level => $level)` accepts `level` (`DEBUG`, `INFO`, `WARN`, `ERROR`).

#### Output
- `parse_line` returns hashref `{host, time, method, path, status, millis}` or `undef`.
- `add` returns `1` on success, `0` if line unparsable, dies on line limit.
- `summarize` returns hashref `{generated, total, hosts, busiest, slow, statuses}`.
- `top_n` returns list of top N keys from a count hash.

#### Core logic
- `new`: validate level, initialize counters.
- `parse_line`: regex extraction, host normalization.
- `add`: limit to `MAX_LINES`; update host/status counts; increment slow counter if `millis > SLOW_REQUEST_MS`.
- `summarize`: compute total requests, host count, busiest host, slow count, copy status counts; timestamp with UTC.
- `top_n`: sort counts descending, tie‑break by key lexicographically, slice top N.
- `_normalize_host`: lowercase, strip trailing dot.

#### Subs
- `new(%args) -> object` – create digest with level.
- `parse_line($line) -> hashref` – parse log line.
- `add($line) -> 1/0` – add entry, update stats.
- `summarize() -> hashref` – aggregate summary.
- `top_n($counts, $n) -> list` – top N keys.
- `_normalize_host($host) -> host` – lowercase, strip dot.

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
- `croak` on unknown level in `new`.
- `croak` if more than `MAX_LINES` added in `add`.
