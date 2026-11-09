### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:44:27Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Reads web server logs and reports the busiest hosts and status‑code counts, optionally as JSON.

#### Purpose
- Command‑line tool for summarizing access logs.
- Outputs host usage statistics and slow request counts.

#### Type
- Script.
- No package; procedural.
- Exports none.

#### Input
- Options:
  - `--top|n=i` → `$top` (default 10).
  - `--level=s` → `$level` (default 'WARN').
  - `--json!` → `$as_json` (default 0).
  - `--exclude-host=s` → `@exclude_hosts` (multiple allowed).
  - `--help|h` → prints usage.
- Positional arguments: `FILE...` (log files).
- Environment: `LOG_DIGEST_CONFIG` → `$config_file` (default `/etc/log-digest.conf`).
- Reads config file lines `from = to` for host aliases.

#### Output
- If `$as_json`: JSON string of `$summary` hash.
- Else: plain text:
  - `printf "%d requests from %d hosts, %d slow\n", $summary->{total}, $summary->{hosts}, $summary->{slow};`
  - List of top hosts: each line from `top_n($digest->{hosts}, $top)`.

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
- `usage() -> string` – prints help text.
- `read_aliases(PATH) -> hash` – parses alias file.
- `main() -> int` – orchestrates processing.

#### Constants and configuration
- `$config_file = $ENV{LOG_DIGEST_CONFIG} // '/etc/log-digest.conf'`.
- `$top = 10`.
- `$level = 'WARN'`.
- `$as_json = 0`.
- `@exclude_hosts = ()`.

#### Dependencies
- `FindBin` – `$Bin`.
- `Getopt::Long` – `GetOptions`.
- `JSON::PP` – `JSON::PP->new->canonical->encode`.
- `LogDigest` – `LogDigest->new`, `add`, `summarize`, `top_n`.

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` on file open failure.
