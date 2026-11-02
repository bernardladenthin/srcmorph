### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:18:45Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates web‑server access logs into per‑host and per‑status statistics, then exposes a CLI to report the busiest hosts and status‑code counts.

#### Purpose
- Provide a reusable log‑digesting library.
- Offer a command‑line tool for quick log summaries.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` parses individual log lines, normalizes hosts, counts requests, tracks slow requests, and aggregates status codes.
- **Reporting** – `log_digest.pl` reads log files, applies host exclusions and aliases, feeds lines to `LogDigest`, then outputs a human‑readable or JSON summary.

#### Key units
- `LogDigest` (module) – constructor `new`, `add`, `summarize`, `top_n`, `parse_line`.
- `log_digest.pl` (script) – `main`, `usage`, `read_aliases`.

#### Data flow
1. `log_digest.pl` loads host aliases and exclusion list.
2. For each input file, it streams lines, skips excluded hosts, and calls `$digest->add($line)`.
3. `LogDigest::add` parses the line, updates host and status counters, and tracks slow requests.
4. After all files, `summarize` compiles totals, busiest host, and status counts.
5. `log_digest.pl` formats the summary (text or JSON) and prints it.

#### Dependencies
- **Internal**: `LogDigest` module used by the script.
- **External**: `POSIX`, `List::Util`, `Carp`, `Exporter` (in `LogDigest`); `FindBin`, `Getopt::Long`, `JSON::PP` (in script).

#### Cross-cutting
- Host normalization (`_normalize_host`) ensures consistent counting.
- Constants `MAX_LINES`, `SLOW_REQUEST_MS`, `DEFAULT_LEVEL` centralize limits and thresholds.
- Error handling via `croak` in the module and `die` in the script for configuration and I/O issues.
