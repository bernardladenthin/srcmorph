### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:15:27Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> A Perl toolkit that parses web‑server access logs, aggregates per‑host and per‑status statistics, and reports the busiest hosts and slow requests.

#### Purpose
- Summarize access logs into host‑level counts and status‑code tallies.
- Provide a CLI for quick reporting, optionally in JSON.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` parses individual log lines, normalizes hosts, tracks counts, and flags slow requests.
- **Reporting** – `log_digest.pl` orchestrates file reading, applies host exclusions, and formats output (plain text or JSON).
- **Utility helpers** – `top_n` ranks counts; `parse_line` extracts fields via regex.

#### Key units
- **LogDigest** (module) – OO interface: `new`, `add`, `summarize`, `top_n`; exports `parse_line`.
- **log_digest.pl** (script) – command‑line driver: `main`, `usage`, `read_aliases`.

#### Data flow
1. `log_digest.pl` loads host aliases and exclusions.
2. For each log file, each line is passed to `LogDigest->add`.
3. `LogDigest` parses the line, updates host/status counters, and tracks slow requests.
4. After all files, `summarize` produces a hashref of totals, busiest host, slow count, and status counts.
5. `log_digest.pl` outputs the summary as text or JSON.

#### Dependencies
- **Internal**: `LogDigest` module, `top_n`, `parse_line`.
- **External**: `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, `Carp`, `Exporter`.

#### Cross-cutting
- Host normalization (`_normalize_host`) ensures consistent counting.
- Error handling via `croak` for invalid levels or exceeding `MAX_LINES`.
- Configurable log level (`DEBUG`, `INFO`, `WARN`, `ERROR`) influences verbosity (not shown in summaries but present in module).
- JSON output uses canonical ordering for reproducibility.
