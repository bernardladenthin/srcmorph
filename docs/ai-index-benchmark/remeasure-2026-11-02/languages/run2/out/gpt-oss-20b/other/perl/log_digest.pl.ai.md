### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:41:41Z
- G: 
- A: 0.0.0
- X: file
- P: B953BC65
---
> Aggregates web server logs to report request counts, host activity, and status‑code distribution, optionally outputting JSON.

#### Purpose
- Script that reads access log files.
- Reports busiest hosts and status‑code counts.

#### Type
- Script.
- No package; procedural.

#### Input
- `--top|n=i` → `$top` (default 10).
- `--level=s` → `$level` (default 'WARN').
- `--json!` → `$as_json` (default 0).
- `--exclude-host=s` → `@exclude_hosts` (multiple allowed).
- `--help|h` → prints usage and exits.
- Positional arguments: list of log files (`@ARGV`).
- Environment: `LOG_DIGEST_CONFIG` (default `/etc/log-digest.conf`).

#### Output
- If `$as_json`: JSON string of `$summary` followed by newline.
- Else: plain text line with totals, then each host line from `top_n`.
- Exit status 0 on success; dies on errors.

#### Core logic
- Load host aliases from config file (`read_aliases`).
- Build exclusion hash from `@exclude_hosts`.
- Create `LogDigest` object with `level`.
- For each input file:
  - Read line by line.
  - Extract host (first field).
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After all files, call `$digest->summarize`.
- Output summary in chosen format.

#### Subs
- `usage() -> string` – prints usage text.
- `read_aliases($path) -> %aliases` – parses `key = value` lines.
- `main() -> 0` – orchestrates processing and output.

#### Constants and configuration
- `$config_file` – `LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`.
- `$top` – 10.
- `$level` – 'WARN'.
- `$as_json` – 0.
- `@exclude_hosts` – empty array.

#### Dependencies
- `FindBin` – `$Bin`.
- `Getopt::Long` – `GetOptions`.
- `JSON::PP` – `new`, `canonical`, `encode`.
- `LogDigest` – `new`, `add`, `summarize`, `hosts` (hash), `top_n` (imported).

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` if file open fails.
