### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:02:36Z
- G: 
- A: 0.0.0
- X: file
- P: 4F9ECAEF
---
> Reads web server access logs, filters hosts, and reports request counts and slow responses, optionally as JSON.

#### Purpose
- Script to analyze access logs.
- Outputs busiest hosts and status‑code tallies.

#### Type
- Script.
- Uses package `LogDigest` (imported `top_n`).

#### Input
- Command‑line options:
  - `--top|n=i` → `$top` (default 10).
  - `--level=s` → `$level` (default 'WARN').
  - `--json!` → `$as_json` (default 0).
  - `--exclude-host=s` → `@exclude_hosts` (multiple allowed).
  - `--help|h` → prints usage.
- Positional arguments: list of log files (`@ARGV`).
- Environment variable: `LOG_DIGEST_CONFIG` (default `/etc/log-digest.conf`).
- Alias file format: `host=alias` per line, comments start with `#`.

#### Output
- If `$as_json`: JSON string of `$summary` hash, newline terminated.
- Else: plain text:
  - Summary line: `"%d requests from %d hosts, %d slow"`.
  - List of top hosts: one per line from `top_n($digest->{hosts}, $top)`.

#### Core logic
- Load host aliases from config file.
- Build exclusion set from `@exclude_hosts`.
- Create `LogDigest` object with `level`.
- For each input file:
  - Read line by line.
  - Extract host (first token).
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After processing all files:
  - Get `$summary = $digest->summarize`.
  - Output JSON or formatted text.

#### Subs
- `usage() -> string`  
  Returns usage message.
- `read_aliases($path) -> hash`  
  Parses alias file into `%aliases`.
- `main() -> 0`  
  Orchestrates parsing, digesting, and output.

#### Constants and configuration
- `$config_file = $ENV{LOG_DIGEST_CONFIG} // '/etc/log-digest.conf'`.
- `$top = 10`.
- `$level = 'WARN'`.
- `$as_json = 0`.
- `@exclude_hosts = ()`.

#### Dependencies
- `FindBin` (`$Bin`).
- `Getopt::Long` (`GetOptions`).
- `JSON::PP` (`JSON::PP->new->canonical->encode`).
- `LogDigest` (`LogDigest->new`, `->add`, `->summarize`, `top_n`).

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` if file open fails.
- `die` on missing config file read (handled by returning empty hash).

---
