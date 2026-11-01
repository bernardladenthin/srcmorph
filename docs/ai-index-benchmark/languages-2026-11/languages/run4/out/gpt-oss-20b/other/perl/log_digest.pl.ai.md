### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:29:03Z
- G: 
- A: 0.0.0
- X: file
---
> Reads web server logs and reports the busiest hosts and status‑code counts, optionally as JSON.

#### Purpose
- Command‑line tool for summarizing access logs.
- Outputs host request counts and slow request totals.

#### Type
- Script (`log_digest.pl`).
- Uses `LogDigest` module; no exported symbols.

#### Input
- Options:
  - `--top|n=i` → `$top` (default `10`).
  - `--level=s` → `$level` (default `'WARN'`).
  - `--json!` → `$as_json` (default `0`).
  - `--exclude-host=s` → `@exclude_hosts` (multiple allowed).
  - `--help|h` → prints usage.
- Positional arguments: `FILE...` (log files).
- Environment: `$ENV{LOG_DIGEST_CONFIG}` (default `'/etc/log-digest.conf'`).
- Reads config file for host aliases (`HOST=ALIAS` lines).

#### Output
- If `$as_json`: JSON string of `$summary` hash, newline terminated.
- Else: plain text:
  - One line: `"%d requests from %d hosts, %d slow"`.
  - Followed by `$top` lines from `top_n($digest->{hosts}, $top)`.
- Exit status `0` on success; non‑zero on errors.

#### Core logic
- Parse options; validate `$top > 0`.
- Load host aliases via `read_aliases`.
- Build `%excluded` hash from `@exclude_hosts`.
- Create `LogDigest` instance with `level => $level`.
- For each input file:
  - Open file; read line by line.
  - Extract `$host` (first field).
  - Skip if host (or its alias) is in `%excluded`.
  - Pass raw `$line` to `$digest->add`.
- After all files:
  - `$summary = $digest->summarize`.
  - Output JSON or formatted text as described.

#### Subs
- `usage() -> string`  
  Returns usage message.
- `read_aliases($path) -> hash`  
  Reads alias file into `%aliases`.
- `main() -> int`  
  Orchestrates parsing, digesting, and output.

#### Constants and configuration
- `$config_file` → `$ENV{LOG_DIGEST_CONFIG}` or `'/etc/log-digest.conf'`.
- `$top` → `10`.
- `$level` → `'WARN'`.
- `$as_json` → `0`.
- `@exclude_hosts` → empty array.

#### Dependencies
- `FindBin` (`$Bin`).
- `Getopt::Long` (`GetOptions`).
- `JSON::PP` (`->new->canonical->encode`).
- `LogDigest` (`top_n`, `new`, `add`, `summarize`).

#### Exceptions / Errors
- `die usage()` on option parse failure.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` if a log file cannot be opened.
