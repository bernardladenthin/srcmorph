### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:58:02Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates web‑server access logs into per‑host and per‑status statistics, exposing a reusable module and a command‑line tool for reporting busiest hosts and slow requests.

#### Purpose
- Provide a reusable log‑digesting class (`LogDigest`) for parsing and summarizing access logs.
- Offer a CLI (`log_digest.pl`) that reads log files, applies host exclusions/aliases, and outputs human‑readable or JSON summaries.

#### Responsibilities
- **Parsing & Aggregation** – `LogDigest` parses individual log lines, normalizes hosts, counts requests, tracks slow requests, and tallies status codes.
- **Reporting** – `log_digest.pl` orchestrates file I/O, applies configuration (host aliases, exclusions), feeds lines to `LogDigest`, and formats the final summary.
- **Utility** – `LogDigest` supplies `top_n` for ranking hosts by request count.

#### Key units
- `LogDigest` (module) – constructor `new(level => $lvl)`, `add($line)`, `summarize()`, `top_n($counts, $n)`.
- `log_digest.pl` (script) – parses options (`--top`, `--level`, `--json`, `--exclude-host`), loads host aliases, iterates over log files, and prints summary.

#### Data flow
1. **CLI** parses options → creates `LogDigest` with chosen level.  
2. For each log file: read line → extract host → skip if excluded → `$digest->add($line)`.  
3. After all files: `$digest->summarize()` → `$summary`.  
4. Output: JSON or formatted text + top hosts via `top_n`.

#### Dependencies
- **Internal**: `LogDigest` module used by `log_digest.pl`.  
- **External**: `POSIX`, `List::Util`, `Carp`, `Exporter` (module); `Getopt::Long`, `JSON::PP`, `FindBin` (script).

#### Cross-cutting
- Host normalization (`_normalize_host`) ensures consistent counting.  
- `MAX_LINES` guard prevents runaway memory usage.  
- Error handling via `croak` in the module; `die` with usage messages in the script.  
- Configuration via `/etc/log-digest.conf` for host alias mapping.
