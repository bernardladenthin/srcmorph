### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:40:00Z
- G: 
- A: 0.0.0
- X: file
- P: B953BC65
---
> Summarizes access-log requests by host and status, reporting busiest hosts and slow-request counts as text or JSON.

#### Purpose
- Reads access-log files, applies configured host aliases and exclusions, and prints a request summary.
- Supports text output or canonical JSON.

#### Type
- Script; no package declaration.
- Procedural; no exports or object constructor defined in this file.

#### Input
- `--top` / `-n` → `$top` (integer; default `10`).
- `--level` → `$level` (string; default `'WARN'`).
- `--json` / `--nojson` → `$as_json` (default `0`).
- `--exclude-host` → `@exclude_hosts` (repeatable).
- `--help` / `-h` → prints usage and exits `0`.
- `@ARGV` → access-log file paths, read line by line.
- `%ENV`: `LOG_DIGEST_CONFIG` → `$config_file`; defaults to `/etc/log-digest.conf`.
- Reads the alias configuration file at `$config_file`; missing or unreadable config produces an empty alias map.
- Reads each access-log file named in `@ARGV`.

#### Output
- Text mode: request, host, and slow-request counts, followed by `top_n` host entries.
- JSON mode: canonical JSON encoding of the summary, followed by a newline.
- Returns `0` from `main` and exits with that status.
- Writes usage to standard output for help; validation and file-read errors go to standard error via `die`.

#### Core logic
- Loads host aliases from the config file, ignoring blank and comment lines; parses remaining lines as `from = to`.
- Builds an exclusion lookup from `@exclude_hosts`.
- Creates a `LogDigest` instance using `$level`.
- For each input line, extracts the first space-delimited field as the host, resolves its alias, skips excluded hosts, and adds remaining lines to the digest.
- Summarizes the digest; prints canonical JSON or text counts plus the top hosts.

#### Subs
- `usage() -> string` — formats command usage.
- `read_aliases($path) -> hash` — reads host aliases from config.
- `main() -> 0` — processes logs and prints summary.

#### Constants and configuration
- `$config_file` — `$ENV{LOG_DIGEST_CONFIG}` or `'/etc/log-digest.conf'`.
- `$top` — `10`.
- `$level` — `'WARN'`.
- `$as_json` — `0`.
- `@exclude_hosts` — initially empty.

#### Dependencies
- `strict`, `warnings` — enable strict checks and warnings.
- `FindBin` — imports `$Bin`.
- `Getopt::Long` — imports `GetOptions`.
- `JSON::PP` — uses `new`, `canonical`, and `encode`.
- `LogDigest` — imports `top_n`; calls `new`, `add`, and `summarize`.
- `lib` — adds `$Bin` to module search paths.

#### Exceptions / Errors
- Dies with usage if option parsing fails.
- Dies with `--top must be positive` when `$top < 1`.
- Dies with `cannot read $file: $!` if an access-log file cannot be opened.
- Config-file open failures are silently treated as no aliases.
