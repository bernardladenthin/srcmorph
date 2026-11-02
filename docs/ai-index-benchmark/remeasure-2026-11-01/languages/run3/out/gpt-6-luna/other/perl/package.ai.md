### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:56:38Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates web-server access logs into host, status-code, and slow-request statistics, with configurable filtering and text or JSON reporting.

#### Purpose
- Parse and count valid access-log requests, then produce a timestamped digest.
- Provide a command-line workflow for processing log files with host exclusions and optional JSON output.

#### Responsibilities
- **Log parsing and aggregation:** Extract request fields, normalize hosts, count requests by host and status, and track slow requests.
- **Command-line reporting:** Read options, aliases, exclusions, and log files; emit summary statistics and busiest hosts.
- **Ranking:** Order hosts by descending count, breaking ties lexicographically.

#### Key units
- `LogDigest` — validates logging level and accumulates parsed request statistics.
- `LogDigest::parse_line` — parses a log entry into fields or returns `undef` for a non-matching line.
- `LogDigest::add` — counts a valid entry, subject to the `MAX_LINES` limit.
- `LogDigest::summarize` — returns generated timestamp, totals, host and status counts, busiest-host count, and slow-request count.
- `LogDigest::top_n` — returns the highest-ranked keys from a count hash.
- `log_digest.pl::main` — processes CLI options and log files, applies exclusions, and prints text or canonical JSON.
- `log_digest.pl::read_aliases` — reads alias mappings from the configured file; returns no mappings if it cannot be opened.
- `log_digest.pl::usage` — supplies command-line usage text.

#### Data flow
- `log_digest.pl` reads options and alias configuration, then processes each supplied log file line by line.
- It resolves each line’s first-field host through the alias map for exclusion checks; non-excluded lines are passed to `LogDigest->add`.
- `LogDigest` parses and counts valid requests; `summarize` supplies aggregate results for text or JSON output.

#### Dependencies
- `log_digest.pl` uses `Getopt::Long`, `JSON::PP`, and `LogDigest`.
- `LogDigest` uses `POSIX` for UTC timestamps, `List::Util` for aggregate operations, `Carp` for validation errors, and `Exporter` for optional exports.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`, accepts levels `DEBUG`, `INFO`, `WARN`, and `ERROR`, caps input at `50_000` lines, and classifies requests over `1500` ms as slow.
- Host normalization lowercases names and removes one trailing dot.
- Invalid levels, excess lines, invalid `--top` values, and unreadable log files produce errors; an unreadable alias configuration is treated as empty.
