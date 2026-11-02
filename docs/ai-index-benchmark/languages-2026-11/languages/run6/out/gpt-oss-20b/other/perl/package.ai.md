### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:47:40Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates web‑server access logs, producing per‑host and per‑status summaries and a CLI tool for reporting the busiest hosts and status‑code tallies.

#### Purpose
- Collect and summarize HTTP access log entries.
- Provide a command‑line utility to display host usage and status statistics.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` parses individual log lines, normalizes hosts, counts requests, tracks slow requests, and tallies status codes.
- **Reporting** – `log_digest.pl` reads log files, applies host exclusions and aliases, feeds entries to `LogDigest`, then outputs either plain text or JSON summaries, including the top N hosts.

#### Key units
- **`LogDigest` module** – OO class with `new`, `add`, `summarize`, and helper `parse_line`, `top_n`.  
- **`log_digest.pl` script** – CLI entry point; parses options, loads host aliases, feeds logs to `LogDigest`, and prints results.

#### Data flow
1. CLI parses options → creates `LogDigest` instance.  
2. For each log file: read line → optionally skip excluded hosts → `LogDigest->add`.  
3. After all files: `LogDigest->summarize` → `$summary`.  
4. CLI formats `$summary` (text or JSON) and prints top hosts via `top_n`.

#### Dependencies
- **Internal**: `LogDigest` module used by the script.  
- **External**: `POSIX`, `List::Util`, `Carp` (in `LogDigest`); `Getopt::Long`, `JSON::PP`, `FindBin` (in script).

#### Cross-cutting
- Host normalization (lowercase, strip trailing dot) is shared.  
- Constants: `MAX_LINES = 50000`, `SLOW_REQUEST_MS = 1500`, default log level `'WARN'`.  
- Error handling via `croak` in `LogDigest`; `die` with usage in the script.  
- Configuration: optional host‑alias file (`LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`).
