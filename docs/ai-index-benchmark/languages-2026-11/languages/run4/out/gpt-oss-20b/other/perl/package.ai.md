### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:33:08Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Condenses web‑server access logs into per‑host and per‑status summaries, exposing a CLI for reporting busiest hosts and slow requests.

#### Purpose
- Aggregate and summarize HTTP access logs.
- Provide a command‑line interface for quick reporting.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` parses individual log lines, counts hosts, statuses, and slow requests.
- **Reporting** – `log_digest.pl` consumes a `LogDigest` instance, applies host exclusions, and outputs human‑readable or JSON summaries.

#### Key units
- `LogDigest` (module) – constructor `new`, `add`, `summarize`, `top_n`; constants `MAX_LINES`, `SLOW_REQUEST_MS`.
- `log_digest.pl` (script) – `main`, `usage`, `read_aliases`; handles CLI options, file I/O, and output formatting.

#### Data flow
1. CLI parses options → creates `LogDigest` with chosen log level.  
2. For each input file: read lines → optionally skip excluded hosts → `LogDigest->add`.  
3. After all files: `LogDigest->summarize` → produce hash of totals, host counts, slow request count, status counts.  
4. Script formats the summary (plain text or JSON) and prints.

#### Dependencies
- **Internal**: `LogDigest` module functions (`top_n`, `new`, `add`, `summarize`).  
- **External**: `POSIX`, `List::Util`, `Carp` (in `LogDigest`); `FindBin`, `Getopt::Long`, `JSON::PP` (in script).  
- **Configuration**: `/etc/log-digest.conf` for host alias mapping.

#### Cross-cutting
- Host normalization (`_normalize_host`) ensures consistent counting.  
- Log level validation via `%LEVEL_RANK` and `DEFAULT_LEVEL`.  
- Error handling with `croak` (module) and `die` (script) for invalid input or file access.  
- Constants (`MAX_LINES`, `SLOW_REQUEST_MS`) guard against excessive input and define slow‑request threshold.
