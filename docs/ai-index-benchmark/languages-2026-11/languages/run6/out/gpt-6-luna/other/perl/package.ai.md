### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:38:22Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Converts web-server access logs into host- and status-based request statistics, highlighting busiest hosts and slow requests.

#### Purpose
- Parse and aggregate access-log entries into a digest.
- Provide a command-line tool to filter, configure, and display the digest as text or JSON.

#### Responsibilities
- **Parsing and aggregation:** `LogDigest` parses and normalizes entries, tracks host and status counts, and counts slow requests.
- **Ranking and summaries:** Build aggregate statistics and rank hosts by request count with lexical tie-breaking.
- **Command-line processing:** `log_digest.pl` reads log files, applies configured host aliases and exclusions, and emits digest output.

#### Key units
- `LogDigest` — procedural module with `new`, `add`, and `summarize` methods for access-log digesting.
- `parse_line` — returns normalized host, timestamp, method, path, status, and elapsed milliseconds, or `undef` for malformed input.
- `top_n` — returns the requested number of keys ranked by descending count, then ascending name.
- `log_digest.pl` — CLI supporting top-host count, log level, host exclusions, and text or JSON output.
- `read_aliases` — reads `from = to` host mappings from the configured file.

#### Data flow
- The CLI reads alias configuration and log files, extracts each line’s first field for exclusion checks, and passes non-excluded lines to `LogDigest`.
- `LogDigest` parses entries, enforces its line limit, updates host/status/slow-request counts, and produces a summary.
- The CLI prints the summary as canonical JSON or as text counts followed by ranked hosts.

#### Dependencies
- `log_digest.pl` uses `LogDigest` for aggregation and `top_n` for host ranking.
- `Getopt::Long` parses CLI options; `JSON::PP` encodes JSON output; `FindBin` and `lib` make the script directory available for module lookup.
- `LogDigest` uses `POSIX` for UTC timestamps, `Carp` for validation errors, and `List::Util` for `max` and `sum`.

#### Cross-cutting
- `LogDigest` normalizes hosts to lowercase and removes one trailing dot.
- Digest limits and defaults: `MAX_LINES` is 50000, `SLOW_REQUEST_MS` is 1500, and the default level is `WARN`.
- Invalid levels and excess input lines croak; malformed entries are ignored. CLI option errors, non-positive `--top`, and unreadable input logs terminate with errors.
