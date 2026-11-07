### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:14:37Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Reads web server logs, aggregates request counts per host, reports the busiest hosts and status‑code statistics, optionally outputting JSON.

#### Purpose
- Command‑line tool to analyze access logs.
- Produces host‑level request summaries and status‑code tallies.

#### Type
- Script.
- No package; procedural.
- Exports none.

#### Input
- Options:
  - `--top|n=i` → `$top` (default `10`).
  - `--level=s` → `$level` (default `'WARN'`).
  - `--json!` → `$as_json` (default `0`).
  - `--exclude-host=s` → `@exclude_hosts` (multiple allowed).
  - `--help|h` → prints usage.
- Positional arguments: `FILE...` (log files).
- Environment:
  - `LOG_DIGEST_CONFIG` → `$config_file` (default `'/etc/log-digest.conf'`).
- Files read:
  - `$config_file` (alias mapping).
  - Each `FILE` from `@ARGV`.

#### Output
- If `$as_json`: JSON string of `$summary` (canonical order) followed by newline.
- Else: plain text:
  - One line: `"<total> requests from <hosts> hosts, <slow> slow"`.
  - Followed by `<top>` lines of host summaries from `top_n`.
- Exit status: `0` on success, non‑zero on error.

#### Core logic
- Load host aliases from config file into `%aliases`.
- Build `%excluded` hash from `@exclude_hosts`.
- Create `LogDigest` object with `level => $level`.
- For each input file:
  - Read line by line.
  - Extract `$host` (first field).
  - Skip if `$host` (or its alias) is in `%excluded`.
  - Pass raw log line to `$digest->add`.
- After all files:
  - `$summary = $digest->summarize`.
  - Output JSON or formatted text as described.

#### Subs
- `usage() -> string`  
  Returns usage message.
- `read_aliases($path) -> %aliases`  
  Parses `key = value` lines, ignores comments/blank.
- `main() -> 0`  
  Orchestrates parsing, digesting, and output.

#### Constants and configuration
- `$config_file = $ENV{LOG_DIGEST_CONFIG} // '/etc/log-digest.conf'`.
- `$top = 10`.
- `$level = 'WARN'`.
- `$as_json = 0`.
- `@exclude_hosts = ()`.

#### Dependencies
- `FindBin` – `$Bin`.
- `Getopt::Long` – `GetOptions`.
- `JSON::PP` – `->new->canonical->encode`.
- `LogDigest` – `new`, `add`, `summarize`, `hosts` (hash), `top_n`.

#### Exceptions / Errors
- `die usage()` if `GetOptions` fails.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` when opening a log file fails.
