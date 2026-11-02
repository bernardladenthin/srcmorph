### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:23:38Z
- G: 
- A: 0.0.0
- X: file
- P: B953BC65
---
> Summarizes access-log requests by host and status, reporting busiest hosts and slow-request counts as text or JSON.

#### Purpose
- Reads access-log files, applies configured host aliases and exclusions, and prints a request summary.
- Supports text output or canonical JSON output.

#### Type
- Script; package `main`; procedural; no exports or object constructor.
- Uses `LogDigest` objects via `LogDigest->new`.

#### Input
- `--top` or `--n` → `$top` (default `10`).
- `--level` → `$level` (default `WARN`).
- `--json` / `--no-json` → `$as_json` (default `0`).
- `--exclude-host` → appends to `@exclude_hosts`.
- `--help` or `--h` → prints usage and exits `0`.
- Remaining `@ARGV` entries are access-log file paths.
- `%ENV`: `LOG_DIGEST_CONFIG` sets `$config_file`; default `/etc/log-digest.conf`.
- Reads the config file for `host = alias` mappings; missing or unreadable config yields no aliases.
- Reads each specified log file line by line.

#### Output
- Text mode: request, host, and slow counts, followed by `top_n` host entries.
- JSON mode: canonical JSON encoding of the summary, followed by a newline.
- Returns `0` from `main`; process exits with that status.
- Invalid `$top` dies with `--top must be positive`.
- File-open failures die with `cannot read $file: $!`.
- Option-parsing failure dies after printing usage; help exits successfully.

#### Core logic
- Reads aliases from `$config_file`; builds an exclusion lookup from `@exclude_hosts`.
- Creates a digest with `LogDigest->new(level => $level)`.
- For each log line, extracts the first space-delimited field as the host.
- Skips lines whose aliased host (or original host if unmapped) is excluded; adds other lines to the digest.
- Calls `$digest->summarize`.
- Prints canonical JSON when `$as_json` is true; otherwise prints summary counts and `top_n($digest->{hosts}, $top)`.

#### Subs
- `usage() -> string` — Builds command usage text.
- `read_aliases($path) -> hash` — Reads host alias mappings; returns empty hash if open fails.
- `main() -> status` — Loads inputs, aggregates requests, and prints results.

#### Constants and configuration
- `$config_file` — `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- `$top` — `10`.
- `$level` — `WARN`.
- `$as_json` — `0`.
- `@exclude_hosts` — initially empty.

#### Dependencies
- `strict` — strict variable and reference checks.
- `warnings` — enables warnings.
- `FindBin` — imports `$Bin`.
- `Getopt::Long` — imports `GetOptions`.
- `JSON::PP` — canonical JSON encoding.
- `LogDigest` — uses `top_n`; calls `new`, `add`, and `summarize`.
- `lib` — adds `$Bin` to `@INC`.

#### Exceptions / Errors
- `die` if option parsing fails.
- `die` if `$top < 1`.
- `die` when an access-log file cannot be opened.
- Config-file open failure is handled by returning an empty alias hash.
