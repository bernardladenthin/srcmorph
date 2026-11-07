### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:12:41Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Summarizes access-log requests by host and status, reporting busiest hosts and slow-request counts as text or JSON.

#### Purpose
- Reads one or more access-log files, applies host aliases and exclusions, and prints an aggregate summary.

#### Type
- Script; package `main`; procedural.
- Imports `top_n` from `LogDigest`; no object construction in this script.

#### Input
- `--top` / `--n`: sets `$top` (integer; default `10`).
- `--level`: sets `$level` (string; default `WARN`).
- `--json` / `--nojson`: sets `$as_json` (default `0`).
- `--exclude-host`: appends host names to `@exclude_hosts`.
- `--help` / `-h`: prints usage and exits successfully.
- `@ARGV`: access-log file paths; each file is read line by line.
- `%ENV`: `LOG_DIGEST_CONFIG`; sets `$config_file`, default `/etc/log-digest.conf`.
- `$config_file`: read as host-alias configuration; missing/unreadable file yields no aliases.
- Access-log lines: first space-delimited field is treated as the host.
- Alias configuration lines: `from=to`; blank lines and lines beginning with optional whitespace followed by `#` are ignored.

#### Output
- Text mode: request, host, and slow counts, followed by `top_n` host entries.
- JSON mode: canonical JSON encoding of the summary, followed by a newline.
- Returns `0` from `main`; script exits with that status.
- Help prints usage and exits with status `0`.

#### Core logic
- Parses options and rejects `$top < 1`.
- Reads aliases from `$config_file`; builds an exclusion lookup from `@exclude_hosts`.
- Creates a `LogDigest` object with the selected `level`.
- For each `@ARGV` file, reads each line and skips it when its aliased host (or original host if no alias) is excluded.
- Adds remaining lines to the digest, then calls `summarize`.
- Prints canonical JSON or text counts plus `top_n($digest->{hosts}, $top)`.

#### Subs
- `usage() -> string` — builds command usage text.
- `read_aliases($path) -> hash` — reads host aliases; returns empty hash on open failure.
- `main() -> 0` — processes files and prints the digest summary.

#### Constants and configuration
- `$config_file`: `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- `$top`: `10`.
- `$level`: `WARN`.
- `$as_json`: `0`.
- `@exclude_hosts`: initially empty.

#### Dependencies
- `strict`, `warnings` — enable strictness and warnings.
- `FindBin` — imports `$Bin`.
- `lib` — adds `$Bin` to module search path.
- `Getopt::Long` — imports `GetOptions`.
- `JSON::PP` — canonical JSON encoding.
- `LogDigest` — imports `top_n`; calls `new`, `add`, and `summarize`.

#### Exceptions / Errors
- Dies with `--top must be positive` when `$top < 1`.
- Dies with usage text if `GetOptions` fails.
- Dies with `cannot read $file: $!` when an access-log file cannot be opened.
- `read_aliases` handles config-file open failure by returning an empty hash.
