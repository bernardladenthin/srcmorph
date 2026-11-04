### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:46:23Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Reads web server access logs, filters hosts, and reports request counts and slow responses, optionally as JSON.

#### Purpose
- Script to analyze access logs.
- Outputs busiest hosts and status‑code statistics.

#### Type
- Script.
- Uses package `LogDigest` (imported `top_n`).

#### Input
- Command‑line options:
  - `--top|n=i` → `$top` (default `10`).
  - `--level=s` → `$level` (default `'WARN'`).
  - `--json!` → `$as_json` (default `0`).
  - `--exclude-host=s` → `@exclude_hosts` (multiple allowed).
  - `--help|h` → prints usage.
- Positional arguments: `FILE...` (log files).
- Environment variable: `LOG_DIGEST_CONFIG` → `$config_file` (default `'/etc/log-digest.conf'`).
- Reads alias file: each line `from = to`.

#### Output
- If `$as_json`: JSON string of `$summary` (canonical order) followed by newline.
- Else: plain text:
  - Summary line: `"%d requests from %d hosts, %d slow\n"`.
  - Busiest hosts: each on its own line, from `top_n($digest->{hosts}, $top)`.

#### Core logic
- Load host aliases from config file.
- Build exclusion set from `@exclude_hosts`.
- Create `LogDigest` object with `level`.
- For each input file:
  - Read line by line.
  - Extract host (first token).
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After all files:
  - `$digest->summarize` → `$summary`.
  - Output JSON or text as above.

#### Subs
- `usage() -> string`  
  Returns usage message.
- `read_aliases($path) -> hash`  
  Parses alias file into `%aliases`.
- `main() -> int`  
  Orchestrates parsing, digesting, and output.

#### Constants and configuration
- `$config_file` → `ENV{LOG_DIGEST_CONFIG}` or `'/etc/log-digest.conf'`.
- `$top` → `10`.
- `$level` → `'WARN'`.
- `$as_json` → `0`.
- `@exclude_hosts` → empty array.

#### Dependencies
- `FindBin` (`$Bin`).
- `Getopt::Long` (`GetOptions`).
- `JSON::PP` (`JSON::PP->new->canonical->encode`).
- `LogDigest` (`top_n`, `new`, `add`, `summarize`).

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` if file open fails.
