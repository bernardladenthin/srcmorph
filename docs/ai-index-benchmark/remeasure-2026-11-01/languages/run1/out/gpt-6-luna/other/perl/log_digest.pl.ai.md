### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:15:28Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes access-log requests by host and status, reporting busiest hosts and slow-request counts as text or JSON.

#### Purpose
- Script for reading access-log files, applying host aliases and exclusions, and printing an aggregate digest.

#### Type
- Script; procedural; no package declaration or exports.
- Uses `LogDigest` object methods via `LogDigest->new`.

#### Input
- `--top` / `--n` sets `$top`; integer, default `10`.
- `--level` sets `$level`; string, default `WARN`.
- `--json` / `--nojson` sets `$as_json`; default false.
- `--exclude-host` appends to `@exclude_hosts`.
- `--help` / `-h` prints usage and exits.
- `@ARGV`: access-log file paths.
- `%ENV`: `LOG_DIGEST_CONFIG`, default `/etc/log-digest.conf`.
- Reads the configured alias file when available; ignores it if it cannot be opened.
- Reads each access-log file named in `@ARGV`.

#### Output
- Text mode: request, host, and slow-request totals, followed by `top_n` host entries.
- JSON mode: canonical JSON encoding of the summary, followed by a newline.
- Returns `0` from `main`; script exits with that status.
- Help exits with status `0`.

#### Core logic
- Reads host aliases from the config file; skips blank lines and lines beginning with optional whitespace followed by `#`.
- Builds an exclusion lookup from `@exclude_hosts`.
- Creates a `LogDigest` object with the selected `$level`.
- For each log line, extracts the first space-delimited field as the host; skips the line if its aliased host, or otherwise its original host, is excluded.
- Adds remaining lines to the digest and summarizes it.
- Prints canonical JSON or text totals and the busiest hosts.

#### Subs
- `usage()` -> string — formats command usage and config hint.
- `read_aliases($path)` -> hash — reads host aliases from a file.
- `main()` -> status — processes logs and prints the digest.

#### Constants and configuration
- `$config_file` => `$ENV{LOG_DIGEST_CONFIG} // '/etc/log-digest.conf'`.
- `$top` => `10`.
- `$level` => `'WARN'`.
- `$as_json` => `0`.
- `@exclude_hosts` => empty list.

#### Dependencies
- `FindBin` — imports `$Bin`.
- `Getopt::Long` — imports `GetOptions`.
- `JSON::PP` — canonical JSON encoding.
- `LogDigest` — imports `top_n`; calls `new`, `add`, and `summarize`.
- `strict`, `warnings` — enable strict checks and warnings.
- `lib` — adds `$Bin` to the module search path.

#### Exceptions / Errors
- Invalid options cause `die usage()`.
- `$top < 1` causes `die "--top must be positive\n"`.
- Failure to open an access-log file causes `die "cannot read $file: $!\n"`.
- Failure to open the alias config file returns an empty alias map without an error.
