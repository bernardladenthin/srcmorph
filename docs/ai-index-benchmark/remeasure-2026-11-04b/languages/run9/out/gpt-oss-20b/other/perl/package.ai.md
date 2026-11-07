### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:48:43Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Condenses web‑server access logs into per‑host and per‑status summaries, highlighting slow requests and providing a CLI for reporting.

#### Purpose
- Aggregate and summarize HTTP access logs.
- Expose a command‑line tool for quick host‑usage and status‑code reporting.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` module parses individual log lines, normalizes hosts, counts requests per host and status, tracks slow requests.
- **Reporting** – `log_digest.pl` reads log files, applies host exclusions, outputs a concise summary or JSON, and supports configuration via a host‑alias file.

#### Key units
- `LogDigest` (module) – constructor `new`, `add`, `summarize`, `top_n`; constants `MAX_LINES`, `SLOW_REQUEST_MS`.
- `log_digest.pl` (script) – `main`, `usage`, `read_aliases`; options `--top`, `--level`, `--json`, `--exclude-host`.

#### Data flow
1. **CLI** parses options → creates `LogDigest` with chosen level.  
2. **File loop**: each log line is read, host checked against exclusion set, then passed to `$digest->add`.  
3. After all files, `$digest->summarize` produces a hash of totals, host counts, slow request count, and status counts.  
4. **Output**: either JSON via `JSON::PP` or formatted text with `top_n` host statistics.

#### Dependencies
- Internal: `LogDigest` module.  
- External Perl modules: `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, `Carp`, `Exporter`.  
- External data: optional host‑alias config file (`/etc/log-digest.conf`).

#### Cross-cutting
- Host normalization (`_normalize_host`) ensures consistent counting.  
- Error handling via `croak` in the module and `die` in the script for invalid options or file I/O.  
- Logging level validation in `LogDigest::new`.  
- Constants (`MAX_LINES`, `SLOW_REQUEST_MS`) guard against excessive input and define slow‑request threshold.
