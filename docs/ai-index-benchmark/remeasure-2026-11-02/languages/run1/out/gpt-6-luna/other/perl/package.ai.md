### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:24:30Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Parses access logs and reports request volume, host and status counts, slow requests, and busiest hosts, with configurable filtering and text or JSON output.

#### Purpose
- Aggregate matching web-server access-log entries into normalized host and HTTP-status statistics.
- Provide a command-line report with host exclusions, alias-aware filtering, and text or canonical JSON output.

#### Responsibilities
- **Parsing and aggregation:** `LogDigest` parses entries, normalizes hostnames, enforces a line limit, and tracks request, host, status, and slow-request counts.
- **Ranking and summaries:** Build aggregate statistics and rank count keys by descending count with lexical tie-breaking.
- **Command-line reporting:** `log_digest.pl` parses options, reads aliases and log files, filters excluded hosts, and prints the summary.

#### Key units
- `LogDigest` — module exposing `new`, `parse_line`, `add`, `summarize`, and `top_n`.
- `LogDigest::parse_line` — extracts normalized host, timestamp, method, path, status, and duration from a matching access-log line.
- `LogDigest::add` — accepts valid entries and updates counters, rejecting input beyond `MAX_LINES` with an error.
- `LogDigest::summarize` — returns totals, distinct-host and busiest-host statistics, slow-request count, copied status counts, and a UTC generation timestamp.
- `LogDigest::top_n` — returns up to the requested number of keys ordered by count and lexical tie-break.
- `log_digest.pl::main` — loads configuration and logs, applies exclusion filtering, and emits a text or JSON report.
- `log_digest.pl::read_aliases` — reads `host = alias` mappings, returning an empty mapping if the config cannot be opened.

#### Data flow
- `log_digest.pl` reads `LOG_DIGEST_CONFIG` (default `/etc/log-digest.conf`) and access-log files; aliases are used when checking hosts against exclusions.
- Non-excluded lines are passed to a `LogDigest` object, which parses entries and updates host, status, request, and slow-request counts.
- The script obtains `summarize` output and reports it as canonical JSON or as text with the top host counts.

#### Dependencies
- `log_digest.pl` uses `LogDigest`, `Getopt::Long`, `JSON::PP`, and `FindBin`.
- `LogDigest` uses `POSIX` for UTC timestamp formatting, `List::Util` for aggregation, `Carp` for errors, and `Exporter` for optional exports.

#### Cross-cutting
- `LogDigest` limits input to `MAX_LINES` (50,000), counts requests slower than `SLOW_REQUEST_MS` (1,500 ms), and defaults to log level `WARN`.
- Valid levels are `DEBUG`, `INFO`, `WARN`, and `ERROR`; an unknown level causes `new` to croak.
- Host normalization lowercases names and removes one trailing dot.
- CLI defaults include `--top 10`, `--level WARN`, and text output; invalid top counts and unreadable log files cause errors.
