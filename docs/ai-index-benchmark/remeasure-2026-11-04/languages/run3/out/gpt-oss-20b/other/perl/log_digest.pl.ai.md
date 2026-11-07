### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:05:38Z
- G: 
- A: 0.0.0
- X: file
- P: 4F9ECAEF
---
> Reads web server access logs, filters hosts, and outputs the busiest hosts and status‑code statistics in text or JSON.

#### Purpose
- Script to analyze access logs and report host activity and status‑code counts.

#### Type
- Script; no package; procedural.

#### Input
- Command‑line options:
  - `--top|n=i` → `$top` (default 10)
  - `--level=s` → `$level` (default `'WARN'`)
  - `--json!` → `$as_json` (default 0)
  - `--exclude-host=s` → `@exclude_hosts` (multiple allowed)
  - `--help|h` → prints usage
- Positional arguments: list of log files (`@ARGV`)
- Environment variable: `LOG_DIGEST_CONFIG` (default `'/etc/log-digest.conf'`)
- Configuration file: reads alias mappings (`from = to`) lines, ignores comments/blank lines.

#### Output
- If `$as_json`: JSON string of `$summary` hash, newline‑terminated.
- Else: plain text:
  - One line: `"<total> requests from <hosts> hosts, <slow> slow"`
  - Followed by `<top>` lines of host summaries from `top_n`.
- Exit status 0 on success; non‑zero on errors (e.g., file read failure, invalid `--top`).

#### Core logic
- Load host aliases from config file.
- Build exclusion set from `@exclude_hosts`.
- Create `LogDigest` object with `level`.
- For each input file:
  - Read line by line.
  - Extract host (first field).
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After processing all files, call `$digest->summarize`.
- Output results in chosen format.

#### Subs
- `usage() -> string` – returns usage message.
- `read_aliases($path) -> hash` – parses alias file into `%aliases`.
- `main() -> int` – orchestrates parsing, digesting, and output.

#### Constants and configuration
- `$config_file = $ENV{LOG_DIGEST_CONFIG} // '/etc/log-digest.conf'`
- `$top = 10`
- `$level = 'WARN'`
- `$as_json = 0`
- `@exclude_hosts = ()`

#### Dependencies
- `FindBin qw($Bin)`
- `Getopt::Long qw(GetOptions)`
- `JSON::PP` (used for JSON encoding)
- `LogDigest` (provides `new`, `add`, `summarize`; also exported `top_n`)

#### Exceptions / Errors
- `die usage()` if `GetOptions` fails.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` when opening a log file fails.
