### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:35:24Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates web‑server access logs into per‑host and per‑status statistics, then exposes a CLI for reporting the busiest hosts and slow request counts.

#### Purpose
- Parse and summarize HTTP access logs.
- Provide a command‑line interface for reporting host activity and status‑code distribution.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` module reads individual log lines, normalizes hosts, counts requests, tracks slow requests, and tallies status codes.
- **Reporting** – `log_digest.pl` consumes `LogDigest`, applies host exclusions, and outputs a concise summary in text or JSON.

#### Key units
- `LogDigest` (module) – OO class with `new`, `add`, `summarize`, `top_n`; constants `MAX_LINES`, `SLOW_REQUEST_MS`.
- `log_digest.pl` (script) – CLI entry point; options `--top`, `--level`, `--json`, `--exclude-host`; helper `read_aliases`.

#### Data flow
1. CLI parses options and reads optional host‑alias config.
2. For each log file, each line is passed to `LogDigest->add` unless the host (or alias) is excluded.
3. After all lines, `LogDigest->summarize` produces a hash of totals, host counts, slow request count, and status counts.
4. CLI formats the summary: plain text header + top‑N hosts, or JSON string.

#### Dependencies
- **Internal**: `LogDigest` module (provides `top_n`, `new`, `add`, `summarize`).
- **External**: `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, `Carp`.

#### Cross‑cutting
- Host normalization (`_normalize_host`) ensures consistent counting.
- Error handling via `Carp::croak` for invalid levels or line limits.
- Configurable log level and maximum line count (`MAX_LINES`).
- JSON output uses canonical ordering for reproducibility.
