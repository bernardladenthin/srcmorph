### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:33:43Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates web-server access logs into host, HTTP-status, and slow-request statistics, with aliasing, exclusions, and text or JSON reports.

#### Purpose
- Parse and count access-log requests, producing summary statistics and ranked hosts.
- Provide a command-line tool for filtering, summarizing, and reporting logs.

#### Responsibilities
- **Parsing and aggregation:** Match log lines, normalize host names, count requests by host and status, and track slow requests.
- **Filtering and reporting:** Read alias and exclusion settings, process log files, and print totals and top hosts as text or canonical JSON.

#### Key units
- `LogDigest` — initializes counters, parses entries, aggregates statistics, and produces summaries.
- `LogDigest::parse_line` — extracts host, time, method, path, status, and milliseconds from a matching log line.
- `LogDigest::add` — updates counters for valid entries, subject to the `MAX_LINES` limit.
- `LogDigest::summarize` — returns generated time, request total, host count, busiest-host count, slow-request count, and status counts.
- `LogDigest::top_n` — ranks count keys by descending count, then ascending key.
- `log_digest.pl::read_aliases` — reads host mappings from the configured alias file.
- `log_digest.pl::main` — applies exclusions and aliases, aggregates input files, and emits the selected report format.

#### Data flow
- `log_digest.pl` reads alias configuration and access-log files; it skips entries whose aliased or original first-field host is excluded.
- Remaining lines pass to `LogDigest->add`, which parses entries and updates host, status, request, and slow-request counters.
- The script obtains a summary and uses `top_n` to render ranked hosts, outputting either canonical JSON or text totals and host entries.

#### Dependencies
- `log_digest.pl` uses `Getopt::Long` for CLI options, `JSON::PP` for JSON output, and `LogDigest` for aggregation.
- `LogDigest` uses `POSIX::strftime`, `List::Util` (`max`, `sum`), and `Carp::croak`.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`, validates levels against `DEBUG`, `INFO`, `WARN`, and `ERROR`, and caps processing at `MAX_LINES` (50,000).
- Requests exceeding `SLOW_REQUEST_MS` (1,500 ms) count as slow; hosts are lowercased and have one trailing dot removed.
- Invalid configured levels, excess lines, invalid CLI options, nonpositive `--top`, and unreadable log files trigger errors; an unreadable alias file is treated as empty.
