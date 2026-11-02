### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:40:56Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Converts web-server access logs into host and status traffic summaries, with configurable aliases, exclusions, rankings, and JSON output.

#### Purpose
- Parse access-log requests and aggregate counts by host and HTTP status, including slow requests.
- Provide a command-line tool to process log files and report summaries as text or canonical JSON.

#### Responsibilities
- **Log parsing and aggregation:** `LogDigest` parses entries, normalizes host names, enforces a line limit, and tracks host, status, and slow-request counts.
- **Ranking and summary generation:** `LogDigest` produces timestamped aggregate statistics and ranks count keys by descending count, with lexical tie-breaking.
- **CLI and input configuration:** `log_digest.pl` handles options, loads host aliases, applies host exclusions, reads log files, and prints text or JSON results.

#### Key units
- `LogDigest` — Perl module exposing `new`, `add`, `parse_line`, `summarize`, and exported `top_n`.
- `log_digest.pl` — command-line entry point with `main`, `read_aliases`, and `usage`.

#### Data flow
- `log_digest.pl` reads alias configuration and access-log files, resolves each line’s host alias, skips excluded hosts, and passes remaining lines to `LogDigest`.
- `LogDigest` parses and normalizes matching entries, updates counters, and creates a timestamped summary; the script renders that summary as canonical JSON or text with top hosts.

#### Dependencies
- **Internal:** `log_digest.pl` uses `LogDigest` for parsing, aggregation, and host ranking.
- **Perl modules:** `POSIX` for timestamps; `List::Util` for aggregate operations; `Carp` for validation errors; `Getopt::Long` for CLI options; `JSON::PP` for JSON output; `FindBin` and `lib` for module lookup.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`, accepts only levels in `%LEVEL_RANK`, counts requests slower than `1500` ms, and rejects more than `50,000` input lines.
- Hosts are lowercased and have one trailing dot removed by the module; CLI exclusions and alias resolution are applied before entries reach the digest.
- Invalid levels, excess lines, option errors, invalid top counts, and unreadable log files raise errors; missing or unreadable alias configuration silently yields no aliases.
