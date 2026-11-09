### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:59:31Z
- G: 
- A: 0.0.0
- X: file
- P: 4F9ECAEF
---
> Generates a summary of web‑server access logs, listing the busiest hosts and status‑code counts, optionally in JSON.

#### Purpose
- Script to analyze access logs.
- Outputs busiest hosts and request statistics.

#### Type
- Script.
- No package; procedural.

#### Input
- Command‑line options:
  - `--top|n=i` → `$top` (default 10)
  - `--level=s` → `$level` (default 'WARN')
  - `--json!` → `$as_json` (default 0)
  - `--exclude-host=s` → `@exclude_hosts`
  - `--help|h` → prints usage
- Positional arguments: `FILE...` (log files)
- Environment variable: `LOG_DIGEST_CONFIG` → `$config_file` (default `/etc/log-digest.conf`)
- Reads alias file: `$config_file`
- Reads each log file line by line.

#### Output
- If `$as_json`: JSON string of `$summary` followed by newline.
- Else: plain text:
  - One line: `"<total> requests from <hosts> hosts, <slow> slow"`
  - Followed by `$top` lines from `top_n($digest->{hosts}, $top)`
- Exit status 0 on success; non‑zero on errors.

#### Core logic
- Load host aliases from config file.
- Build exclusion set from `@exclude_hosts`.
- Create `LogDigest` object with `level`.
- For each input file:
  - Read each line.
  - Extract host (first token).
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After all files, call `$digest->summarize`.
- Output summary in chosen format.

#### Subs
- `usage() -> string` – returns usage text.
- `read_aliases(path) -> hash` – parses `key = value` pairs, ignores comments/blank lines.
- `main() -> int` – orchestrates processing, returns exit code.

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
- `die "cannot read $file: $!\n"` if a log file cannot be opened.
