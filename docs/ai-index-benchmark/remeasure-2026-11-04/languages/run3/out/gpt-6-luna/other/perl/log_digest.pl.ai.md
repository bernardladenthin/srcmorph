### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:03:49Z
- G: 
- A: 0.0.0
- X: file
- P: 4F9ECAEF
---
> Summarizes access-log requests by host and status-related metrics, printing the busiest hosts and request totals as text or JSON.

#### Purpose
- Reads one or more access-log files, applies configured host aliases and exclusions, and summarizes requests.
- Prints a text summary with the top hosts or a canonical JSON summary.

#### Type
- Script; package `main`; procedural.
- Imports `top_n` from `LogDigest`; no exports or object construction of its own.

#### Input
- `--top` / `-n` → `$top`; integer, default `10`.
- `--level` → `$level`; string, default `WARN`.
- `--json` / `--nojson` → `$as_json`; boolean, default `0`.
- `--exclude-host` → `@exclude_hosts`; repeatable.
- `--help` / `-h` → prints usage and exits `0`.
- `@ARGV` → access-log file paths; each file is read line by line.
- `%ENV`: `LOG_DIGEST_CONFIG` → `$config_file`; defaults to `/etc/log-digest.conf`.
- Reads `$config_file` for host aliases; unreadable or missing config yields no aliases.
- Reads each log file via a file handle; no standard-input reading.

#### Output
- Text mode: `"<total> requests from <hosts> hosts, <slow> slow\n"`, followed by one line per result from `top_n($digest->{hosts}, $top)`.
- JSON mode: canonical JSON encoding of the summary, followed by a newline.
- Returns `0` from `main`; process exits with that status.
- Errors print to standard error through `die`.

#### Core logic
- Parses options; dies if `$top < 1`.
- Loads aliases from config lines, skipping blank and comment lines and splitting remaining lines at `=`.
- Builds an exclusion lookup from `@exclude_hosts`.
- Creates a `LogDigest` object with `level => $level`.
- For each log line, takes the first space-delimited field as the host; skips the line if its aliased host (or original host when no alias exists) is excluded.
- Adds remaining lines to the digest, summarizes it, then prints JSON or text with top hosts.

#### Subs
- `usage() -> string` — formats usage text.
- `read_aliases($path) -> hash` — reads host alias mappings; returns an empty hash if opening fails.
- `main() -> 0` — reads logs, summarizes requests, and prints output.

#### Constants and configuration
- `$config_file` — `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- `$top` — `10`.
- `$level` — `'WARN'`.
- `$as_json` — `0`.
- `@exclude_hosts` — empty initially.

#### Dependencies
- `strict`, `warnings` — enable compile-time and runtime checks.
- `FindBin` — imports `$Bin`.
- `Getopt::Long` — imports `GetOptions`.
- `JSON::PP` — uses `new`, `canonical`, and `encode`.
- `LogDigest` — imports `top_n`; calls `new`, `add`, and `summarize`.

#### Exceptions / Errors
- `GetOptions` failure: dies with usage text.
- `$top < 1`: dies with `--top must be positive`.
- Log-file open failure: dies with `cannot read $file: $!`.
- Config-file open failure is ignored; `read_aliases` returns an empty hash.
