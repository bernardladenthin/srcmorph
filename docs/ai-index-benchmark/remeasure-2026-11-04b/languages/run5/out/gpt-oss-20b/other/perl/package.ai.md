### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:44:55Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Condenses web‑server access logs into per‑host and per‑status summaries, highlighting slow requests and providing a CLI for reporting.

#### Purpose
- Summarize HTTP access logs by host, status, and latency.
- Offer a command‑line utility to generate human‑readable or JSON reports.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` module parses individual log lines, tracks counts per host and status, and flags slow requests.
- **Reporting** – `log_digest.pl` reads log files, applies host exclusions/aliases, and outputs a concise summary or JSON.

#### Key units
- `LogDigest` (module) – object with `new`, `add`, `summarize`, `top_n`; exports `parse_line`.
- `log_digest.pl` (script) – orchestrates file reading, option parsing, and output formatting.

#### Data flow
1. `log_digest.pl` parses CLI options and loads host alias config.
2. For each log file, it reads lines, skips excluded hosts, and feeds each line to `LogDigest->add`.
3. After all input, `LogDigest->summarize` produces a hash of totals, host counts, slow request count, and status tallies.
4. The script formats this hash either as plain text or JSON.

#### Dependencies
- **Internal**: `LogDigest` module, its exported `parse_line`.
- **External**: `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, `Carp`, `Exporter`.

#### Cross-cutting
- Host normalization (lowercase, strip trailing dot) in `LogDigest`.
- Configurable log level with rank mapping (`DEBUG`, `INFO`, `WARN`, `ERROR`).
- Constant limits: `MAX_LINES` (50 000) and `SLOW_REQUEST_MS` (1500 ms).
- Error handling via `croak` for invalid levels or exceeding line limits; `die` for CLI misuse or file I/O failures.
