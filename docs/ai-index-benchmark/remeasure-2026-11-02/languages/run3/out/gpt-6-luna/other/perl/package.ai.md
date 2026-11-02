### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:55:27Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Converts web-server access logs into host- and status-based request summaries, with slow-request counts and ranked busiest hosts.

#### Purpose
- Parse and aggregate access-log requests, then report summary metrics in text or canonical JSON.
- Allow command-line selection of output format, host exclusions, ranking size, and logging level.

#### Responsibilities
- **Parsing and aggregation:** `LogDigest` normalizes hosts, counts valid requests by host and status, tracks slow requests, and enforces a line limit.
- **Input selection and reporting:** `log_digest.pl` reads aliases and log files, excludes selected hosts, and prints summary metrics or JSON.
- **Ranking:** `top_n` orders count keys by descending count, breaking ties lexically.

#### Key units
- `LogDigest` — object-oriented accumulator for parsed request counts and summary metrics.
- `parse_line` — extracts host, timestamp, method, path, status, and duration from a log line.
- `add` — accepts a valid parsed record and updates host, status, and slow-request counts.
- `summarize` — returns a hash with generation time, total requests, distinct hosts, busiest-host count, slow count, and status counts.
- `top_n` — returns the requested prefix of count keys in ranked order.
- `log_digest.pl::read_aliases` — reads `host = alias` mappings from the configured file.
- `log_digest.pl::main` — processes options and log files, applies host exclusions, and emits the summary.

#### Data flow
- `log_digest.pl` reads alias configuration and access-log files; it skips records whose mapped host is excluded.
- Remaining lines go to `LogDigest->add`, which parses and aggregates valid records; malformed records are not counted.
- `summarize` produces aggregate metrics; the script emits canonical JSON or text with counts and ranked hosts.

#### Dependencies
- `log_digest.pl` uses `LogDigest`, `Getopt::Long`, `JSON::PP`, and `FindBin`.
- `LogDigest` uses `POSIX` for UTC timestamps, `List::Util` for aggregation, `Carp` for errors, and `Exporter` for optional exports.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`, accepts only configured levels, and caps input at `MAX_LINES` (50,000); durations above `SLOW_REQUEST_MS` (1,500 ms) count as slow.
- Host normalization lowercases names and removes one trailing dot; malformed records return false rather than raising an exception.
- Invalid levels, excess lines, invalid options, nonpositive `--top`, and unreadable log files raise errors; an unreadable alias configuration yields an empty mapping.
