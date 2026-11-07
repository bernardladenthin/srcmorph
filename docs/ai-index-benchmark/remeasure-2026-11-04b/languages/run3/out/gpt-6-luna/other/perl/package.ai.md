### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:20:33Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Converts web-server access logs into normalized host and HTTP-status traffic summaries, including busiest-host rankings and slow-request counts.

#### Purpose
- Parse and aggregate access-log requests into host, status, and slow-request metrics.
- Provide a CLI for filtering hosts, applying aliases, and displaying the summary as text or JSON.

#### Responsibilities
- **Parsing and aggregation:** Normalize hostnames, validate log entries, count requests by host and status, and track slow requests.
- **Ranking and reporting:** Rank hosts by request count with lexical tie-breaking; produce summary metrics and a UTC generation timestamp.
- **CLI input and filtering:** Read log files, map host aliases from configuration, exclude selected hosts, and control output format and top-host count.

#### Key units
- `LogDigest`: Object-oriented aggregator with `new`, `add`, and `summarize`; exports `parse_line` and `top_n`.
- `parse_line`: Extracts and normalizes host, timestamp, method, path, status, and duration from a log line.
- `top_n`: Returns up to *n* keys ranked by descending count, then ascending name.
- `log_digest.pl` `main`: Reads filtered log entries, builds a digest, and prints text or canonical JSON.
- `read_aliases`: Loads `from = to` host mappings from the configured alias file.

#### Data flow
- The CLI reads alias configuration and access-log files, maps each line’s host for exclusion checks, then passes non-excluded lines to `LogDigest->add`.
- `add` parses each line and updates host/status totals and the slow-request count; malformed lines are ignored.
- `summarize` produces aggregate metrics; the CLI emits canonical JSON or text with top hosts from `top_n`.

#### Dependencies
- `log_digest.pl` uses `LogDigest` and `JSON::PP`; it uses `Getopt::Long` for options and `FindBin` for script location.
- `LogDigest` uses `POSIX` for UTC timestamps, `List::Util` for aggregate calculations, `Carp` for errors, and `Exporter` for optional exports.

#### Cross-cutting
- Hostnames are lowercased and have one trailing dot removed; CLI aliases and exclusions provide additional host filtering.
- `LogDigest` enforces a 50,000-line limit, counts requests exceeding 1,500 ms as slow, and validates levels against `DEBUG`, `INFO`, `WARN`, and `ERROR`.
- Invalid log lines are skipped; invalid levels, excessive lines, invalid `--top`, option parsing failures, and unreadable log files raise errors.
