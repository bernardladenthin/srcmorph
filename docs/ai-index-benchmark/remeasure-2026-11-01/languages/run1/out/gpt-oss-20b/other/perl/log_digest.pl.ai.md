### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:17:27Z
- G: 
- A: 0.0.0
- X: file
---
> Reads access logs and prints the busiest hosts and the status‑code counts, as text or JSON.

#### Purpose
- Command‑line tool for summarizing web server logs.
- Outputs host request counts and slow request statistics.

#### Type
- Script
- Uses `LogDigest` module; no exported symbols.

#### Input
- Options:
  - `--top|n=i` → `$top` (default 10)
  - `--level=s` → `$level` (default 'WARN')
  - `--json!` → `$as_json` (default 0)
  - `--exclude-host=s` → `@exclude_hosts` (multiple)
  - `--help|h` → prints usage
- Positional arguments: `FILE...` (log files)
- Environment: `$ENV{LOG_DIGEST_CONFIG}` (default `/etc/log-digest.conf`)
- Reads config file for host aliases (`key = value` lines)

#### Output
- Text mode: prints summary line and top hosts list.
- JSON mode: prints JSON object of summary.
- Exit status 0 on success; dies on errors.

#### Core logic
- Load host aliases from config file.
- Build exclusion hash from `@exclude_hosts`.
- Create `LogDigest` object with `level`.
- For each input file:
  - Read line by line.
  - Extract host (first field).
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After processing, call `$digest->summarize`.
- Output summary in chosen format.
- Return 0.

#### Subs
- `usage() -> string` – prints help text.
- `read_aliases($path) -> hash` – parses alias file.
- `main() -> int` – orchestrates processing.

#### Constants and configuration
- `$config_file` – path from `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- `$top` – number of top hosts to display (default 10).
- `$level` – log level for `LogDigest` (default 'WARN').
- `$as_json` – flag for JSON output (default 0).
- `@exclude_hosts` – list of hosts to exclude.

#### Dependencies
- `FindBin` – `$Bin`
- `Getopt::Long` – `GetOptions`
- `JSON::PP` – `new`, `canonical`, `encode`
- `LogDigest` – `new`, `add`, `summarize`, `hosts` (accessed as `$digest->{hosts}`)

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` if file cannot be opened.
