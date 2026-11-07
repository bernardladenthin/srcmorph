### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:13:44Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates web-server access logs into host and status statistics, with configurable host filtering, busiest-host rankings, and slow-request reporting.

#### Purpose
- Parse and summarize access-log requests by normalized host and status.
- Provide a command-line tool to process files and emit text or canonical JSON summaries.

#### Responsibilities
- **Parsing and aggregation:** `LogDigest` parses individual log lines, tracks request, host, status, and slow-request counts, and builds summary statistics.
- **Host ranking:** `top_n` ranks host counts by descending count with lexical tie-breaking.
- **Command-line processing:** `log_digest.pl` reads access-log files, applies configured aliases and excluded hosts, and prints the summary.
- **Configuration and output:** The script accepts options for ranking, level, exclusions, and JSON output; it reads aliases from `LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`.

#### Key units
- `LogDigest::new` — validates the level and initializes aggregation state.
- `LogDigest::parse_line` — extracts host, time, method, path, status, and milliseconds from a log entry.
- `LogDigest::add` — parses and accumulates an entry, enforcing the line limit and slow-request threshold.
- `LogDigest::summarize` — returns generated time, totals, distinct-host count, busiest-host count, slow count, and status counts.
- `LogDigest::top_n` — returns the highest-ranked count keys.
- `log_digest.pl::read_aliases` — loads `from=to` host mappings, returning no aliases if the config cannot be opened.
- `log_digest.pl::main` — processes options and input files, then prints text or JSON results.

#### Data flow
- The script reads alias configuration and builds an exclusion lookup.
- For each input line, it applies the alias for the first space-delimited field when checking host exclusions; excluded lines are skipped.
- Remaining lines are passed to `LogDigest::add`, which parses each request and updates host, status, total, and slow-request counts.
- `summarize` produces the aggregate result; text output includes counts and ranked hosts, while JSON output uses canonical encoding.

#### Dependencies
- `log_digest.pl` uses `Getopt::Long`, `JSON::PP`, and `LogDigest`.
- `LogDigest` uses `POSIX` for UTC timestamp formatting, `List::Util` for `max` and `sum`, `Carp` for errors, and `Exporter` for exports.

#### Cross-cutting
- `LogDigest` normalizes hosts to lowercase and removes one trailing dot.
- Aggregation rejects more than `50,000` lines; requests over `1,500` milliseconds count as slow.
- The default level is `WARN`; unknown levels and line-limit violations raise errors.
- Host ranking is deterministic: count descending, then key ascending.
