### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:43:34Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Reads web server logs, filters hosts, and reports request counts and slow requests, optionally as JSON.

#### Purpose
- Command‑line tool for summarizing access logs.
- Filters by host aliases and exclusion list.
- Outputs text or JSON summary.

#### Type
- Script.
- No package; procedural.
- Exports none.

#### Input
- `--top|n=i` → `$top` (default 10).
- `--level=s` → `$level` (default 'WARN').
- `--json!` → `$as_json` (default 0).
- `--exclude-host=s` → `@exclude_hosts`.
- `--help|h` → prints usage.
- `@ARGV` → list of log files.
- `%ENV{LOG_DIGEST_CONFIG}` → `$config_file` (default '/etc/log-digest.conf').

#### Output
- Text: `printf "%d requests from %d hosts, %d slow\n"`.
- JSON: `JSON::PP->new->canonical->encode($summary)`.
- Prints top hosts via `top_n`.
- Exit status 0 on success; non‑zero on errors.

#### Core logic
- Load host aliases from `$config_file`.
- Build exclusion hash from `@exclude_hosts`.
- Create `LogDigest` with `$level`.
- For each file:
  - Open file; read lines.
  - Extract host from each line.
  - Skip if host (or alias) in exclusion hash.
  - Add line to digest.
- Summarize digest.
- Output summary in chosen format.

#### Subs
- `usage() -> string` – prints help text.
- `read_aliases($path) -> %aliases` – parses alias file.
- `main() -> 0` – orchestrates processing.

#### Constants and configuration
- None.

#### Dependencies
- `FindBin` – `$Bin`.
- `Getopt::Long` – `GetOptions`.
- `JSON::PP` – `new`, `canonical`, `encode`.
- `LogDigest` – `new`, `add`, `summarize`, `hosts` (accessed as `$digest->{hosts}`).

#### Exceptions / Errors
- `die "--top must be positive\n"` if `$top < 1`.
- `die usage()` on `GetOptions` failure.
- `die "cannot read $file: $!\n"` on file open error.
