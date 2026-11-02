### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:16:32Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates web-server access logs into normalized host and HTTP-status statistics, highlighting busiest hosts and slow requests.

#### Purpose
- Parse and count valid access-log requests, producing a timestamped digest of request volume, host distribution, status counts, and slow requests.
- Provide a command-line workflow for processing log files with host aliases, exclusions, and text or JSON output.

#### Responsibilities
- **Parsing and aggregation:** `LogDigest` parses and normalizes log entries, tracks host/status counts, and computes totals, busiest-host count, and slow-request count.
- **Command-line processing and reporting:** `log_digest.pl` handles options and input files, applies configured aliases and host exclusions, then prints the digest.

#### Key units
- `LogDigest` — procedural module with a `new`-created object for accumulating access-log statistics.
- `parse_line` — extracts host, time, method, path, status, and milliseconds from a matching log line; normalizes the host.
- `add` — accepts a log line and updates counts when parsing succeeds.
- `summarize` — returns aggregate statistics, copied status counts, and a UTC generation timestamp.
- `top_n` — orders count keys by descending count, then lexical key.
- `log_digest.pl::read_aliases` — reads host mappings from the configured alias file.
- `log_digest.pl::main` — processes options and log files, filters excluded hosts, and emits text or canonical JSON.
- `log_digest.pl::usage` — formats command usage and the configuration hint.

#### Data flow
- The script reads aliases from `LOG_DIGEST_CONFIG` (default `/etc/log-digest.conf`) and access-log paths from `@ARGV`; CLI options select top-host count, level, output format, and excluded hosts.
- For each log line, it checks the first space-delimited field against exclusions using its alias when available, otherwise the original host; remaining lines are passed to `LogDigest::add`.
- `LogDigest` parses and normalizes valid entries, updates counters, and supplies the summary; the script prints canonical JSON or text totals and top hosts.

#### Dependencies
- `log_digest.pl` uses `LogDigest`, `Getopt::Long`, `JSON::PP`, and `FindBin`; it adds `$Bin` to the module search path.
- `LogDigest` uses `POSIX` for UTC timestamp formatting, `List::Util` for aggregate calculations, `Carp` for errors, and `Exporter` for optional `parse_line` and `top_n` exports.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`, accepts levels ranked `DEBUG`, `INFO`, `WARN`, `ERROR`, and rejects unknown levels.
- Aggregation is capped at `MAX_LINES = 50_000`; requests count as slow only when latency exceeds `SLOW_REQUEST_MS = 1500`.
- Host normalization lowercases names and removes one trailing dot; `top_n` uses lexical tie-breaking for equal counts.
- Invalid CLI options, nonpositive `--top`, unknown levels, excess input lines, and unreadable log files produce errors; an unreadable alias file is ignored.
