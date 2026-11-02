### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:56:11Z
- G: 
- A: 0.0.0
- X: file
- P: B953BC65
---
> A command‑line tool that parses web server access logs, filters hosts, and reports request counts and slow requests, optionally in JSON.

#### Purpose
- Parses log files to count requests per host.
- Reports busiest hosts and status‑code statistics.
- Supports JSON output.

#### Type
- Script.
- Uses package `LogDigest` (imported `top_n`).

#### Input
- `--top|n=i` → `$top` (default 10).
- `--level=s` → `$level` (default 'WARN').
- `--json!` → `$as_json` (default 0).
- `--exclude-host=s` → `@exclude_hosts`.
- `--help|h` → prints usage.
- Positional arguments: `FILE...` (log files).
- Environment: `LOG_DIGEST_CONFIG` (default `/etc/log-digest.conf`).

#### Output
- If `$as_json`: JSON string of `$summary` (canonical order).
- Else: plain text:
  - `printf "%d requests from %d hosts, %d slow\n", $summary->{total}, $summary->{hosts}, $summary->{slow};`
  - List of top hosts: `print "$_\n" for top_n($digest->{hosts}, $top);`
- Exit status 0 on success, non‑zero on errors.

#### Core logic
- Load host aliases from config file (`read_aliases`).
- Build exclusion hash from `@exclude_hosts`.
- Create `LogDigest` object with `level`.
- For each input file:
  - Read line by line.
  - Extract host (`split / /, $line, 2`).
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After all files, call `$digest->summarize`.
- Output summary in chosen format.

#### Subs
- `usage() -> string` – prints help text.
- `read_aliases($path) -> hash` – parses `key = value` lines, ignores comments/blank.
- `main() -> 0` – orchestrates parsing, digesting, and output.

#### Constants and configuration
- `$config_file` – `LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`.
- `$top` – 10.
- `$level` – 'WARN'.
- `$as_json` – 0.
- `@exclude_hosts` – empty array.

#### Dependencies
- `FindBin` – `$Bin`.
- `Getopt::Long` – `GetOptions`.
- `JSON::PP` – `new`, `canonical`, `encode`.
- `LogDigest` – `new`, `add`, `summarize`, `top_n`.

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` if file open fails.
