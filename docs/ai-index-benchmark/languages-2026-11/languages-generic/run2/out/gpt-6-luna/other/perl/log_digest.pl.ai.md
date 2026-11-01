### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:32:31Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes access logs into request, host, and slow-request totals, with busiest-host output in text or JSON.

#### Purpose
- Reads access-log files, applies configured host aliases and host exclusions, and aggregates requests through `LogDigest`.
- Prints a summary and, in text mode, the top hosts.

#### Type
- Perl command-line script.

#### Input
- `@ARGV`: access-log file paths; each line is passed to `LogDigest->add`.
- `LOG_DIGEST_CONFIG`: optional environment variable for the host-alias config path.
- Config lines: `from=to` aliases; blank lines and lines beginning with `#` are skipped.
- Options: `--top` / `-n`, `--level`, `--json` / `--no-json`, repeated `--exclude-host`, and `--help` / `-h`.

#### Output
- Text mode: request, host, and slow counts, followed by `top_n` host entries.
- JSON mode: canonical JSON encoding of the summary, followed by a newline.
- Returns `0` from `main`; exits with that status.

#### Core logic
- Reads host aliases from the config file; if opening it fails, proceeds with no aliases.
- Builds an exclusion set and creates a `LogDigest` with the selected level.
- For each log line, extracts the first space-delimited field as the host; skips the line if its aliased host (or original host if no alias) is excluded.
- Adds remaining lines to the digest, summarizes it, then emits JSON or text output.

#### Public API
- `main() -> 0` — reads logs and prints summary.
- `usage() -> string` — formats command usage.
- `read_aliases($path) -> hash` — reads host aliases.

#### Constants and configuration
- `LOG_DIGEST_CONFIG`: config path environment variable; defaults to `/etc/log-digest.conf`.
- `$top`: default `10`; must be positive.
- `$level`: default `'WARN'`.
- `$as_json`: default `0`.
- `@exclude_hosts`: default empty.
- CLI options: `--top` / `-n` (integer), `--level` (string), `--json` (also supports `--no-json`), `--exclude-host` (repeatable), `--help` / `-h`.
- Exit conditions: help exits `0`; nonpositive `--top` dies with `--top must be positive`.

#### Dependencies
- `FindBin` (`$Bin`), `Getopt::Long` (`GetOptions`), `JSON::PP` (`canonical`, `encode`), `LogDigest` (`new`, `add`, `summarize`, `top_n`).
- Perl built-ins: `open`, `close`, `split`, `chomp`, `map`, `printf`, `print`, `exit`.

#### Exceptions / Errors
- Dies on option parsing failure, displaying usage.
- Dies if `--top` is less than `1`.
- Dies when an access-log file cannot be opened: `cannot read $file: $!`.
- Alias-config open failures are silently treated as an empty alias map.
