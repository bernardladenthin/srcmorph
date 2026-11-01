### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:35:35Z
- G: 
- A: 0.0.0
- X: file
---
> Reads access logs and prints the busiest hosts and the status‑code counts, as text or JSON.

#### Purpose
- Script to analyze web server logs.
- Outputs host usage statistics and status‑code distribution.

#### Type
- Script.
- Uses `LogDigest` module; no exported symbols.

#### Input
- Command‑line options:
  - `--top|n=i` → `$top` (default `10`).
  - `--level=s` → `$level` (default `'WARN'`).
  - `--json!` → `$as_json` (default `0`).
  - `--exclude-host=s` → `@exclude_hosts` (multiple allowed).
  - `--help|h` → prints usage and exits.
- Positional arguments: `FILE...` (log files to read).
- Environment variable: `LOG_DIGEST_CONFIG` (default `'/etc/log-digest.conf'`).
- Config file format: `from = to` lines; comments start with `#`.

#### Output
- If `$as_json`: JSON string of `$summary` followed by newline.
- Else: plain text:
  - One line: `"<total> requests from <hosts> hosts, <slow> slow"`.
  - Followed by top `$top` hosts, one per line, from `top_n($digest->{hosts}, $top)`.
- Exit status `0` on success; non‑zero on errors.

#### Core logic
- Load host aliases from config file.
- Build exclusion set from `@exclude_hosts`.
- Create `LogDigest` object with `level => $level`.
- For each input file:
  - Read line by line.
  - Extract host (first field).
  - Skip if host (or its alias) is in exclusion set.
  - Pass line to `$digest->add`.
- After all files:
  - `$summary = $digest->summarize`.
  - Output in JSON or text as described.

#### Subs
- `usage() -> string`  
  Returns usage message.
- `read_aliases($path) -> hash`  
  Parses alias file into `%aliases`.
- `main() -> 0`  
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
- `LogDigest` (`top_n`, constructor, `add`, `summarize`).

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` if file open fails.
