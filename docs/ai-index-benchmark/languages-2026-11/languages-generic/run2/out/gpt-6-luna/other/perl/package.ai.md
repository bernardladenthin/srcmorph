### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:33:25Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Converts web-server access logs into normalized host and HTTP-status traffic summaries, including slow-request totals and ranked busiest hosts.

#### Purpose
- Parse and aggregate valid access-log requests with host, status, and slow-request counts.
- Provide a command-line workflow to filter hosts, apply aliases, and emit text or JSON summaries.

#### Responsibilities
- **Parsing and aggregation:** `LogDigest` validates log lines, normalizes hostnames, enforces an input limit, and tracks host, status, and slow-request totals.
- **Summary and ranking:** Build timestamped aggregate results and rank count keys by descending count with lexical tie-breaking.
- **CLI and filtering:** `log_digest.pl` reads log files, applies configured host aliases and exclusions, and prints a summary with optional top-host results.

#### Key units
- `LogDigest` — Perl package for parsing access-log lines and maintaining request aggregates.
- `LogDigest::parse_line` — parses the expected log format and returns normalized request fields or no result.
- `LogDigest::add` — accumulates a valid request and updates counters.
- `LogDigest::summarize` — returns generation time, request total, distinct-host count, busiest-host count, slow-request count, and copied status counts.
- `LogDigest::top_n` — returns count keys ordered by descending count and ascending lexical tie-break.
- `log_digest.pl::main` — processes CLI options and log files, then emits text or canonical JSON output.
- `log_digest.pl::read_aliases` — reads `from=to` host mappings from the configured alias file.
- `log_digest.pl::usage` — formats command usage.

#### Data flow
- The CLI reads alias configuration and options, then reads each log line and excludes it if its aliased (or original) host is excluded.
- Remaining lines are passed to `LogDigest->add`; parsed requests update host, status, slow-request, and line counters.
- `summarize` produces aggregate results; JSON mode encodes that result, while text mode prints totals and ranked hosts via `top_n`.

#### Dependencies
- `log_digest.pl` uses `LogDigest` for parsing-related aggregation and summary/ranking, `Getopt::Long` for options, `JSON::PP` for JSON output, and `FindBin` for script-relative paths.
- `LogDigest` uses `POSIX::strftime` and `POSIX::gmtime` for UTC timestamps, `List::Util` for aggregate operations, and `Carp::croak` for errors.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`, accepts only configured `%LEVEL_RANK` values, limits input to `50_000` lines, and classifies requests as slow above `1500` ms.
- Host normalization lowercases names and removes one trailing dot; aliases and exclusions are applied in the CLI before aggregation.
- Malformed log lines are ignored; invalid levels, exceeding the line limit, option failures, invalid `--top`, and unreadable log files produce errors. Alias-config open failures fall back to no aliases.
