### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:57:42Z
- G: 
- A: 0.0.0
- X: file
- P: 4F9ECAEF
---
> Summarizes access-log requests by host and status, with host aliases, exclusions, and text or JSON reporting.

#### Purpose
- Command-line script for reading access logs and reporting request, host, and slow-request counts.
- Supports host alias configuration, host exclusions, and top-host output.

#### Type
- Script; no package declaration, exports, or object construction in this file.
- Uses `LogDigest` objects via `LogDigest->new`.

#### Input
- `--top` / `--n` sets `$top`; integer; default `10`.
- `--level` sets `$level`; string; default `WARN`.
- `--json` / `--no-json` sets `$as_json`; default false.
- `--exclude-host` sets `@exclude_hosts`; repeatable.
- `--help` / `-h` prints usage and exits successfully.
- Remaining `@ARGV` entries are access-log file paths.
- `$ENV{LOG_DIGEST_CONFIG}` selects the alias configuration file; default `/etc/log-digest.conf`.
- Reads the alias configuration file and each specified access-log file.

#### Output
- JSON mode: canonical JSON encoding of `$summary`, followed by a newline.
- Text mode: `N requests from H hosts, S slow`, followed by lines from `top_n($digest->{hosts}, $top)`.
- Returns exit status `0` after successful processing; help exits `0`.

#### Core logic
- Parses options; dies if `$top < 1`.
- Reads host aliases from the configured file; missing or unreadable config yields no aliases.
- Builds an exclusion lookup from `@exclude_hosts`.
- Creates a `LogDigest` object with the selected `$level`.
- For each log line, extracts the first space-delimited field as host; skips it if its aliased (or original) host is excluded, otherwise adds the line to the digest.
- Summarizes the digest and prints JSON or text output, including top hosts in text mode.

#### Subs
- `usage() -> string` — formats command usage text.
- `read_aliases($path) -> hash` — reads host aliases from configuration.
- `main() -> 0` — processes logs and prints summary.

#### Constants and configuration
- `$config_file` — `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- `$top` — `10`.
- `$level` — `WARN`.
- `$as_json` — `0`.
- `@exclude_hosts` — empty initially.

#### Dependencies
- `strict` — enables strict checks.
- `warnings` — enables warnings.
- `FindBin` — imports `$Bin`.
- `Getopt::Long` — imports `GetOptions`.
- `JSON::PP` — canonical JSON encoding.
- `LogDigest` — imports `top_n`; uses `new`, `add`, and `summarize`.

#### Exceptions / Errors
- `die "--top must be positive\n"` when `$top < 1`.
- `die usage()` if option parsing fails.
- `die "cannot read $file: $!\n"` when an access-log file cannot be opened.
- Alias configuration open failure is ignored; `read_aliases` returns an empty hash.
