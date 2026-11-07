### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:00:03Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> A Perl toolkit that parses web‑server access logs, aggregates per‑host and per‑status statistics, and reports the busiest hosts and slow requests, optionally as JSON.

#### Purpose
- Aggregate and summarize web‑server access logs.
- Identify busiest hosts and slow requests.
- Provide a command‑line utility for quick reporting.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` module parses individual log lines, normalizes hosts, counts requests, tracks slow requests, and tallies status codes.
- **Reporting** – `log_digest.pl` script consumes `LogDigest`, applies host exclusions and aliases, and outputs a concise summary or JSON.

#### Key units
- **`LogDigest`** – OO class; methods: `new`, `add`, `summarize`, `top_n`; exports `parse_line`.
- **`log_digest.pl`** – procedural script; functions: `usage`, `read_aliases`, `main`.

#### Data flow
1. **Input** – log files supplied on the command line.
2. **Alias & exclusion** – `log_digest.pl` reads `/etc/log-digest.conf` (or env override) for host aliases and builds an exclusion set.
3. **Processing** – each line is passed to `LogDigest->add`; the module updates host, status, and slow‑request counters.
4. **Summarization** – after all files, `summarize` produces totals, busiest host, and status counts.
5. **Output** – script prints a plain‑text line and top‑N hosts, or JSON if `--json` is set.

#### Dependencies
- **Perl core** – `POSIX`, `List::Util`, `Carp`, `Exporter`, `FindBin`, `Getopt::Long`.
- **CPAN** – `JSON::PP`.
- **Internal** – `LogDigest` module used by the script.

#### Cross-cutting
- **Host normalization** – lowercasing and stripping trailing dots (`_normalize_host`).
- **Error handling** – `croak` for invalid levels or exceeding `MAX_LINES`; script dies on file read errors.
- **Configuration** – host alias file (`/etc/log-digest.conf`), command‑line options (`--top`, `--level`, `--json`, `--exclude-host`).
- **Constants** – `MAX_LINES=50000`, `SLOW_REQUEST_MS=1500`, log levels mapping.
