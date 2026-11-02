### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:37:23Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes access-log requests by host and status, printing busiest hosts and request counts as text or JSON.

#### Purpose
- Reads one or more access-log files, applies configured host aliases and exclusions, and reports a digest.
- Supports text and JSON output.

#### Type
- Script: `log_digest.pl`; procedural.
- Uses `LogDigest`; imports `top_n`.

#### Input
- `--top` / `-n` → `$top` (integer; default `10`).
- `--level` → `$level` (string; default `WARN`).
- `--json` / `--nojson` → `$as_json` (default false).
- `--exclude-host` → `@exclude_hosts` (repeatable).
- `--help` / `-h` → prints usage and exits `0`.
- `@ARGV` → access-log file paths.
- `$ENV{LOG_DIGEST_CONFIG}` → `$config_file`; defaults to `/etc/log-digest.conf`.
- Reads the config file as alias lines (`from = to`); unreadable or missing config yields no aliases.
- Reads each supplied log file line by line.

#### Output
- Text mode: request, host, and slow-request counts; then `top_n` host entries, one per line.
- JSON mode: canonical JSON encoding of the summary, followed by a newline.
- Exits with `main`’s return value (`0`) on success.

#### Core logic
- Parses command-line options and rejects `$top < 1`.
- Reads host aliases from the config file; skips blank lines and lines beginning with optional whitespace followed by `#`.
- Builds an exclusion lookup from `@exclude_hosts`.
- Creates a `LogDigest` object with the selected `level`.
- For each log line, extracts the first space-delimited field as the host; skips the line if its aliased host (or original host) is excluded, otherwise adds it to the digest.
- Summarizes the digest and prints canonical JSON or text counts plus the top hosts.

#### Subs
- `usage() -> string` — return command usage text.
- `read_aliases($path) -> hash` — read host aliases from a config file.
- `main() -> 0` — process logs and print digest output.

#### Constants and configuration
- `$config_file` — `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- `$top` — `10`.
- `$level` — `'WARN'`.
- `$as_json` — `0`.
- `@exclude_hosts` — initially empty.

#### Dependencies
- `strict` — strict variable and reference checks.
- `warnings` — enable warnings.
- `FindBin` — imports `$Bin`.
- `lib` — adds `$Bin` to `@INC`.
- `Getopt::Long` — imports `GetOptions`.
- `JSON::PP` — `new`, `canonical`, `encode`.
- `LogDigest` — `new`, `add`, `summarize`; imports `top_n`.

#### Exceptions / Errors
- Dies with usage text if option parsing fails.
- Dies with `--top must be positive` if `$top < 1`.
- Dies with `cannot read $file: $!` if an input log file cannot be opened.
- `read_aliases` returns an empty alias hash if its config file cannot be opened.
