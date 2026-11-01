### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:26:58Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes access logs into busiest-host listings and request/status summaries, with optional JSON output and host exclusions.

#### Purpose
- Reads one or more access-log files and reports request, host, and slow-request totals plus the busiest hosts.
- Loads host aliases from a configuration file; supports text or JSON output.

#### Type
- Script: `log_digest.pl`; procedural, with `main()` and package `main`.
- Imports `top_n` from `LogDigest`; uses `LogDigest->new` for its digest object.

#### Input
- `--top N` or `--n N` sets `$top` (default `10`).
- `--level LEVEL` sets `$level` (default `WARN`).
- `--json` sets `$as_json`; `--nojson` disables it (default off).
- Repeated `--exclude-host HOST` values populate `@exclude_hosts`.
- `--help` or `-h` prints usage and exits successfully.
- Remaining `@ARGV` entries are access-log paths; each is read line by line.
- Reads `%ENV` key `LOG_DIGEST_CONFIG`; default `/etc/log-digest.conf`.
- Reads the alias configuration file if it can be opened; lines with leading whitespace, comments (`#`), or blank content are skipped. Other lines are split at `=`.
- Log lines are split at the first space to obtain the host.

#### Output
- Text mode: prints `N requests from H hosts, S slow`, followed by one `top_n` result per line.
- JSON mode: prints the canonical JSON encoding of the summary, followed by a newline.
- Exits with `main()`’s return value (`0` on success); help exits `0`.

#### Core logic
- Parses options; dies with usage text on invalid options and rejects `$top < 1`.
- Reads host aliases and builds an exclusion lookup from `@exclude_hosts`.
- Creates a `LogDigest` object with the selected `level`.
- For each input file, reads each line, resolves its host through the alias map, skips excluded hosts, and adds remaining lines to the digest.
- Summarizes the digest; emits canonical JSON or text totals and the top hosts.

#### Subs
- `usage()` → usage text; describes command-line syntax and config path.
- `read_aliases($path)` → alias hash; reads `from=to` mappings, returning empty if the file cannot be opened.
- `main()` → exit code; reads logs, summarizes, and prints results.

#### Constants and configuration
- `$config_file` => `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- `$top` => `10`.
- `$level` => `WARN`.
- `$as_json` => `0`.
- `@exclude_hosts` => empty initially.

#### Dependencies
- `strict`, `warnings` — enable strictness and warnings.
- `FindBin` — imports `$Bin`.
- `Getopt::Long` — imports `GetOptions`.
- `JSON::PP` — canonical JSON encoding.
- `LogDigest` — imports `top_n`; uses `new`, `add`, and `summarize`.

#### Exceptions / Errors
- Dies with `--top must be positive` when `$top < 1`.
- Dies with usage text if `GetOptions` fails.
- Dies with `cannot read $file: $!` if an input log cannot be opened.
- Alias-file open failure is handled by returning an empty alias hash.
