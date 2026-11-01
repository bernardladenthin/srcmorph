### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:28:45Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates web-server access logs into host and HTTP-status statistics, identifies slow requests and busiest hosts, and provides text or JSON reports with configurable host filtering.

#### Purpose
- Parse access-log records and calculate request, host, status, and slow-request totals.
- Provide a command-line reporting workflow with host exclusions, aliases, and optional JSON output.

#### Responsibilities
- **Parsing and aggregation:** Match log lines, normalize host names, and maintain request counts by host and HTTP status plus slow-request totals.
- **Ranking and summaries:** Calculate aggregate statistics and order hosts by descending request count, breaking ties lexically.
- **CLI reporting and configuration:** Read log files, load optional host aliases, apply exclusions, validate options, and print text or canonical JSON output.

#### Key units
- `LogDigest` — validates configuration and accumulates access-log statistics.
- `LogDigest::parse_line` — extracts host, timestamp, method, path, status, and duration from a matching line.
- `LogDigest::add` — records a parsed request, subject to the `MAX_LINES` limit.
- `LogDigest::summarize` — returns timestamped totals, host and slow-request counts, busiest-host count, and status counts.
- `LogDigest::top_n` — ranks count keys with lexical tie-breaking.
- `log_digest.pl::main` — processes CLI options and log files, then emits a report.
- `log_digest.pl::read_aliases` — loads `host=alias` entries from configuration.

#### Data flow
- `log_digest.pl` reads alias configuration and CLI options, then processes each input file line by line.
- The script derives a host for alias resolution and exclusion checks; lines not excluded are passed to `LogDigest->add`.
- `LogDigest` parses and normalizes accepted lines, updates counts, and produces a summary; the CLI prints that summary as text or canonical JSON, with text mode also ranking hosts through `top_n`.

#### Dependencies
- `log_digest.pl` uses `Getopt::Long` for options, `JSON::PP` for JSON output, `FindBin` for script location, and `LogDigest` for aggregation and ranking.
- `LogDigest` uses `POSIX` for UTC timestamps, `List::Util` for aggregate calculations, and `Carp::croak` for errors.
- Alias configuration comes from `LOG_DIGEST_CONFIG`, defaulting to `/etc/log-digest.conf`.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`; accepted levels are `DEBUG`, `INFO`, `WARN`, and `ERROR`. The level is validated and stored.
- Slow requests are durations strictly greater than `SLOW_REQUEST_MS` (1500 ms); `add` rejects input beyond `MAX_LINES` (50,000).
- Invalid levels, excess lines, invalid top-host counts, and unreadable input logs produce errors; unreadable alias configuration is treated as empty.
