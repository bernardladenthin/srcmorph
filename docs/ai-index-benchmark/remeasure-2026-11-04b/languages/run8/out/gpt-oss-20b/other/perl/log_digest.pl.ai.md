### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:47:22Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> A command‑line tool that parses web‑server access logs, filters hosts, and reports request counts and slow requests, optionally as JSON.

#### Purpose
- Parses access log files.
- Filters out excluded hosts.
- Reports total requests, unique hosts, slow requests.
- Outputs top N hosts by request count.

#### Type
- Script.
- No package; procedural.
- Exports none.

#### Input
- `--top|n=i` → `$top` (default 10).
- `--level=s` → `$level` (default 'WARN').
- `--json!` → `$as_json` (default 0).
- `--exclude-host=s` → `@exclude_hosts` (multiple allowed).
- `--help|h` → prints usage and exits.
- Positional arguments: `FILE...` (log files).
- Environment: `LOG_DIGEST_CONFIG` → `$config_file` (default `/etc/log-digest.conf`).
- Reads `$config_file` for host alias mappings (`from=to` lines).

#### Output
- If `$as_json`: JSON string of `$summary` hash.
- Else: plain text:
  - One line: `"<total> requests from <hosts> hosts, <slow> slow"`.
  - Followed by top N hosts, one per line (from `top_n`).
- Exit status 0 on success; non‑zero on errors.

#### Core logic
- Load host aliases from config file.
- Build exclusion hash from `@exclude_hosts`.
- Create `LogDigest` object with `level`.
- For each input file:
  - Read line by line.
  - Extract host (first token).
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After all files, call `$digest->summarize`.
- Output summary in chosen format.

#### Subs
- `usage() -> string` – returns help text.
- `read_aliases(path) -> hash` – parses alias file.
- `main() -> int` – orchestrates processing and output.

#### Constants and configuration
- `$config_file` – `LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`.
- `$top` – 10.
- `$level` – 'WARN'.
- `$as_json` – 0.
- `@exclude_hosts` – empty array.

#### Dependencies
- `FindBin` – `$Bin`.
- `Getopt::Long` – `GetOptions`.
- `JSON::PP` – `->new->canonical->encode`.
- `LogDigest` – `new`, `add`, `summarize`, `hosts`, `top_n`.

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` if file open fails.
