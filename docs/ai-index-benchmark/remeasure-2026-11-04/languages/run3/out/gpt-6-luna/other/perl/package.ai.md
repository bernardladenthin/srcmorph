### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:04:52Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates web-server access logs into host and status metrics, identifies slow requests, and reports request summaries.

#### Purpose
- Parse access-log records and calculate request counts by host and status, plus slow-request totals.
- Provide a command-line tool for filtering log inputs and emitting text or canonical JSON summaries.

#### Responsibilities
- **Parsing and aggregation:** `LogDigest` normalizes parsed hosts, counts accepted requests by host and status, tracks slow requests, and computes summary metrics.
- **Ranking:** `top_n` orders count keys by descending count with lexical tie-breaking.
- **Command-line reporting:** `log_digest.pl` reads log files, applies host-based exclusions, and prints the summary and top hosts.
- **Configuration:** `read_aliases` loads host alias mappings from the configured file; an unavailable config file yields no aliases.

#### Key units
- `LogDigest` — object API for validating log level, accepting log lines, and producing aggregate statistics.
- `parse_line` — parses an access-log line into normalized host, time, method, path, status, and milliseconds, or returns false if unmatched.
- `add` — counts a parsed request, subject to `MAX_LINES`.
- `summarize` — returns UTC generation time, request total, distinct-host count, busiest-host count, slow count, and status counts.
- `top_n` — selects the highest-count keys with lexical tie-breaking.
- `log_digest.pl::main` — parses options, reads files, summarizes records, and writes text or JSON output.
- `read_aliases` — reads host mappings from the configured file.

#### Data flow
- `log_digest.pl` parses CLI options and loads aliases, then reads each specified log file line by line.
- For each line, it checks the first field against exclusions using the alias when present; non-excluded lines are passed to `LogDigest::add`.
- `LogDigest` parses and normalizes accepted entries, updates host/status/slow counters, and produces a summary.
- The script emits the summary as canonical JSON or prints request totals and the top hosts using `top_n`.

#### Dependencies
- `log_digest.pl` uses `LogDigest`, `Getopt::Long`, `JSON::PP`, and `FindBin`.
- `LogDigest` uses `POSIX` for UTC timestamp formatting, `List::Util` for aggregate calculations, `Carp` for errors, and `Exporter` for optional exports.
- Configuration is read from `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`; log inputs are file paths supplied via `@ARGV`.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`, accepts only levels in `%LEVEL_RANK`, limits processing to `MAX_LINES` (50,000), and marks requests over 1,500 ms as slow.
- Invalid levels, exceeding the line limit, invalid CLI options, nonpositive `--top`, and unreadable log files raise errors; failure to open the alias config is ignored.
- Text output is the default; `--json` selects canonical JSON output.
