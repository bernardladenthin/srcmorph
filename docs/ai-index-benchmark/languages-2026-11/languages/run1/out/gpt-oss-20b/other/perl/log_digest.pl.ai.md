### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:12:11Z
- G: 
- A: 0.0.0
- X: file
---
> Reads access logs and prints the busiest hosts and status‑code counts, as text or JSON.

#### Purpose
- Command‑line tool to analyze web server access logs.
- Outputs host usage statistics and status‑code tallies.

#### Type
- Script
- Uses `LogDigest` module; no exported symbols.

#### Input
- Options:
  - `--top|n=i` → `$top` (default 10)
  - `--level=s` → `$level` (default 'WARN')
  - `--json!` → `$as_json` (default 0)
  - `--exclude-host=s` → `@exclude_hosts` (multiple allowed)
  - `--help|h` → prints usage
- Positional arguments: list of log files (`@ARGV`)
- Environment:
  - `LOG_DIGEST_CONFIG` → `$config_file` (default `/etc/log-digest.conf`)
- Config file format: `host=alias` lines; comments start with `#`.

#### Output
- If `$as_json`: JSON string of `$summary` hash, newline terminated.
- Else: plain text:
  - Summary line: `printf "%d requests from %d hosts, %d slow\n", $summary->{total}, $summary->{hosts}, $summary->{slow};`
  - Top hosts list: each line from `top_n($digest->{hosts}, $top)`.

#### Core logic
- Load host aliases from config file.
- Build exclusion hash from `@exclude_hosts`.
- Create `LogDigest` object with `level`.
- For each input file:
  - Read line by line.
  - Extract host (first token).
  - Skip if host (or its alias) is in exclusion hash.
  - Pass line to `$digest->add`.
- After processing all files:
  - `$digest->summarize` → `$summary`.
  - Output JSON or formatted text as above.

#### Subs
- `usage() -> string`  
  Returns usage message.
- `read_aliases($path) -> hash`  
  Parses alias file into `%aliases`.
- `main() -> 0`  
  Orchestrates parsing, digesting, and output.

#### Constants and configuration
- `$config_file` → `$ENV{LOG_DIGEST_CONFIG}` or `'/etc/log-digest.conf'`
- `$top` → 10
- `$level` → 'WARN'
- `$as_json` → 0
- `@exclude_hosts` → empty array

#### Dependencies
- `FindBin` – `$Bin`
- `Getopt::Long` – `GetOptions`
- `JSON::PP` – `JSON::PP->new->canonical->encode`
- `LogDigest` – `LogDigest->new`, `add`, `summarize`, `top_n`

#### Exceptions / Errors
- `die usage()` on bad options.
- `die "--top must be positive\n"` if `$top < 1`.
- `die "cannot read $file: $!\n"` if a log file cannot be opened.
