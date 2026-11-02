### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:39:30Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Converts web-server access logs into host- and status-based request metrics, with configurable filtering and text or JSON reporting.

#### Purpose
- Parse and aggregate access-log requests, tracking host counts, HTTP status totals, and slow requests.
- Provide a command-line report with host aliases, exclusions, and selectable output format.

#### Responsibilities
- **Log parsing and aggregation:** validate access-log lines, normalize hosts, enforce input limits, and accumulate request, status, and slow-request counts.
- **Reporting and ranking:** generate timestamped aggregate metrics and rank hosts by request count.
- **CLI and configuration:** parse options and log files, load host aliases, filter excluded hosts, and print text or canonical JSON summaries.

#### Key units
- `LogDigest` — package implementing log-line parsing, aggregation, summaries, and host ranking.
- `LogDigest::parse_line` — returns parsed host, timestamp, method, path, status, and elapsed milliseconds, or `undef` for unmatched lines.
- `LogDigest::add` — adds a valid request to the counters; rejects input beyond `MAX_LINES = 50_000`.
- `LogDigest::summarize` — returns generated time, total requests, distinct hosts, busiest-host count, slow count, and status counts.
- `LogDigest::top_n` — ranks hosts by descending counts with lexical tie-breaking.
- `log_digest.pl::main` — processes CLI options and files, then emits the configured report.
- `log_digest.pl::read_aliases` — reads `from = to` host mappings from the configured file.

#### Data flow
- `log_digest.pl` reads each input file, resolves its first-field host through aliases, skips excluded resolved hosts, and passes remaining lines to `LogDigest`.
- `LogDigest` parses accepted lines, normalizes hostnames, and accumulates host, status, request, and slow-request counts.
- The script obtains a summary and ranked hosts, then prints text totals and host entries or canonical JSON.

#### Dependencies
- `log_digest.pl` uses `Getopt::Long`, `JSON::PP`, `FindBin`, and the `LogDigest` package.
- `LogDigest` uses `POSIX` time functions, `List::Util` aggregation helpers, and `Carp::croak`.
- Configuration comes from `LOG_DIGEST_CONFIG`, defaulting to `/etc/log-digest.conf`; an unreadable config is treated as having no aliases.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`; accepted levels are `DEBUG`, `INFO`, `WARN`, and `ERROR`. The stored level is not otherwise used by the package.
- Slow requests are those exceeding `SLOW_REQUEST_MS = 1500`; exceeding `MAX_LINES` croaks with `more than 50000 lines`.
- Malformed lines are ignored by `add`; `parse_line` returns `undef` when parsing fails.
- CLI defaults to top 10 hosts, `WARN`, and text output; nonpositive `--top` values and unreadable input logs produce errors.
