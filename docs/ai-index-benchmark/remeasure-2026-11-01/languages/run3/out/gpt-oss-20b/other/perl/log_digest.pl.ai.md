### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:57:22Z
- G: 
- A: 0.0.0
- X: file
---
> Reads web server logs and reports the busiest hosts and status‑code counts, optionally as JSON.

#### Purpose
- Command‑line tool for summarizing access logs.
- Outputs host usage statistics and slow request counts.

#### Type
- Script (`log_digest.pl`).
- Uses `LogDigest` module for core analysis.

#### Input
- Options:
  - `--top|n=i` → `$top` (default `10`).
  - `--level=s` → `$level` (default `'WARN'`).
  - `--json!` → `$as_json` (default `0`).
  - `--exclude-host=s` → `@exclude_hosts` (can repeat).
  - `--help|h` → prints usage and exits.
- Positional arguments: list of log files (`@ARGV`).
- Environment: `LOG_DIGEST_CONFIG` (default `'/etc/log-digest.conf'`).
- Config file: key‑value pairs `from = to` for host aliases.

#### Output
- If `$as_json`:
  - JSON string of `$summary` hash, newline‑terminated.
- Else:
  - One line: `"<total> requests from <hosts> hosts, <slow> slow"`.
  - Followed by `$top` lines of host statistics from `top_n`.
- Exit status: `0` on success, non‑zero on errors.

#### Core logic
- Load host aliases from config file.
- Build exclusion set from `@exclude_hosts`.
- Create `LogDigest` object with specified `level`.
- For each input file:
  - Read line by line.
  - Extract host from first field.
  - Skip if host (or its alias) is excluded.
  - Feed line to `$digest->add`.
- After all files:
  - `$digest->summarize` → `$summary`.
  - Output JSON or formatted text.
  - Print top hosts via `top_n`.

#### Subs
- `usage() -> string`  
  Returns help text.
- `read_aliases($path) -> hash`  
  Parses alias file into `%aliases`.
- `main() -> 0`  
  Orchestrates parsing, digesting, and output.

#### Constants and configuration
- `$config_file` → `ENV{LOG_DIGEST_CONFIG}` or `'/etc/log-digest.conf'`.
- `$top` → `10`.
- `$level` → `'WARN'`.
- `$as_json` → `0`.
- `@exclude_hosts` → empty array.

#### Dependencies
- `FindBin` – `$Bin`.
- `Getopt::Long` – `GetOptions`.
- `JSON::PP` – `new->canonical->encode`.
- `LogDigest` – `new`, `add`, `summarize`, `hosts` (used by `top_n`).

#### Exceptions / Errors
- `die "--top must be positive\n"` if `$top < 1`.
- `die usage()` on bad options.
- `die "cannot read $file: $!\n"` if a log file cannot be opened.
