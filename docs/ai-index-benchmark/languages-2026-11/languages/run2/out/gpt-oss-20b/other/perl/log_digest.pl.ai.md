### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:34:28Z
- G: 
- A: 0.0.0
- X: file
---
> Reads access logs and prints the busiest hosts and the status‑code counts, as text or JSON.

#### Purpose
- Command‑line tool to analyze web server logs.
- Outputs host request counts and slow request statistics.

#### Type
- Script
- Uses `LogDigest` module; no package defined.

#### Input
- Options:
  - `--top|n=i` → `$top` (default `10`)
  - `--level=s` → `$level` (default `'WARN'`)
  - `--json!` → `$as_json` (default `0`)
  - `--exclude-host=s` → `@exclude_hosts` (multiple allowed)
  - `--help|h` → prints usage
- Positional arguments: list of log files (`@ARGV`)
- Environment:
  - `LOG_DIGEST_CONFIG` → `$config_file` (default `'/etc/log-digest.conf'`)

#### Output
- If `$as_json`: JSON string of `$summary` hash, newline terminated.
- Else: plain text:
  - One line: `"<total> requests from <hosts> hosts, <slow> slow"`
  - Followed by top N hosts, one per line (`top_n` output).
- Exit status: `0` on success, non‑zero on error.

#### Core logic
- Load host aliases from config file (`read_aliases`).
- Build exclusion hash from `@exclude_hosts`.
- Create `LogDigest` object with `level`.
- For each input file:
  - Read line by line.
  - Extract host from first field.
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After all files, call `$digest->summarize`.
- Output results in chosen format.

#### Subs
- `usage() -> string` – prints help text.
- `read_aliases($path) -> %aliases` – parses `key = value` pairs, ignores comments.
- `main() -> 0` – orchestrates parsing, digesting, and output.

#### Constants and configuration
- `$config_file` – path from `LOG_DIGEST_CONFIG` or `'/etc/log-digest.conf'`.
- `$top` – number of top hosts to display (default `10`).
- `$level` – log level for `LogDigest` (default `'WARN'`).
- `$as_json` – flag for JSON output (default `0`).
- `@exclude_hosts` – list of hosts to skip.

#### Dependencies
- `FindBin` – `$Bin` for lib path.
- `Getopt::Long` – `GetOptions`.
- `JSON::PP` – `->new->canonical->encode`.
- `LogDigest` – `top_n`, `new`, `add`, `summarize`.

#### Exceptions / Errors
- `die usage()` if `GetOptions` fails.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` when opening a log file fails.
