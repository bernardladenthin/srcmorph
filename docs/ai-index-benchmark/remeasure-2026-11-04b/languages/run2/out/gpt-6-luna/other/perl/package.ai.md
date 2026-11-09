### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:17:13Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Converts web-server access logs into host, HTTP-status, and slow-request statistics, with command-line reporting in text or JSON.

#### Purpose
- Parse and aggregate combined-style access-log entries into request, host, status, busiest-host, and slow-request counts.
- Provide a CLI for processing log files with configurable host aliases, exclusions, top-host limits, and output format.

#### Responsibilities
- **Parsing and aggregation:** Parse log lines, normalize hosts, enforce an input-line limit, and build summary statistics.
- **Log-file orchestration:** Read configured log files, apply aliases and host exclusions, and add eligible entries to the digest.
- **Reporting:** Emit aggregate counts and top hosts as text, or serialize the complete summary as canonical JSON.

#### Key units
- `LogDigest`: Object-oriented accumulator for host, status, request, and slow-request counts.
- `parse_line`: Parses a combined-style log line into host, time, method, path, status, and duration fields.
- `top_n`: Sorts count keys by descending count with alphabetical tie-breaking.
- `summarize`: Produces a timestamped aggregate with totals, distinct hosts, busiest host, slow requests, and status counts.
- `log_digest.pl::read_aliases`: Reads `from=to` host mappings from the configured alias file.
- `log_digest.pl::main`: Parses CLI options, processes log files, and prints the digest.

#### Data flow
- `log_digest.pl` reads aliases and log files, extracts each line’s first field for alias resolution and exclusion checks, then sends eligible lines to `LogDigest::add`.
- `LogDigest` parses and normalizes each line, updates counters for valid entries, and returns a summary; the script renders it as text or canonical JSON.
- `top_n` selects busiest hosts for text output; `--json` serializes the full summary.

#### Dependencies
- `log_digest.pl` uses `LogDigest`, `Getopt::Long`, `JSON::PP`, and `FindBin`.
- `LogDigest` uses `Exporter`, `POSIX::strftime`, `List::Util`, and `Carp`.
- Alias configuration comes from `LOG_DIGEST_CONFIG`, defaulting to `/etc/log-digest.conf`; unreadable or missing configuration yields no aliases.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`, accepts configured levels from `DEBUG`, `INFO`, `WARN`, and `ERROR`, and rejects unknown levels.
- Aggregation stops with an error after more than `50,000` lines; requests slower than `1,500` ms count as slow.
- Host normalization lowercases names and removes one trailing dot; CLI aliases and exclusions are applied before aggregation.
