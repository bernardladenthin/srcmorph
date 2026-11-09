### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:58:45Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Converts web-server access logs into host, status, and slow-request statistics, with configurable host aliases, exclusions, and text or JSON reporting.

#### Purpose
- Parse access-log requests and aggregate counts by normalized host and HTTP status.
- Provide a command-line workflow for filtering logs and reporting aggregate and busiest-host statistics.

#### Responsibilities
- **Parsing and aggregation:** `LogDigest` parses combined-log-style lines, counts valid requests by host and status, and tracks slow requests.
- **Host handling and ranking:** Normalize hostnames and rank count keys by descending count with lexical tie-breaking.
- **CLI reporting:** `log_digest.pl` reads alias configuration and log files, applies host exclusions, and prints text or canonical JSON summaries.

#### Key units
- `LogDigest` — maintains request statistics and provides `new`, `add`, and `summarize`.
- `parse_line` — parses one access-log line into host, time, method, path, status, and duration fields, or returns undefined for a nonmatch.
- `top_n` — returns up to the requested number of count keys in ranked order.
- `_normalize_host` — lowercases hostnames and removes one trailing dot.
- `read_aliases` — loads host aliases from the configured file; returns no aliases if it cannot be opened.
- `main` — processes CLI options and log files, then emits the selected report.
- `usage` — formats command-line usage text.

#### Data flow
- `log_digest.pl` parses options, loads aliases, and builds the exclusion set.
- For each log line, it takes the first space-delimited field as the host, applies its alias if present, and skips the line if that host is excluded.
- Remaining lines are passed to `LogDigest->add`, which parses valid requests and updates host, status, and slow-request counts.
- `summarize` produces aggregate statistics; the CLI prints canonical JSON or a text summary with ranked hosts from `top_n`.

#### Dependencies
- `LogDigest` uses `POSIX` for UTC timestamp formatting, `List::Util` for `max` and `sum`, `Carp` for `croak`, and `Exporter` for optional exports.
- `log_digest.pl` uses `Getopt::Long` for options, `JSON::PP` for JSON output, `FindBin`, and `LogDigest`.
- Alias configuration comes from `$ENV{LOG_DIGEST_CONFIG}` or defaults to `/etc/log-digest.conf`.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`, validates levels against `DEBUG`, `INFO`, `WARN`, and `ERROR`, and rejects input beyond `MAX_LINES` (50,000).
- Requests are counted as slow when duration exceeds `SLOW_REQUEST_MS` (1,500 ms).
- Invalid log lines are ignored by `add`; invalid levels and line-limit overflow raise errors. The CLI rejects nonpositive `--top` values and dies on unreadable access-log files.
