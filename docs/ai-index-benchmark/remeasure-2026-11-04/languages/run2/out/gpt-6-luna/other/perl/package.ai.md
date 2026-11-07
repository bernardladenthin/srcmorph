### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:01:50Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Converts access-log traffic into normalized host and status statistics, with filtering and text or JSON reporting.

#### Purpose
- Parse web-server access logs and aggregate request, host, status, and slow-request counts.
- Provide a command-line report with configurable host aliases, exclusions, ranking, and output format.

#### Responsibilities
- **Parsing and aggregation:** Match access-log entries, normalize hosts, enforce a line limit, and accumulate counts and summary statistics.
- **Ranking:** Order count keys by descending count with lexical tie-breaking.
- **CLI reporting:** Parse options and log files, load host aliases, filter excluded hosts, and print text or canonical JSON summaries.

#### Key units
- `LogDigest`: Perl module exposing an object API for collecting entries and producing aggregate summaries.
- `LogDigest::parse_line`: Extract host, timestamp, method, path, status, and response time from a log line.
- `LogDigest::add`: Add a valid entry to host and status counts and track slow requests.
- `LogDigest::summarize`: Return generated time, request and host totals, busiest-host count, slow-request count, and status counts.
- `LogDigest::top_n`: Rank keys from a count hash.
- `log_digest.pl::read_aliases`: Read `from=to` host mappings from configuration.
- `log_digest.pl::main`: Filter and digest input logs, then emit the selected report format.

#### Data flow
- `log_digest.pl` parses CLI options and loads aliases from `LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`.
- For each input line, the script checks the first field against the exclusion list after alias mapping; remaining lines go to `LogDigest->add`.
- `LogDigest` parses and normalizes entries, updates counts, and returns a summary; the script prints canonical JSON or text with ranked hosts.

#### Dependencies
- `LogDigest` uses `POSIX` for UTC timestamps, `List::Util` for aggregate operations, `Carp` for errors, and `Exporter` for optional exports.
- `log_digest.pl` uses `Getopt::Long`, `JSON::PP`, `FindBin`, and `LogDigest`.
- Configuration and inputs: `LOG_DIGEST_CONFIG` alias file and access-log files supplied through `@ARGV`.

#### Cross-cutting
- `LogDigest` defaults to `WARN`, accepts `DEBUG`, `INFO`, `WARN`, or `ERROR`, rejects unknown levels, and limits processing to `MAX_LINES` (50,000).
- Hosts are lowercased and have one trailing dot removed; requests over `SLOW_REQUEST_MS` (1,500 ms) count as slow.
- The CLI defaults to the top 10 hosts and text output; config-file read failures produce an empty alias map.
