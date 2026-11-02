### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:11:30Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Processes web-server access logs into host and status aggregates, identifying busiest hosts and slow requests for text or JSON reporting.

#### Purpose
- Parse and summarize accepted access-log requests with per-host, per-status, and slow-request counts.
- Provide a command-line tool to process log files with host aliases and exclusions.

#### Responsibilities
- **Parsing and aggregation (`LogDigest`)** — parse expected log records, normalize hosts, enforce the line limit, and calculate request summaries.
- **Command-line reporting (`log_digest.pl`)** — read options, aliases, exclusions, and log files; emit text summaries or canonical JSON.

#### Key units
- `LogDigest` — object-oriented accumulator for access-log entries and summary statistics.
- `parse_line` — extract host, timestamp, method, path, status, and elapsed milliseconds from a log line.
- `add` — accept valid parsed entries and update counts, subject to `MAX_LINES`.
- `summarize` — return totals, distinct hosts, busiest-host count, slow-request count, status totals, and generation time.
- `top_n` — rank count-map keys by descending count, then alphabetically.
- `log_digest.pl::read_aliases` — load host alias mappings, returning an empty map if the config cannot be opened.
- `log_digest.pl::main` — process configured log files and print the selected report.

#### Data flow
- The script reads CLI options and alias configuration, then reads each log line and resolves its first field through the alias map.
- Excluded hosts are skipped; remaining lines are passed to `LogDigest::add`.
- `summarize` supplies aggregate data for canonical JSON output or text output with ranked hosts from `top_n`.

#### Dependencies
- `log_digest.pl` uses `Getopt::Long`, `FindBin`, `JSON::PP`, and `LogDigest`.
- `LogDigest` uses `POSIX` (`strftime`), `List::Util` (`max`, `sum`), and `Carp` (`croak`).
- Configuration comes from `LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`; input log paths come from remaining command-line arguments.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`, allows `DEBUG`, `INFO`, `WARN`, and `ERROR`, and rejects unknown levels.
- Requests slower than `SLOW_REQUEST_MS` (1500 ms) are counted; processing is capped at `MAX_LINES` (50,000).
- Hosts are lowercased and have one trailing dot removed; aliases and exclusions are applied by the CLI.
