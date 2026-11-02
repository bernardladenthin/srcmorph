### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:28:01Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Processes web-server access logs into normalized traffic totals, slow-request counts, and busiest-host reports.

#### Purpose
- Parse and aggregate access-log requests by host and status.
- Provide a command-line report with host exclusions, aliases, and text or JSON output.

#### Responsibilities
- **Parsing and aggregation:** `LogDigest` parses expected access-log lines, normalizes hosts, enforces a maximum line count, and tracks host, status, and slow-request counts.
- **Summary and ranking:** Build aggregate traffic statistics and rank count keys by descending count with lexical tie-breaking.
- **CLI and configuration:** `log_digest.pl` reads log files, loads host aliases, applies exclusions, and prints a text or canonical JSON report.

#### Key units
- `LogDigest` — object-oriented accumulator for parsed requests and traffic summaries.
- `parse_line` — extracts host, time, method, path, status, and elapsed milliseconds; returns false for nonmatching lines.
- `add` — records a parsed request and updates host, status, and slow-request counts.
- `summarize` — returns generation time, request and host totals, busiest-host count, slow count, and status counts.
- `top_n` — returns the highest-count keys, with lexical tie-breaking.
- `log_digest.pl::main` — parses options, reads configuration and logs, filters hosts, and emits the report.
- `read_aliases` — reads `from=to` mappings from the configured alias file.

#### Data flow
- `log_digest.pl` parses CLI options and reads aliases from `LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`.
- For each log line, it obtains the host, resolves aliases, skips excluded hosts, and passes remaining lines to `LogDigest::add`.
- `LogDigest` parses and normalizes each line, accumulates counts, then `summarize` supplies report totals; output is text with top hosts or canonical JSON.

#### Dependencies
- `log_digest.pl` uses `Getopt::Long`, `FindBin`, `JSON::PP`, and `LogDigest`.
- `LogDigest` uses `POSIX` for UTC timestamp formatting, `List::Util` for aggregate operations, and `Carp` for errors.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`, accepts configured levels `DEBUG`, `INFO`, `WARN`, and `ERROR`, and rejects unknown levels.
- Host normalization lowercases names and removes a trailing dot.
- `MAX_LINES` is `50_000`; exceeding it raises an error. Requests over `SLOW_REQUEST_MS` (`1500`) count as slow.
- Input-log open failures are fatal; failure to open the alias file produces an empty alias map.
