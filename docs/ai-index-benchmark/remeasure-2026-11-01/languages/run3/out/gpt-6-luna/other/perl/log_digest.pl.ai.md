### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:55:26Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes access logs by request volume, host, and slow-request count, with optional host aliases, exclusions, and JSON output.

#### Purpose
- Script for aggregating access-log data and reporting busiest hosts and status-code counts.
- Reads host aliases from configuration and excludes selected hosts before aggregation.

#### Type
- Script; no package declaration or exports.
- Procedural; creates a `LogDigest` object with `LogDigest->new`.

#### Input
- `--top` / `-n` sets `$top` (integer; default `10`).
- `--level` sets `$level` (string; default `WARN`).
- `--json` sets `$as_json` true; `--no-json` sets it false (default false).
- `--exclude-host` appends each value to `@exclude_hosts`.
- `--help` / `-h` prints usage and exits successfully.
- `@ARGV` supplies access-log file paths.
- `%ENV`: `LOG_DIGEST_CONFIG`; default `/etc/log-digest.conf`.
- Reads the config file if openable; lines map alias source to target using `=`, with blank and comment lines skipped. Missing/unreadable config produces no aliases.
- Reads each log file line by line; takes the first space-delimited field as the host.

#### Output
- Text mode: summary line (`requests`, `hosts`, `slow`), followed by the hosts returned by `top_n`, one per line.
- JSON mode: canonical JSON encoding of the summary, followed by a newline.
- Help prints usage text and exits `0`; successful `main` returns `0`.
- Invalid `--top` dies with `--top must be positive`; unreadable log file dies with `cannot read $file: $!`.

#### Core logic
- Parses command-line options; rejects `$top < 1`.
- Loads aliases and builds an exclusion lookup from `@exclude_hosts`.
- Creates a digest with `LogDigest->new(level => $level)`.
- For each log line, resolves its host through the alias map and skips it if excluded; otherwise passes the line to `$digest->add`.
- Calls `$digest->summarize`; emits canonical JSON or a text summary and `top_n($digest->{hosts}, $top)` results.

#### Subs
- `usage() -> string` — returns command-line usage text.
- `read_aliases($path) -> hash` — reads host alias mappings.
- `main() -> 0` — reads logs, summarizes, and prints results.

#### Constants and configuration
- `$config_file` — `$ENV{LOG_DIGEST_CONFIG} // '/etc/log-digest.conf'`.
- `$top` — `10`.
- `$level` — `'WARN'`.
- `$as_json` — `0`.
- `@exclude_hosts` — initially empty.

#### Dependencies
- `FindBin` — imports `$Bin`.
- `Getopt::Long` — imports `GetOptions`.
- `JSON::PP` — uses `JSON::PP->new`, `canonical`, and `encode`.
- `LogDigest` — imports `top_n`; uses `new`, `add`, and `summarize`.
- Pragmas: `strict`, `warnings`.

#### Exceptions / Errors
- `GetOptions` failure dies with `usage()`.
- `$top < 1` dies with `--top must be positive\n`.
- Failure to open a log file dies with `cannot read $file: $!\n`.
- Failure to open the alias config file is handled by returning an empty alias hash.
