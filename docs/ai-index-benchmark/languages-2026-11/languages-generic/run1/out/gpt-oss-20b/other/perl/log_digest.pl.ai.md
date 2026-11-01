### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:29:26Z
- G: 
- A: 0.0.0
- X: file
---
> Reads web server access logs and reports the busiest hosts and status‑code counts, optionally as JSON.

#### Purpose
- Parses log files to aggregate request counts per host.
- Filters hosts via aliases and exclusion list.
- Outputs summary statistics and top hosts.

#### Type
- Perl script (executable).

#### Input
- Command‑line arguments: `FILE...` log files.
- Options: `--top N`, `--level LEVEL`, `--json`, `--exclude-host HOST`.
- Environment variable `LOG_DIGEST_CONFIG` (default `/etc/log-digest.conf`).
- Alias file: key=value pairs mapping host names.

#### Output
- Text or JSON summary:
  - `total`: total requests.
  - `hosts`: distinct host count.
  - `slow`: count of slow requests.
- List of top N hosts (host → count) printed line‑by‑line.

#### Core logic
- Load host aliases from config file.
- Build exclusion hash from `--exclude-host`.
- Create `LogDigest` object with specified `level`.
- For each input file:
  - Read line by line.
  - Extract host from first field.
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After processing all files:
  - Call `$digest->summarize` for statistics.
  - If `--json`, encode summary with `JSON::PP`.
  - Otherwise, print formatted summary and top hosts via `top_n`.

#### Public API
- `main() -> 0`  
  Entry point; processes arguments, reads logs, outputs results.
- `read_aliases(PATH) -> %aliases`  
  Parses alias file into hash.
- `usage() -> STRING`  
  Returns usage message.

#### Constants and configuration
- `$config_file` = `$ENV{LOG_DIGEST_CONFIG}` or `'/etc/log-digest.conf'`.
- `$top` default `10`.
- `$level` default `'WARN'`.
- `$as_json` default `0`.
- `@exclude_hosts` empty array.

#### Dependencies
- `FindBin` for script directory.
- `Getopt::Long` for option parsing.
- `JSON::PP` for JSON output.
- `LogDigest` module (provides `new`, `add`, `summarize`, `hosts`).
- `top_n` function from `LogDigest`.

#### Exceptions / Errors
- `die "--top must be positive\n"` if `$top < 1`.
- `die usage()` on option parsing failure.
- `die "cannot read $file: $!\n"` if a log file cannot be opened.
- `die` on failure to open alias file (returns empty hash silently).
