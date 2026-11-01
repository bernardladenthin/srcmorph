### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:40:18Z
- G: 
- A: 0.0.0
- X: file
---
> Reads access logs and prints the busiest hosts and status‑code counts, as text or JSON.

#### Purpose
- Command‑line tool to analyze web server logs.
- Outputs host usage statistics and slow request counts.

#### Type
- Script
- Uses `LogDigest` module (object‑oriented).

#### Input
- Options:
  - `--top|n=i` → `$top` (default 10)
  - `--level=s` → `$level` (default 'WARN')
  - `--json!` → `$as_json` (default 0)
  - `--exclude-host=s` → `@exclude_hosts` (multiple)
  - `--help|h` → prints usage
- Positional arguments: `FILE...` (log files)
- Environment:
  - `LOG_DIGEST_CONFIG` → `$config_file` (default `/etc/log-digest.conf`)
- Files:
  - `$config_file` (alias mapping)
  - Each `FILE` (log entries)

#### Output
- If `$as_json`: JSON string of `$summary` followed by newline.
- Else: Text line `"%d requests from %d hosts, %d slow"` and top hosts list.
- Exit status: 0 on success, non‑zero on errors.

#### Core logic
- Load host aliases from config file.
- Build exclusion set from `@exclude_hosts`.
- Create `LogDigest` object with `level`.
- For each log file:
  - Read line by line.
  - Extract host (first field).
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After all files:
  - `$digest->summarize` → `$summary`.
  - Output JSON or formatted text.
  - Print top hosts via `top_n`.

#### Subs
- `usage() -> string` – prints help text.
- `read_aliases(PATH) -> hash` – parses `key = value` lines.
- `main() -> 0` – orchestrates processing.

#### Constants and configuration
- `$config_file` – `LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`.
- `$top` – 10.
- `$level` – 'WARN'.
- `$as_json` – 0.
- `@exclude_hosts` – empty array.

#### Dependencies
- `FindBin` – `$Bin`.
- `Getopt::Long` – `GetOptions`.
- `JSON::PP` – `encode`.
- `LogDigest` – `new`, `add`, `summarize`, `hosts` (public), `top_n` (exported).

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` if file open fails.
