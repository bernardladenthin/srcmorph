### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:21:20Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Reads web server access logs, filters hosts, and reports request counts and slow responses, optionally as JSON.

#### Purpose
- Script to analyze access logs.
- Outputs busiest hosts and status‑code tallies.

#### Type
- Script.
- Uses `LogDigest` module; no exported symbols.

#### Input
- Options:
  - `--top|n=i` → `$top` (default 10).
  - `--level=s` → `$level` (default 'WARN').
  - `--json!` → `$as_json` (default 0).
  - `--exclude-host=s` → `@exclude_hosts` (multiple allowed).
  - `--help|h` → prints usage.
- Positional arguments: `FILE...` (log files).
- Environment:
  - `LOG_DIGEST_CONFIG` → `$config_file` (default `/etc/log-digest.conf`).
- Reads config file lines `from = to` for host aliases.

#### Output
- If `$as_json`: JSON string of `$summary` hash, newline terminated.
- Else: plain text:
  - `printf "%d requests from %d hosts, %d slow\n", $summary->{total}, $summary->{hosts}, $summary->{slow};`
  - List of top hosts via `top_n($digest->{hosts}, $top)`.

#### Core logic
- Load host aliases from config.
- Build `%excluded` hash from `@exclude_hosts`.
- Create `LogDigest` with `level`.
- For each input file:
  - Read line by line.
  - Extract `$host` (first token).
  - Skip if host (or its alias) in `%excluded`.
  - Pass line to `$digest->add`.
- After all files, call `$digest->summarize`.
- Output summary in chosen format.

#### Subs
- `usage() -> string`  
  Returns usage message.
- `read_aliases(path) -> hash`  
  Parses alias file into `%aliases`.
- `main() -> 0`  
  Orchestrates parsing, digesting, output.

#### Constants and configuration
- `$config_file = $ENV{LOG_DIGEST_CONFIG} // '/etc/log-digest.conf'`.
- `$top = 10`.
- `$level = 'WARN'`.
- `$as_json = 0`.
- `@exclude_hosts = ()`.

#### Dependencies
- `FindBin` – `$Bin`.
- `Getopt::Long` – `GetOptions`.
- `JSON::PP` – `new`, `canonical`, `encode`.
- `LogDigest` – `new`, `add`, `summarize`, `hosts`, `top_n`.

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` on file open failure.
