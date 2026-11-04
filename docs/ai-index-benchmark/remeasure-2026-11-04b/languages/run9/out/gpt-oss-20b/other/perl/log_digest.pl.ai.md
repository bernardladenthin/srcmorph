### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:48:18Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Reads web server logs and reports the busiest hosts and status‑code counts, optionally as JSON.

#### Purpose
- Command‑line tool for summarizing access logs.
- Outputs host usage statistics and slow request counts.

#### Type
- Script
- Uses package `LogDigest` (imported `top_n`).

#### Input
- Options:
  - `--top|n=i` → `$top` (default 10)
  - `--level=s` → `$level` (default 'WARN')
  - `--json!` → `$as_json` (default 0)
  - `--exclude-host=s` → `@exclude_hosts`
  - `--help|h` → prints usage
- Positional arguments: `FILE...` (log files)
- Environment:
  - `LOG_DIGEST_CONFIG` → `$config_file` (default `/etc/log-digest.conf`)
- Files read:
  - `$config_file` (host alias mapping)
  - Each `FILE` (log entries)

#### Output
- If `$as_json`:
  - JSON string of `$summary` hash, newline terminated.
- Else:
  - One line: `"<total> requests from <hosts> hosts, <slow> slow"`
  - Followed by `$top` lines of host statistics from `top_n`.
- Exit status: 0 on success, non‑zero on error.

#### Core logic
- Load host aliases from config file.
- Build exclusion set from `@exclude_hosts`.
- Create `LogDigest` object with `level`.
- For each log file:
  - Read line by line.
  - Extract host (first field).
  - Skip if host (or its alias) is excluded.
  - Feed line to `$digest->add`.
- After all files:
  - `$digest->summarize` → `$summary`.
  - Output JSON or formatted text as above.

#### Subs
- `usage() -> string` – prints help text.
- `read_aliases(path) -> hash` – parses `key = value` lines, ignores comments.
- `main() -> int` – orchestrates parsing, digesting, output.

#### Constants and configuration
- `$config_file = $ENV{LOG_DIGEST_CONFIG} // '/etc/log-digest.conf'`
- `$top = 10`
- `$level = 'WARN'`
- `$as_json = 0`
- `@exclude_hosts = ()`

#### Dependencies
- `FindBin` – `$Bin`
- `Getopt::Long` – `GetOptions`
- `JSON::PP` – `->new->canonical->encode`
- `LogDigest` – `new`, `add`, `summarize`, `top_n`

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` if a log file cannot be opened.
