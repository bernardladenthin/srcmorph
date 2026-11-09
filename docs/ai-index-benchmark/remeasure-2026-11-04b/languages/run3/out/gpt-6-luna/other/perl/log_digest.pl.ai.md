### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:19:35Z
- G: 
- A: 0.0.0
- X: file
- P: 94FD0195
---
> Summarizes access-log requests by host and status, reporting busiest hosts and slow-request counts as text or JSON.

#### Purpose
- Command-line tool for digesting access-log files, applying host aliases and exclusions.
- Prints aggregate request statistics and the top hosts, or a JSON summary.

#### Type
- Script; no package declaration or exports; procedural.
- Uses `LogDigest` object methods.

#### Input
- `--top` / `--n`: set `$top`; integer, default `10`.
- `--level`: set `$level`; string, default `WARN`.
- `--json` / `--nojson`: set `$as_json`.
- `--exclude-host`: append host names to `@exclude_hosts`.
- `--help` / `--h`: print usage and exit `0`.
- `@ARGV`: access-log file paths; each line is passed to `LogDigest->add` unless its first space-delimited field, after alias mapping, is excluded.
- `%ENV` key `LOG_DIGEST_CONFIG`: alias-config path; defaults to `/etc/log-digest.conf`.
- Reads the alias-config file as `from = to` lines; skips blank and comment lines. Reads each access-log file line by line.

#### Output
- Text mode: `N requests from H hosts, S slow`, followed by one line per result from `top_n($digest->{hosts}, $top)`.
- JSON mode: canonical JSON encoding of the summary, followed by a newline.
- Returns `0` from `main`; the script exits with that status.
- Help prints usage text and exits `0`.

#### Core logic
- Loads aliases from the configured file; missing or unreadable alias files produce an empty alias map.
- Builds the excluded-host lookup and initializes `LogDigest` with `$level`.
- Reads each `@ARGV` log file, skips lines whose aliased host is excluded, and adds remaining lines to the digest.
- Summarizes the digest, then prints canonical JSON or text statistics and top hosts.
- Rejects `$top < 1` before running `main`.

#### Subs
- `usage() -> string` — returns command usage text.
- `read_aliases($path) -> hash` — reads host aliases from a config file.
- `main() -> 0` — reads logs, summarizes, and prints results.

#### Constants and configuration
- `$config_file` — `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- `$top` — `10`.
- `$level` — `WARN`.
- `$as_json` — `0`.
- `@exclude_hosts` — initially empty.

#### Dependencies
- `strict`, `warnings` — enforce strictness and emit warnings.
- `FindBin` — imports `$Bin`.
- `Getopt::Long` — imports `GetOptions`.
- `JSON::PP` — canonical JSON encoding.
- `LogDigest` — imports `top_n`; uses `new`, `add`, and `summarize`.

#### Exceptions / Errors
- `GetOptions` failure: dies with usage text.
- `$top < 1`: dies with `--top must be positive`.
- Failure to open an access-log file: dies with `cannot read $file: $!`.
- Failure to open the alias config is handled by returning an empty alias map.
