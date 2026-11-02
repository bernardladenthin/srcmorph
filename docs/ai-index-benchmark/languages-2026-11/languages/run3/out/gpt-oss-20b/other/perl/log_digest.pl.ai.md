### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:55:44Z
- G: 
- A: 0.0.0
- X: file
---
> Reads access logs and prints the busiest hosts and status‑code counts, as text or JSON.

#### Purpose
- Script to analyze web server access logs.
- Outputs busiest hosts and status‑code statistics.

#### Type
- Script.
- Uses `LogDigest` module; no exported symbols.

#### Input
- Command‑line options:
  - `--top|n=i` → `$top` (default `10`).
  - `--level=s` → `$level` (default `'WARN'`).
  - `--json!` → `$as_json` (default `0`).
  - `--exclude-host=s` → `@exclude_hosts` (multiple allowed).
  - `--help|h` → prints usage.
- Positional arguments: list of log files (`@ARGV`).
- Environment variable: `LOG_DIGEST_CONFIG` (default `'/etc/log-digest.conf'`).
- Configuration file: reads host alias mappings (`from = to`).

#### Output
- If `$as_json`: JSON string of `$summary` hash.
- Else: plain text:
  - Summary line: `printf "%d requests from %d hosts, %d slow\n"`.
  - Top hosts list: one per line from `top_n($digest->{hosts}, $top)`.
- Exit status: `0` on success, non‑zero on errors.

#### Core logic
- Load host aliases from config file.
- Build exclusion set from `@exclude_hosts`.
- Create `LogDigest` object with `level`.
- For each input file:
  - Read line by line.
  - Extract host (first field).
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After all files, call `$digest->summarize`.
- Output results in requested format.

#### Subs
- `usage() -> string` – returns usage message.
- `read_aliases($path) -> hash` – parses alias file.
- `main() -> int` – orchestrates processing; returns exit code.

#### Constants and configuration
- `$config_file` – `LOG_DIGEST_CONFIG` or `'/etc/log-digest.conf'`.
- `$top` – default `10`.
- `$level` – default `'WARN'`.
- `$as_json` – default `0`.
- `@exclude_hosts` – empty array.

#### Dependencies
- `FindBin` – `$Bin`.
- `Getopt::Long` – `GetOptions`.
- `JSON::PP` – `->new->canonical->encode`.
- `LogDigest` – `top_n`, `new`, `add`, `summarize`.

#### Exceptions / Errors
- `die usage()` on invalid options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` if a log file cannot be opened.
