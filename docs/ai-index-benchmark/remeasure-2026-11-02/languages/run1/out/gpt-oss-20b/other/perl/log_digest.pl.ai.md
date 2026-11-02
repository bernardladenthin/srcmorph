### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:25:18Z
- G: 
- A: 0.0.0
- X: file
- P: B953BC65
---
> Prints the busiest hosts and status‑code counts from web access logs, optionally as JSON.

#### Purpose
- Command‑line tool to analyze log files.
- Outputs host activity summary and top hosts.

#### Type
- Script.
- Uses `LogDigest` module; no exported symbols.

#### Input
- Options:
  - `--top|n=i` → `$top` (default 10)
  - `--level=s` → `$level` (default 'WARN')
  - `--json!` → `$as_json` (default 0)
  - `--exclude-host=s` → `@exclude_hosts` (multiple)
  - `--help|h` → prints usage
- Positional arguments: list of log files (`@ARGV`).
- Environment: `$ENV{LOG_DIGEST_CONFIG}` (default `/etc/log-digest.conf`).
- Reads alias file: each line `from = to`.

#### Output
- If `$as_json`: JSON string of `$summary` hash, newline‑terminated.
- Else: plain text:
  - Summary line: `printf "%d requests from %d hosts, %d slow\n"`.
  - Top hosts list: one per line from `top_n($digest->{hosts}, $top)`.
- Exit status 0 on success; non‑zero on errors.

#### Core logic
- Load host aliases from config file.
- Build exclusion set from `@exclude_hosts`.
- Create `LogDigest` object with `level`.
- For each input file:
  - Read line by line.
  - Extract host (first token).
  - Skip if host (or its alias) is excluded.
  - Pass raw line to `$digest->add`.
- After all files, call `$digest->summarize`.
- Output summary and top hosts in chosen format.

#### Subs
- `usage() -> string`  
  Returns usage message.
- `read_aliases($path) -> hash`  
  Parses alias file into `%aliases`.
- `main() -> int`  
  Orchestrates parsing, digesting, and output.

#### Constants and configuration
- `$config_file` → `$ENV{LOG_DIGEST_CONFIG}` or `'/etc/log-digest.conf'`.
- `$top` → 10.
- `$level` → 'WARN'.
- `$as_json` → 0.
- `@exclude_hosts` → empty array.

#### Dependencies
- `FindBin` – `$Bin`.
- `Getopt::Long` – `GetOptions`.
- `JSON::PP` – `->new->canonical->encode`.
- `LogDigest` – `new`, `add`, `summarize`, `hosts` (accessed as `$digest->{hosts}`).
- `strict`, `warnings`.

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` if file open fails.
