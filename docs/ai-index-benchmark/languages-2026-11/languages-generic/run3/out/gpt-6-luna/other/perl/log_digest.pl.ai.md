### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:38:33Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes access-log requests by host and status, with configurable slow-request level, host aliases, exclusions, and text or JSON output.

#### Purpose
- Reads one or more access-log files and reports request totals, host totals, slow-request totals, and the busiest hosts.
- Supports host aliases from a config file and filtering by excluded host.

#### Type
- Perl command-line script; invokes `main` as its program entry point.

#### Input
- Command-line files: `@ARGV`, each containing access-log lines; the first space-delimited field is treated as the host.
- Options: `--top` / `-n`, `--level`, `--json` / `--no-json`, repeated `--exclude-host`, and `--help` / `-h`.
- Config resource: path in `LOG_DIGEST_CONFIG`, or `/etc/log-digest.conf` by default; alias lines are parsed as `from = to`.

#### Output
- Text mode: request, host, and slow-request totals, followed by `top_n` host entries.
- JSON mode: canonical JSON encoding of the summary.
- Exits with `main`’s return value (`0` on success); prints usage and exits `0` for help.

#### Core logic
- Loads aliases from the config file; missing or unreadable config yields an empty alias map.
- Maps each excluded host to a lookup set.
- Creates a `LogDigest` using the selected `level`.
- Reads each input file line by line, resolves the first field through the alias map, skips excluded resolved hosts, and adds remaining lines to the digest.
- Summarizes the digest and emits canonical JSON or text totals plus the top hosts.

#### Public API
- `main() -> integer` — processes files and prints summary.
- `read_aliases($path) -> hash` — reads host aliases from config.
- `usage() -> string` — returns command usage text.
- CLI `--top N`, `-n N` — set host-list limit.
- CLI `--level LEVEL` — set digest level.
- CLI `--json`, `--no-json` — enable or disable JSON output.
- CLI `--exclude-host HOST` — exclude a host; repeatable.
- CLI `--help`, `-h` — print usage and exit.

#### Constants and configuration
- `LOG_DIGEST_CONFIG` — config path environment variable; defaults to `/etc/log-digest.conf`.
- `$top` — default `10`.
- `$level` — default `'WARN'`.
- `$as_json` — default `0`.
- `@exclude_hosts` — initially empty.
- `--top` validation requires a positive value.

#### Dependencies
- `FindBin` (`$Bin`) and `lib` — add the script directory to module lookup.
- `Getopt::Long::GetOptions` — parse options.
- `JSON::PP` — canonical JSON encoding.
- `LogDigest->new`, `LogDigest->add`, `LogDigest->summarize`, and `LogDigest::top_n`.
- Perl built-ins: `open`, `close`, `split`, `chomp`, `map`, `printf`, `print`, `exit`.

#### Exceptions / Errors
- Dies with `--top must be positive` if the top limit is below one.
- Dies with `cannot read $file: $!` if an input log cannot be opened.
- Config-file open failure is silently treated as no aliases.
- `GetOptions` failure dies with usage text.
