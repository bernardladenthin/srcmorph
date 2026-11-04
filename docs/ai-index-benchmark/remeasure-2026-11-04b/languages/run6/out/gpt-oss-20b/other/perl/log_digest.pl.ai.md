### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:45:33Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Aggregates web server logs to report busiest hosts and status‑code counts, optionally outputting JSON.

#### Purpose
- Script to analyze access logs.
- Outputs busiest hosts and status‑code statistics.

#### Type
- Script.
- No package; procedural.

#### Input
- Options:
  - `--top|n=i` → `$top` (default 10).
  - `--level=s` → `$level` (default 'WARN').
  - `--json!` → `$as_json` (default 0).
  - `--exclude-host=s` → `@exclude_hosts`.
  - `--help|h` → prints usage.
- Positional arguments: `FILE...` (log files).
- Environment: `LOG_DIGEST_CONFIG` (default `/etc/log-digest.conf`).
- Reads config file for host aliases (`HOST=ALIAS` lines).

#### Output
- If `$as_json`: JSON string of `$summary` hash.
- Else: plain text:
  - Summary line: `printf "%d requests from %d hosts, %d slow\n"`.
  - List of top hosts: one per line from `top_n($digest->{hosts}, $top)`.

#### Core logic
- Load host aliases from config file.
- Build exclusion set from `@exclude_hosts`.
- Create `LogDigest` instance with `level`.
- For each input file:
  - Read line by line.
  - Extract host (first token).
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After all files:
  - `$digest->summarize` → `$summary`.
  - Output JSON or text as above.

#### Subs
- `usage() -> string` – prints help text.
- `read_aliases(PATH) -> hash` – parses alias file.
- `main() -> 0` – orchestrates processing.

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
- `LogDigest` – `new`, `add`, `summarize`, `top_n`.

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` on file open failure.
