### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:10:38Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes access-log requests by host and status, reporting busiest hosts and slow-request counts as text or JSON.

#### Purpose
- Reads access-log files, applies configured host aliases and exclusions, and prints a request summary.
- Supports text output or canonical JSON output.

#### Type
- Script: `log_digest.pl`; procedural.
- Imports `top_n` from `LogDigest`; uses `LogDigest->new`.

#### Input
- `--top` / `-n` (`top|n=i`) sets `$top`; default `10`.
- `--level` (`level=s`) sets `$level`; default `WARN`.
- `--json` / `--nojson` (`json!`) sets `$as_json`; default `0`.
- `--exclude-host` (`exclude-host=s`) appends hosts to `@exclude_hosts`.
- `--help` / `-h` (`help|h`) prints usage and exits `0`.
- Remaining `@ARGV` entries are access-log file paths; each log line is read as a request record.
- Reads `%ENV` key `LOG_DIGEST_CONFIG`; default config path is `/etc/log-digest.conf`.
- Reads the alias configuration file and each specified log file.

#### Output
- Text mode: prints `<total> requests from <hosts> hosts, <slow> slow`, then one `top_n` result per line.
- JSON mode: prints the summary as canonical JSON followed by a newline.
- Exits with `main`’s return value (`0` on successful completion).

#### Core logic
- Loads host aliases from the configured file; missing or unreadable config yields an empty alias map.
- Builds an exclusion lookup from `@exclude_hosts`.
- Creates a `LogDigest` object with the selected `level`.
- For each log line, extracts the first space-delimited field as the host, resolves its alias, skips excluded hosts, and adds remaining lines to the digest.
- Calls `summarize`; emits canonical JSON or text summary and top hosts.
- Rejects `$top < 1` before running `main`.

#### Subs
- `usage() -> string` — Build command usage text.
- `read_aliases($path) -> hash` — Read host alias mappings.
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
- `JSON::PP` — canonical JSON encoding.
- `LogDigest` — uses `new`, `add`, and `summarize`; imports `top_n`.
- Pragmas: `strict`, `warnings`; `lib "$Bin"` adds the script directory to `@INC`.

#### Exceptions / Errors
- Dies with `usage()` if `GetOptions` fails.
- Dies with `--top must be positive` if `$top < 1`.
- Dies with `cannot read $file: $!` when a log file cannot be opened.
- `read_aliases` returns an empty hash if its config file cannot be opened.
