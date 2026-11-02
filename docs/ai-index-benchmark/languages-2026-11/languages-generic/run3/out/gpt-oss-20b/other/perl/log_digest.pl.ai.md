### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:40:17Z
- G: 
- A: 0.0.0
- X: file
---
> Reads web server logs and outputs the busiest hosts and status‑code statistics, optionally as JSON.

#### Purpose
- Parses access log files.
- Counts requests per host and status code.
- Reports totals and top N hosts.

#### Type
- Perl script (file). Uses `LogDigest` module.

#### Input
- Command‑line arguments: `FILE...` log files.
- Options: `--top N`, `--level LEVEL`, `--json`, `--exclude-host HOST`.
- Environment variable `LOG_DIGEST_CONFIG` (default `/etc/log-digest.conf`).
- Configuration file: host alias mappings (`from = to`).

#### Output
- Text or JSON summary:
  - `total` requests, `hosts` unique hosts, `slow` slow requests.
  - List of top N hosts (host → count).
- Printed to STDOUT.

#### Core logic
- Load host aliases from config file.
- Build exclusion set from `--exclude-host`.
- Create `LogDigest` instance with `level`.
- For each log file:
  - Read line, extract host (first field).
  - Skip if host (or its alias) is excluded.
  - Pass line to `$digest->add`.
- After processing all files:
  - `$digest->summarize` returns hash with totals.
  - If `--json`, encode summary with `JSON::PP`.
  - Else, print summary line and top hosts via `top_n`.

#### Public API
- `main() -> 0`  
  Entry point; processes arguments, reads logs, outputs summary.
- `read_aliases(PATH) -> %aliases`  
  Parses alias file into hash.
- `usage() -> STRING`  
  Returns help text.

#### Constants and configuration
- `$config_file = $ENV{LOG_DIGEST_CONFIG} // '/etc/log-digest.conf'`
- `$top = 10`
- `$level = 'WARN'`
- `$as_json = 0`
- `@exclude_hosts` (array of host names)

#### Dependencies
- `FindBin`, `Getopt::Long`, `JSON::PP`, `LogDigest`.
- `LogDigest->new`, `add`, `summarize`, `top_n`.

#### Exceptions / Errors
- `die "--top must be positive\n"` if `$top < 1`.
- `die usage()` on bad options.
- `die "cannot read $file: $!\n"` if a log file cannot be opened.
