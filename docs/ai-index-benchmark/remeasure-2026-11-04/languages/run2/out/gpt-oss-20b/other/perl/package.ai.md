### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:03:10Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> A Perl toolkit that parses web‑server access logs, aggregates per‑host and per‑status statistics, and reports the busiest hosts and slow requests, optionally as JSON.

#### Purpose
- Aggregate access‑log metrics (host counts, status tallies, slow request counts).
- Provide a command‑line utility to output summaries and top hosts.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` module handles line parsing, counting, and summarization.
- **Command‑line interface** – `log_digest.pl` script orchestrates file reading, host filtering, alias resolution, and output formatting.

#### Key units
- **`LogDigest`** – OO module; `new`, `add`, `summarize`, `top_n`; constants `MAX_LINES`, `SLOW_REQUEST_MS`.
- **`log_digest.pl`** – script; uses `Getopt::Long`, `JSON::PP`, `LogDigest`; functions `usage`, `read_aliases`, `main`.

#### Data flow
1. `log_digest.pl` parses options and loads host aliases.
2. For each input log file, it reads lines, skips excluded hosts, and feeds each line to a `LogDigest` instance via `add`.
3. After all files, `summarize` produces a hash of totals, host counts, slow request count, and status tallies.
4. The script outputs either plain text or JSON, including the top N hosts via `top_n`.

#### Dependencies
- **Internal**: `LogDigest` module, `log_digest.pl` script.
- **External**: `POSIX`, `List::Util`, `Carp`, `Exporter` (module); `FindBin`, `Getopt::Long`, `JSON::PP` (script).

#### Cross-cutting
- Host normalization (`_normalize_host`) ensures consistent counting.
- Error handling via `Carp::croak` for invalid levels or line limits.
- Configuration via environment variable `LOG_DIGEST_CONFIG` and optional host alias file.
- JSON output uses canonical encoding for deterministic ordering.
