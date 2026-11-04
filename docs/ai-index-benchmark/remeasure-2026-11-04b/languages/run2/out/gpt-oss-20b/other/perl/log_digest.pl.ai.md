### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:17:59Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Reads web server logs, filters hosts, and reports request counts and slow requests, optionally as JSON.

#### Purpose
- Script to analyze access logs and report busiest hosts and status‑code counts.

#### Type
- Script; no package; procedural.

#### Input
- `--top|n=i` → `$top` (default 10)
- `--level=s` → `$level` (default 'WARN')
- `--json!` → `$as_json` (default 0)
- `--exclude-host=s` → `@exclude_hosts` (multiple)
- `--help|h` → prints usage
- `@ARGV` → list of log files
- `%ENV{LOG_DIGEST_CONFIG}` → `$config_file` (default '/etc/log-digest.conf')
- Config file lines `from = to` → `%aliases`

#### Output
- If `$as_json`: JSON string of `$summary` followed by newline.
- Else: text line `"%d requests from %d hosts, %d slow"` then top hosts list.
- Exit status 0 on success, non‑zero on error.

#### Core logic
- Load host aliases from `$config_file`.
- Build `%excluded` from `@exclude_hosts`.
- Create `LogDigest` with `level => $level`.
- For each file in `@ARGV`:
  - Read each line, split host, skip if host (or alias) in `%excluded`.
  - Pass line to `$digest->add`.
- After all files, call `$digest->summarize`.
- Output summary in requested format.

#### Subs
- `usage() -> string` – prints usage text.
- `read_aliases($path) -> %aliases` – parses alias file.
- `main() -> 0` – orchestrates processing.

#### Constants and configuration
- `$config_file = $ENV{LOG_DIGEST_CONFIG} // '/etc/log-digest.conf'`
- `$top = 10`
- `$level = 'WARN'`
- `$as_json = 0`
- `@exclude_hosts = ()`

#### Dependencies
- `FindBin` – `$Bin`
- `Getopt::Long` – `GetOptions`
- `JSON::PP` – `->new->canonical->encode`
- `LogDigest` – `new`, `add`, `summarize`, `top_n`

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` on file open failure.
