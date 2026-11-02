### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:27:50Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes access-log requests by host and slow-request count, with optional host exclusions, aliases, and JSON output.

#### Purpose
- Reads access-log files and reports total requests, distinct hosts, and slow requests.
- Prints the busiest hosts or a canonical JSON summary.

#### Type
- Perl command-line script; uses `LogDigest`.

#### Input
- Positional `@ARGV`: access-log file paths; each line is passed to `LogDigest->add`.
- `LOG_DIGEST_CONFIG`: optional alias-config path; defaults to `/etc/log-digest.conf`.
- Config lines: `host=alias`; blank lines and lines beginning with `#` are skipped.
- Options: top-host count, log level, JSON mode, and excluded host names.

#### Output
- Text mode: request, host, and slow counts, followed by `top_n` host results.
- JSON mode: canonical JSON encoding of the summary, followed by a newline.
- Exits with `main()`’s return value (`0` on success).

#### Core logic
- Reads host aliases from the configured file; an unreadable or missing config file yields no aliases.
- Builds an exclusion set from `--exclude-host` values.
- Creates a `LogDigest` instance with the selected `level`.
- For each input line, extracts the first space-delimited field as the host, resolves its alias if present, skips excluded hosts, and adds remaining lines to the digest.
- Calls `summarize`; emits canonical JSON or formatted counts and `top_n($digest->{hosts}, $top)`.

#### Public API
- `main() -> integer` — process input files and print summary.
- `read_aliases($path) -> hash` — load host aliases from config.
- `usage() -> string` — return command usage text.
- CLI options:
  - `--top N`, `-n N` — set number of top hosts; default `10`.
  - `--level LEVEL` — set digest level; default `WARN`.
  - `--json`, `--no-json` — enable or disable JSON output; default disabled.
  - `--exclude-host HOST` — exclude a host; repeatable.
  - `--help`, `-h` — print usage and exit successfully.
- Entry point: `exit main()`.

#### Constants and configuration
- `LOG_DIGEST_CONFIG` — environment variable read for the alias-config path.
- `/etc/log-digest.conf` — default alias-config path.
- `$top` — default `10`; must be positive.
- `$level` — default `WARN`.
- `$as_json` — default `0`.
- `--top must be positive` — validation error.
- Usage heredoc literally includes `sub this_is_not_a_function {`.

#### Dependencies
- `FindBin` (`$Bin`) — locate script directory.
- `Getopt::Long` (`GetOptions`) — parse options.
- `JSON::PP` (`canonical`, `encode`) — serialize JSON.
- `LogDigest` (`new`, `add`, `summarize`, `{hosts}`) — digest access logs.
- `LogDigest::top_n` — select busiest hosts.
- Perl built-ins: `open`, `close`, `split`, `chomp`, `map`, `printf`, `print`, `exit`.

#### Exceptions / Errors
- Dies if `$top < 1`.
- Dies with `cannot read $file: $!` when an input log cannot be opened.
- Alias-config open failure is silently treated as an empty alias map.
- `GetOptions` failure dies with usage text.
