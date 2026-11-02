### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:31:28Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates web‑server access logs into per‑host and per‑status statistics, with host filtering and optional JSON output.

#### Purpose
- Parse raw log lines and aggregate request counts per host and status.
- Detect and count slow requests.
- Provide a summary of total requests, distinct hosts, busiest host, and status distribution.

#### Responsibilities
- **Log parsing** – extract host, time, method, path, status, and latency.
- **Aggregation** – maintain per‑host and per‑status counters, track slow requests.
- **Host filtering** – apply alias mapping and exclusion list.
- **Output formatting** – produce human‑readable or JSON summaries and top‑N host lists.

#### Key units
- **LogDigest** (Perl module) – constructor, `add`, `summarize`, `top_n`, `parse_line`, `_normalize_host`.
- **log_digest.pl** (script) – `main`, `read_aliases`, `usage`.
- **Constants** – `MAX_LINES`, `DEFAULT_LEVEL`, `SLOW_REQUEST_MS`, `%LEVEL_RANK`.

#### Data flow
- Script reads log files → extracts host → passes line to `LogDigest->add`.
- `LogDigest` updates internal counters and tracks slow requests.
- After all files, script calls `LogDigest->summarize` → obtains statistics.
- Script outputs summary and top hosts (text or JSON).

#### Dependencies
- **Internal** – `LogDigest` module.
- **External Perl modules** – `POSIX qw(strftime)`, `List::Util qw(max sum)`, `Carp qw(croak)`, `FindBin`, `Getopt::Long`, `JSON::PP`.

#### Cross-cutting
- Shared constants (`MAX_LINES`, `DEFAULT_LEVEL`, `SLOW_REQUEST_MS`, `%LEVEL_RANK`).
- Unified error handling (`croak` in `LogDigest`, `die` in script).
- Host normalization and alias mapping across parsing and filtering.
- Optional JSON output controlled by `--json` flag.
