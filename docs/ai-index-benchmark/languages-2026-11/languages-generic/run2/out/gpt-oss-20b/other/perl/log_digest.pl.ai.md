### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:34:09Z
- G: 
- A: 0.0.0
- X: file
---
> Parses web server logs, filters hosts, and reports request counts and slow responses, optionally as JSON.

#### Purpose
- Extracts host and status‑code statistics from access logs.
- Supports host aliasing and host exclusion.
- Outputs summary and top hosts.

#### Type
- Perl script (module + executable). Uses `LogDigest` class.

#### Input
- Command‑line options: `--top N`, `--level LEVEL`, `--json`, `--exclude-host HOST`.
- Log files passed as positional arguments.
- Optional config file (`LOG_DIGEST_CONFIG` env var or `/etc/log-digest.conf`) for host aliases.

#### Output
- Text summary: total requests, distinct hosts, slow requests.
- List of top N hosts by request count.
- JSON object when `--json` is set.

#### Core logic
- Parse options; validate `--top`.
- `read_aliases`: read `key = value` pairs from config file into `%aliases`.
- `main`:
  - Load aliases; build `%excluded` hash from `@exclude_hosts`.
  - Create `LogDigest` instance with `level`.
  - For each input file:
    - Read line by line.
    - Extract host (first field).
    - Skip if host (or its alias) is in `%excluded`.
    - Pass line to `$digest->add`.
  - After all files, call `$digest->summarize`.
  - Print summary and top hosts, JSON‑encoded if requested.

#### Public API
- `usage() -> string`  
  Provides command‑line help text.
- `read_aliases(path) -> hash`  
  Reads alias mapping from config file.
- `main() -> int`  
  Entry point; processes files and prints results.

#### Constants and configuration
- `$config_file` – `LOG_DIGEST_CONFIG` env var or `/etc/log-digest.conf`.
- `$top` – default `10`.
- `$level` – default `'WARN'`.
- `$as_json` – default `0`.
- `@exclude_hosts` – empty array.

#### Dependencies
- `FindBin`, `Getopt::Long`, `JSON::PP`, `LogDigest`.
- `LogDigest->new(level => $level)`, `add`, `summarize`, `top_n`.

#### Exceptions / Errors
- `die "--top must be positive\n"` if `--top` < 1.
- `die usage()` on option parsing failure.
- `die "cannot read $file: $!\n"` if a log file cannot be opened.
