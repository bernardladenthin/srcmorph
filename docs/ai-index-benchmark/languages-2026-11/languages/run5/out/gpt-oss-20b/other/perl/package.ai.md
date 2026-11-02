### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:36:19Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> A Perl toolkit that parses web‑server access logs, aggregates per‑host and per‑status statistics, and outputs the results in plain text or JSON.

#### Purpose
- Aggregate HTTP access log data into host‑ and status‑code summaries.
- Provide a reusable module (`LogDigest`) and a command‑line utility (`log_digest.pl`) for quick analysis.

#### Responsibilities
- **Parsing & aggregation** – `LogDigest` reads individual log lines, normalizes hosts, counts requests, tracks slow requests, and tallies status codes.
- **Reporting** – `log_digest.pl` consumes a `LogDigest` instance, applies host exclusions and aliases, and prints a concise summary or JSON payload.
- **Configuration** – `log_digest.pl` loads host‑alias mappings from a simple `from = to` file and respects command‑line options.

#### Key units
- **`LogDigest` module** – OO class exposing `new`, `add`, `summarize`, and helper `top_n`; constants `MAX_LINES`, `SLOW_REQUEST_MS`, and log‑level mapping.
- **`log_digest.pl` script** – CLI driver that parses options, reads log files, filters hosts, feeds lines to `LogDigest`, and formats output.

#### Data flow
1. **CLI** parses options → creates `LogDigest` (`level`).
2. For each input file: read line → optional host exclusion → `$digest->add($line)`.
3. After all files: `$digest->summarize` → `$summary`.
4. Output: JSON (`JSON::PP`) or plain text (top hosts via `top_n`).

#### Dependencies
- **Perl core**: `POSIX`, `List::Util`, `Carp`.
- **CPAN**: `Getopt::Long`, `JSON::PP`, `FindBin`.
- **Internal**: `LogDigest` module used by the script.

#### Cross-cutting
- **Error handling**: `Carp::croak` for invalid levels or line limits; script dies on bad options or file I/O errors.
- **Logging levels**: `DEBUG`, `INFO`, `WARN`, `ERROR` with rank mapping; default `WARN`.
- **Host normalization**: lowercasing and stripping trailing dots; alias mapping via config file.
- **Constants**: `MAX_LINES = 50000`, `SLOW_REQUEST_MS = 1500`, default `top = 10`, default `level = 'WARN'`.
