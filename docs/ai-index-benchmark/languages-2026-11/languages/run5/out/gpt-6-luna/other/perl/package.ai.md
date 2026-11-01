### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:34:50Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Converts web-server access logs into request, host, status, and slow-request statistics, with command-line reporting and alias-aware host exclusions.

#### Purpose
- Parse and aggregate access-log requests into a digest of totals, per-host and per-status counts, slow requests, and busiest hosts.
- Provide text or canonical JSON reporting for one or more log files.

#### Responsibilities
- **Parsing and aggregation (`LogDigest`)** — validate log records, normalize host names, count requests by host and status, enforce input limits, and identify slow requests.
- **Command-line reporting (`log_digest.pl`)** — parse options, load host aliases, apply host exclusions, process log files, and print a digest.
- **Ranking and summaries** — rank hosts by descending request count with lexical tie-breaking; report generation time and aggregate statistics.

#### Key units
- `LogDigest` — procedural Perl module exposing object methods for aggregation and summary generation.
- `LogDigest::new` — validates the configured level and initializes counters.
- `LogDigest::parse_line` — extracts host, timestamp, method, path, status, and duration from a log record.
- `LogDigest::add` — parses a line and updates host/status totals and slow-request count.
- `LogDigest::summarize` — returns generated time, request and host totals, busiest-host data, slow count, and status counts.
- `LogDigest::top_n` — selects highest-count keys, breaking ties lexically.
- `log_digest.pl::main` — processes options and input files, then emits text or JSON output.
- `log_digest.pl::read_aliases` — reads host-alias mappings from the configured file.

#### Data flow
- `log_digest.pl` reads options, loads aliases, and builds the exclusion set.
- For each log line, it resolves the first-field host for exclusion checking; non-excluded lines are passed to `LogDigest->add`.
- `LogDigest` parses and aggregates accepted lines; the script calls `summarize` and emits canonical JSON or a text summary with ranked hosts.

#### Dependencies
- `LogDigest` uses Perl `POSIX` (`strftime`), `List::Util` (`max`, `sum`), and `Carp` (`croak`).
- `log_digest.pl` uses `Getopt::Long`, `JSON::PP`, `FindBin`, and `LogDigest`.
- The script reads log files and a host-alias configuration path from `LOG_DIGEST_CONFIG`, defaulting to `/etc/log-digest.conf`.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`, limits processing to `50_000` lines, and counts requests slower than `1500` ms.
- Host normalization lowercases names and removes one trailing dot; malformed records are rejected.
- Invalid levels and exceeded line limits croak; unreadable log files are fatal, while unreadable alias configuration yields no aliases.
- The module’s `level` setting is validated against `DEBUG`, `INFO`, `WARN`, and `ERROR`.
