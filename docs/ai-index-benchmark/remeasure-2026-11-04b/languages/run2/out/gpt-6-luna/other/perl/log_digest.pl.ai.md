### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:16:09Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Summarizes access-log requests by host and status, reporting busiest hosts and aggregate counts as text or JSON.

#### Purpose
- Command-line tool for digesting access logs with configurable host aliases and exclusions.
- Reports total requests, distinct hosts, slow requests, and top hosts.

#### Type
- Script; no package declaration; procedural.
- Imports `top_n` from `LogDigest`; no object construction in this script, though it uses a `LogDigest` object.

#### Input
- Options: `--top` / `-n` sets `$top`; `--level` sets `$level`; `--json` / `--nojson` sets `$as_json`; repeatable `--exclude-host` sets `@exclude_hosts`; `--help` / `-h` prints usage and exits.
- Remaining `@ARGV` entries are access-log file paths; each file is read line by line.
- `%ENV`: `LOG_DIGEST_CONFIG`, default `/etc/log-digest.conf`; config lines supply host aliases as `from=to`.
- Config and log files are opened for reading; no standard input read.

#### Output
- Text mode: request, host, and slow counts; then one top-host entry per line from `top_n`.
- JSON mode: canonical JSON encoding of the summary, followed by a newline.
- Exit status: `main` returns `0`; help exits `0`. Invalid option parsing or nonpositive `$top` dies.

#### Core logic
- Load aliases from the configured file; missing/unreadable config yields an empty alias map.
- Build an exclusion lookup from `@exclude_hosts`.
- Create `LogDigest` with the selected `level`.
- For each input file, read lines, extract the first space-delimited field as host, resolve its alias, and skip excluded hosts.
- Add remaining lines to the digest, then summarize it.
- Emit canonical JSON or print aggregate counts and `top_n` results.

#### Subs
- `usage() -> string` — Return command usage text.
- `read_aliases($path) -> hash` — Read host aliases from config.
- `main() -> 0` — Process logs and print digest.

#### Constants and configuration
- `$config_file` — `LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`.
- `$top` — Default `10`.
- `$level` — Default `'WARN'`.
- `$as_json` — Default `0`.
- `@exclude_hosts` — Initially empty.

#### Dependencies
- `strict`, `warnings` — Enable compile-time and runtime checks.
- `FindBin` — Imports `$Bin`.
- `Getopt::Long` — Imports `GetOptions`.
- `JSON::PP` — Uses `new`, `canonical`, and `encode`.
- `LogDigest` — Imports `top_n`; calls `new`, `add`, and `summarize`.

#### Exceptions / Errors
- `GetOptions` failure dies with usage text.
- `$top < 1` dies with `--top must be positive`.
- Failure to open a log file dies with `cannot read $file: $!`.
- Failure to open the alias config is handled by returning an empty alias map.
