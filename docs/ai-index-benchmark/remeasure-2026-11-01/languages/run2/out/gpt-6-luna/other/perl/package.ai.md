### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:39:31Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Converts web-server access logs into normalized host and status counts, identifies slow requests, and presents ranked summaries in text or JSON.

#### Purpose
- Parse and aggregate combined-log-style requests into a per-host and per-status digest.
- Provide a command-line tool for filtering hosts, applying aliases, and displaying the digest.

#### Responsibilities
- **Log parsing and aggregation:** Validate entries, normalize hostnames, track request and status totals, and count requests exceeding the slow-request threshold.
- **Ranking and summaries:** Report aggregate counts and rank hosts by count, with alphabetical tie-breaking.
- **CLI and configuration:** Read log files, apply configured host aliases and exclusions, and emit text or canonical JSON output.

#### Key units
- `LogDigest` — object-based parser and accumulator with `new`, `add`, and `summarize`.
- `parse_line` — parse one access-log entry into host, time, method, path, status, and duration fields.
- `top_n` — return count keys ranked by descending count and alphabetical tie-break.
- `_normalize_host` — lowercase hostnames and remove a trailing dot.
- `log_digest.pl::read_aliases` — load `from = to` host mappings, tolerating a missing or unreadable config file.
- `log_digest.pl::main` — parse CLI options, filter log entries, build a digest, and print its summary.
- `log_digest.pl::usage` — build command usage text.

#### Data flow
- The CLI reads aliases from `LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`, then processes each requested log file line by line.
- It checks each line’s first field against exclusions after alias lookup, then passes non-excluded lines to `LogDigest->add`.
- `LogDigest` parses and normalizes valid entries, updates host/status/request/slow counts, and produces a summary; the CLI prints text or canonical JSON and uses `top_n` for host ranking.

#### Dependencies
- `log_digest.pl` uses `Getopt::Long`, `JSON::PP`, `FindBin`, and `LogDigest`.
- `LogDigest` uses `POSIX` for UTC timestamp formatting, `List::Util` for aggregation utilities, `Carp` for errors, and `Exporter` for optional exports.

#### Cross-cutting
- `LogDigest` defaults to level `WARN`, accepts `DEBUG`, `INFO`, `WARN`, or `ERROR`, and rejects unknown levels.
- `LogDigest` caps input at `MAX_LINES` (50,000) and counts requests slower than `SLOW_REQUEST_MS` (1,500 ms).
- Malformed log lines are ignored; exceeding the line cap croaks. The CLI rejects non-positive `--top` values and dies on log-file open failures.
