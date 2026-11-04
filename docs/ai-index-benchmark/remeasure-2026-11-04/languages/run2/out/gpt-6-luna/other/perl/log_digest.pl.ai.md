### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:00:48Z
- G: 
- A: 0.0.0
- X: file
- P: 4F9ECAEF
---
> Summarizes access-log requests by host and status, applying configured host aliases and exclusions, then prints busiest hosts and request counts as text or JSON.

#### Purpose
- Command-line tool for digesting access-log files.
- Reads host aliases from configuration and reports aggregate request statistics.

#### Type
- Script; no package declaration.
- Procedural; calls `LogDigest->new`.
- No exports.

#### Input
- Options: `--top` / `--n` → `$top`; `--level` → `$level`; `--json` / `--nojson` → `$as_json`; repeated `--exclude-host` → `@exclude_hosts`; `--help` / `--h` → print usage and exit.
- `@ARGV`: access-log file paths; each log line is passed to the digest after host filtering.
- `%ENV`: `LOG_DIGEST_CONFIG` sets `$config_file`; default `/etc/log-digest.conf`.
- Reads the config file for host aliases; unreadable or absent config yields no aliases.
- Reads each `@ARGV` file line by line.

#### Output
- JSON mode: canonical JSON summary plus newline.
- Text mode: request/host/slow counts, then lines from `top_n($digest->{hosts}, $top)`.
- `main` returns `0`; script exits with that status.
- Usage is printed for help or invalid options; help exits `0`.

#### Core logic
- Parse options and reject `$top < 1` with `--top must be positive`.
- Load aliases as `from=to` mappings, skipping blank and comment lines.
- Build an exclusion lookup from `@exclude_hosts`.
- Construct a digest with `LogDigest->new(level => $level)`.
- For each log line, extract its first space-delimited field as host; skip it if the aliased host (or original host when no alias exists) is excluded.
- Add remaining lines to the digest and summarize it.
- Emit canonical JSON or formatted text with `top_n` host results.

#### Subs
- `usage()` → usage text; documents options and config path.
- `read_aliases($path)` → alias hash; reads `from=to` mappings.
- `main()` → `0`; reads logs and prints digest.

#### Constants and configuration
- `$config_file` → `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- `$top` → `10`.
- `$level` → `'WARN'`.
- `$as_json` → `0`.
- `@exclude_hosts` → empty initially.

#### Dependencies
- `strict`, `warnings` — enable strictness and warnings.
- `FindBin` — imports `$Bin`.
- `lib` — adds `$Bin` to module search path.
- `Getopt::Long` — imports `GetOptions`.
- `JSON::PP` — canonical JSON encoding.
- `LogDigest` — imports `top_n`; uses `new`, `add`, and `summarize`.

#### Exceptions / Errors
- Dies with usage text if `GetOptions` fails.
- Dies with `--top must be positive` if `$top < 1`.
- Dies with `cannot read $file: $!` when an input log cannot be opened.
- Config-file open failure is silently treated as an empty alias map.
