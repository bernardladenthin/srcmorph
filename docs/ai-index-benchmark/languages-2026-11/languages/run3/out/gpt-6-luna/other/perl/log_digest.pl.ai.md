### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:53:41Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes access logs by request status and busiest hosts, with configurable host aliases and text or JSON output.

#### Purpose
- Reads log files, excludes selected hosts, and reports request totals, host totals, slow requests, and top hosts.
- Loads host aliases from a configuration file.

#### Type
- Script: `log_digest.pl`; procedural.
- Uses `LogDigest` and imports `top_n`.

#### Input
- `--top` or `--n` (`i`) sets `$top`; default `10`.
- `--level` (`s`) sets `$level`; default `WARN`.
- `--json` / `--nojson` sets `$as_json`; default false.
- Repeated `--exclude-host` (`s`) appends to `@exclude_hosts`.
- `--help` or `-h` prints usage and exits successfully.
- Remaining `@ARGV` values are log file paths; each is read line by line.
- Reads `%ENV` key `LOG_DIGEST_CONFIG`; default `/etc/log-digest.conf`. The config file contains host alias mappings.

#### Output
- Text mode: request, host, and slow-request totals, followed by lines from `top_n($digest->{hosts}, $top)`.
- JSON mode: canonical JSON encoding of the summary, followed by a newline.
- Exits with `main()`’s return value (`0` on success).

#### Core logic
- Reads aliases from the configured file; missing or unreadable config yields an empty alias map.
- Builds an exclusion lookup from `@exclude_hosts` and creates a `LogDigest` object with the selected `level`.
- For each log line, takes the first space-delimited field as the host and skips it if its aliased name (or original name) is excluded.
- Adds remaining lines to the digest, summarizes it, and prints JSON or text output.

#### Subs
- `usage() -> string` — Build command-line usage text.
- `read_aliases($path) -> %aliases` — Read host aliases from config.
- `main() -> 0` — Process logs and print summary.

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
- `LogDigest` — imports `top_n`; calls `new`, `add`, and `summarize`.
- Pragmas: `strict`, `warnings`.

#### Exceptions / Errors
- Dies with usage text if option parsing fails.
- Dies with `--top must be positive` if `$top < 1`.
- Dies with `cannot read $file: $!` if a log file cannot be opened.
- Config-file open failure is handled silently by returning an empty alias map.
