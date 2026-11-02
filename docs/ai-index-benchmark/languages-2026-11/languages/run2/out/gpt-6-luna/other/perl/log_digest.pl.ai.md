### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:32:37Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes access-log requests by host and status, reporting busiest hosts and slow-request counts as text or JSON.

#### Purpose
- Reads access-log files and reports request totals, host totals, slow requests, and top hosts.
- Applies host aliases and exclusions before summarizing.

#### Type
- Script; no declared package; procedural.

#### Input
- Options: `top|n=i` sets `$top` (default `10`); `level=s` sets `$level` (default `WARN`); `json!` sets `$as_json` (default `0`); `exclude-host=s` appends to `@exclude_hosts`; `help|h` prints usage and exits.
- Positional `@ARGV`: access-log file paths.
- `%ENV`: `LOG_DIGEST_CONFIG`, default `/etc/log-digest.conf`.
- Reads the alias configuration file and each positional access-log file.

#### Output
- JSON mode: canonical JSON encoding of `$summary`, followed by a newline.
- Text mode: request, host, and slow counts; then host entries from `top_n($digest->{hosts}, $top)`, one per line.
- `main` returns `0`; script exits with `main()`'s result.

#### Core logic
- Reads aliases from `$config_file`; missing or unreadable config produces an empty alias map.
- Builds an exclusion lookup from `@exclude_hosts`.
- Creates a `LogDigest` object with the selected `level`.
- For each input line, extracts the first space-delimited field as the host; skips lines whose aliased host (or original host if no alias) is excluded; adds remaining lines to the digest.
- Summarizes the digest and prints canonical JSON or text totals plus the top hosts.

#### Subs
- `usage() -> string` — Formats command usage and config description.
- `read_aliases($path) -> hash` — Reads host mappings from config.
- `main() -> 0` — Reads logs, summarizes, and prints results.

#### Constants and configuration
- `$config_file` — `LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`.
- `$top` — `10`.
- `$level` — `WARN`.
- `$as_json` — `0`.
- `@exclude_hosts` — Initially empty.

#### Dependencies
- `FindBin` — imports `$Bin`.
- `Getopt::Long` — imports `GetOptions`.
- `JSON::PP` — canonical JSON encoding.
- `LogDigest` — imports `top_n`; uses `LogDigest->new`, `add`, and `summarize`.
- `strict`, `warnings` — pragmas.

#### Exceptions / Errors
- `GetOptions` failure dies with usage text.
- `$top < 1` dies with `--top must be positive`.
- Failure to open an access-log file dies with `cannot read $file: $!`.
- Failure to open the alias config is silently treated as no aliases.
