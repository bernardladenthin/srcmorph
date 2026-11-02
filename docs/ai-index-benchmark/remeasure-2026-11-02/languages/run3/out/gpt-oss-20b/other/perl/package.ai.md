### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:56:33Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Summarizes web‑server access logs, counting requests per host, status codes, and slow requests, and reports the busiest hosts.

#### Purpose
- Aggregate and report access‑log statistics.
- Provide a CLI tool for quick log analysis.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` parses individual log lines, normalizes hosts, and tallies counts.
- **Statistical summarization** – `summarize` produces totals, busiest host, slow‑request count, and status‑code distribution.
- **Top‑N host extraction** – `top_n` returns the most frequent hosts.
- **Command‑line interface** – `log_digest.pl` orchestrates file reading, host filtering, and output formatting (plain text or JSON).

#### Key units
- **LogDigest** (module) – object with `new`, `add`, `summarize`, `top_n`; constants `MAX_LINES`, `DEFAULT_LEVEL`, `SLOW_REQUEST_MS`.
- **log_digest.pl** (script) – parses options, loads host aliases, builds exclusion set, feeds lines to `LogDigest`, outputs summary.

#### Data flow
1. CLI parses options → config file → exclusion hash.
2. For each log file: read line → extract host → skip if excluded → `$digest->add($line)`.
3. After all files: `$digest->summarize()` → `$summary`.
4. Output `$summary` as plain text or JSON.

#### Dependencies
- **Perl core**: `POSIX`, `List::Util`, `Carp`, `Exporter`, `FindBin`, `Getopt::Long`.
- **CPAN**: `JSON::PP`.
- **Internal**: `LogDigest` module.

#### Cross-cutting
- Host normalization (`_normalize_host`) ensures consistent counting.
- Error handling via `croak` and `die` for invalid levels, line limits, and file I/O.
- Configuration via environment variable `LOG_DIGEST_CONFIG` and optional host‑alias file.
- JSON output uses canonical ordering for reproducibility.
