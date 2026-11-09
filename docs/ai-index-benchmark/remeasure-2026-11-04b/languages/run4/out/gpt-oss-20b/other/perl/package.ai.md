### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:43:58Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> A Perl toolkit that parses web‑server access logs, aggregates per‑host and per‑status statistics, and exposes a command‑line utility for quick reporting or JSON output.

#### Purpose
- Summarize access logs into host‑ and status‑level counts.
- Identify slow requests and provide top‑N host or status lists.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` module handles line parsing, counting, and summarization.
- **Command‑line reporting** – `log_digest.pl` script reads log files, applies host filters, and outputs human‑readable or JSON summaries.

#### Key units
- `LogDigest` (module) – object with `new`, `add`, `summarize`, `top_n`; exports `parse_line`.
- `log_digest.pl` (script) – orchestrates file reading, host filtering, and output formatting.

#### Data flow
1. `log_digest.pl` loads host aliases and exclusion list.  
2. For each log file, it reads lines, extracts the host, skips excluded hosts, and feeds the line to a `LogDigest` instance via `add`.  
3. After all files, `summarize` produces a hashref with totals, host stats, slow request count, and status counts.  
4. The script prints the summary textually or as JSON, optionally listing the top N hosts.

#### Dependencies
- **Perl core**: `POSIX`, `List::Util`, `Carp`, `Exporter`.  
- **External modules**: `Getopt::Long`, `JSON::PP`, `FindBin`.  
- **Configuration**: `/etc/log-digest.conf` for host aliases.

#### Cross-cutting
- Host normalization (lowercase, strip trailing dot).  
- Logging level validation (`DEBUG`, `INFO`, `WARN`, `ERROR`).  
- Enforced line limit (`MAX_LINES = 50000`).  
- Slow request threshold (`SLOW_REQUEST_MS = 1500`).  
- Error handling via `croak` and `die` with clear messages.
