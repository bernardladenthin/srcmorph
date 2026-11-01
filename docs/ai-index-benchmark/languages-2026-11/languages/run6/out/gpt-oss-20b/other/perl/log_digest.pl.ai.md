### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:39:03Z
- G: 
- A: 0.0.0
- X: file
---
> Reads access logs and prints the busiest hosts and status‑code counts, as text or JSON.

#### Purpose
- Command‑line tool to analyze web server logs.
- Outputs host usage statistics and status‑code tallies.

#### Type
- Script.
- Uses `LogDigest` module; no exported symbols.

#### Input
- Options:
  - `--top|n=i` → `$top` (default `10`).
  - `--level=s` → `$level` (default `'WARN'`).
  - `--json!` → `$as_json` (default `0`).
  - `--exclude-host=s` → `@exclude_hosts` (multiple allowed).
  - `--help|h` → prints usage.
- Positional arguments: list of log files (`@ARGV`).
- Environment:
  - `LOG_DIGEST_CONFIG` → `$config_file` (default `'/etc/log-digest.conf'`).
- Reads config file for host aliases (`read_aliases`).

#### Output
- If `$as_json`:
  - JSON string of `$summary` (canonical order) followed by newline.
- Else:
  - One line: `"<total> requests from <hosts> hosts, <slow> slow"`.
  - Followed by top `$top` hosts, one per line (`top_n` output).
- Exit status: `0` on success, non‑zero on errors.

#### Core logic
- Load host aliases from config file.
- Build exclusion hash from `@exclude_hosts`.
- Create `LogDigest` instance with `level`.
- For each input file:
  - Read line by line.
  - Extract host (first field).
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After all files:
  - `$digest->summarize` → `$summary`.
  - Output JSON or formatted text and top hosts.

#### Subs
- `usage() -> string` – prints help text.
- `read_aliases($path) -> %aliases` – parses `key = value` pairs, ignores comments/blank lines.
- `main() -> 0` – orchestrates parsing, digesting, and output.

#### Constants and configuration
- `$config_file` – `LOG_DIGEST_CONFIG` or `'/etc/log-digest.conf'`.
- `$top` – `10`.
- `$level` – `'WARN'`.
- `$as_json` – `0`.
- `@exclude_hosts` – empty array.

#### Dependencies
- `FindBin` – `$Bin`.
- `Getopt::Long` – `GetOptions`.
- `JSON::PP` – `new`, `canonical`, `encode`.
- `LogDigest` – `new`, `add`, `summarize`, `top_n`.

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` if file cannot be opened.
