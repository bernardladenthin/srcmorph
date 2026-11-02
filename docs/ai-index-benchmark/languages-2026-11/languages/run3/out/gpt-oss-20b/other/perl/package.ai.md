### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:56:14Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Summarizes web‑server access logs, producing per‑host and per‑status statistics and exposing a command‑line tool for quick reporting.

#### Purpose
- Aggregate access‑log data into host‑level and status‑code metrics.
- Provide a reusable `LogDigest` class and a CLI script for reporting.

#### Responsibilities
- **Log parsing & counting** – `LogDigest` parses individual log lines, normalizes hosts, tracks request counts, slow requests, and status codes.
- **Summary generation** – `summarize` compiles totals, busiest host, and slow‑request metrics.
- **Top‑N extraction** – `top_n` returns the highest‑count hosts or statuses.
- **CLI orchestration** – `log_digest.pl` loads host aliases, applies exclusions, feeds files to `LogDigest`, and outputs results in text or JSON.

#### Key units
- `LogDigest` (module) – constructor `new`, methods `add`, `summarize`, `top_n`; constants `MAX_LINES`, `SLOW_REQUEST_MS`, `%LEVEL_RANK`.
- `log_digest.pl` (script) – `main`, `usage`, `read_aliases`; uses `Getopt::Long`, `JSON::PP`, `LogDigest`.

#### Data flow
1. **CLI** parses options → builds exclusion set and host alias map.  
2. **CLI** creates `LogDigest` with chosen log level.  
3. For each input file: read line → skip excluded hosts → `$digest->add($line)`.  
4. After all files: `$digest->summarize()` → `$summary`.  
5. **CLI** outputs `$summary` as plain text or JSON.

#### Dependencies
- Internal: `LogDigest` module (exports `parse_line`, `top_n`).  
- External Perl modules: `POSIX`, `List::Util`, `Carp`, `Getopt::Long`, `JSON::PP`, `FindBin`.  
- Configuration file (`LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`) for host alias mappings.

#### Cross-cutting
- Host normalization (`_normalize_host`) ensures consistent counting.  
- Error handling via `Carp::croak` for invalid levels or line limits.  
- Logging level ranking (`%LEVEL_RANK`) controls verbosity.  
- Constants (`MAX_LINES`, `SLOW_REQUEST_MS`) guard performance and define slow‑request threshold.
