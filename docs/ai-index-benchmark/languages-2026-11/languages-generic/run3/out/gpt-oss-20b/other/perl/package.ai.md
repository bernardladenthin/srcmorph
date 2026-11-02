### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:41:13Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> A Perl utility that parses web‑server access logs, aggregates per‑host and per‑status statistics, and reports the busiest hosts and slow requests.

#### Purpose
- Aggregate web‑server access log metrics.
- Identify top N hosts and slow requests.
- Output human‑readable or JSON summaries.

#### Responsibilities
- **Log parsing & counting** – `LogDigest` module extracts fields, updates host/status counters, tracks slow requests.
- **Command‑line orchestration** – `log_digest.pl` handles arguments, loads host aliases, filters exclusions, feeds lines to `LogDigest`, and formats the final report.

#### Key units
- `LogDigest` (class) – parses lines, maintains `hosts`, `statuses`, `slow`, `line_count`; provides `add`, `summarize`, `top_n`.
- `log_digest.pl` – script entry point `main()`, helper `read_aliases`, `usage`; orchestrates file reading and output.

#### Data flow
1. `log_digest.pl` parses CLI options → creates `LogDigest` with chosen level.  
2. For each log file: read line → apply alias/exclusion → `$digest->add($line)`.  
3. After all files: `$digest->summarize()` → totals, busiest host, slow count.  
4. Format output: plain text or JSON via `JSON::PP`.

#### Dependencies
- Internal: `LogDigest` module.  
- External: `FindBin`, `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`.

#### Cross-cutting
- **Error handling** – `croak` for invalid levels or exceeding `MAX_LINES`; `die` for CLI misuse or file I/O.  
- **Configuration** – host alias mapping file (`/etc/log-digest.conf` by default).  
- **Constants** – `MAX_LINES=50000`, `SLOW_REQUEST_MS=1500`, default log level `WARN`.  
- **Sorting** – `top_n` uses descending count, ties alphabetically.
