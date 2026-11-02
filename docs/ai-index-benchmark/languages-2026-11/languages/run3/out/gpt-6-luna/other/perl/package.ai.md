### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:54:39Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Parses web-server access logs into request, host, status, and slow-request statistics, with configurable host exclusions and text or JSON reporting.

#### Purpose
- Provide reusable access-log parsing and aggregation plus a command-line tool for processing log files.
- Report request totals, distinct hosts, busiest hosts, slow requests, and status counts.

#### Responsibilities
- **Parsing and aggregation:** Match log entries, normalize hostnames, enforce the line limit, and maintain host and status counts.
- **Reporting and filtering:** Read log files and host aliases, exclude selected hosts, rank busiest hosts, and emit text or JSON summaries.
- **Configuration:** Support CLI options for top-host count, log level, output format, exclusions, and alias-file location.

#### Key units
- `LogDigest` — constructs a digest, parses and adds log entries, and returns aggregate statistics.
- `parse_line` — extracts host, timestamp, method, path, status, and elapsed milliseconds from an access-log line.
- `top_n` — ranks count keys by descending count with lexical tie-breaking.
- `log_digest.pl::read_aliases` — loads host alias mappings, using an empty map if the config cannot be opened.
- `log_digest.pl::main` — processes configured inputs and prints the digest as text or canonical JSON.

#### Data flow
- `log_digest.pl` reads options, aliases, and log files; it skips entries whose aliased or original host is excluded.
- Remaining lines are passed to `LogDigest::add`, which parses entries and updates counters; unmatched lines are ignored.
- `summarize` produces aggregate statistics; output is either canonical JSON or text with totals and ranked hosts from `top_n`.

#### Dependencies
- `log_digest.pl` uses `LogDigest` for digest construction and aggregation, and imports `top_n` for host ranking.
- `LogDigest` uses `POSIX::strftime` for UTC generation timestamps, `List::Util` for `max` and `sum`, and `Carp::croak` for errors.
- The CLI uses `Getopt::Long` for options, `JSON::PP` for JSON output, and `%ENV` key `LOG_DIGEST_CONFIG` for the alias-config path.

#### Cross-cutting
- `LogDigest` limits input to `MAX_LINES` (50,000), counts requests over `SLOW_REQUEST_MS` (1,500 ms), and validates levels against `DEBUG`, `INFO`, `WARN`, and `ERROR`.
- Hostnames are lowercased and stripped of one trailing period; top-host ranking uses lexical order to break count ties.
- Invalid levels, excess lines, invalid CLI options, nonpositive `--top`, and unreadable log files produce errors; an unreadable alias config is silently treated as empty.
