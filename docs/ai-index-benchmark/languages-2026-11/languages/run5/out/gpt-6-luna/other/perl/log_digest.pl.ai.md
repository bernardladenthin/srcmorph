### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:33:49Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes access logs into request, host, and slow-request totals, with busiest-host output in text or JSON.

#### Purpose
- Reads access-log files, applies configured host aliases and exclusions, and reports a digest.
- Provides a command-line interface for `LogDigest`.

#### Type
- Script: `log_digest.pl`; no package declaration or exports.
- Procedural; creates a `LogDigest` object via `LogDigest->new`.

#### Input
- Options:
  - `--top N`, `-n N` (`i`) → `$top`; default `10`.
  - `--level LEVEL` (`s`) → `$level`; default `WARN`.
  - `--json` / `--nojson` (`!`) → `$as_json`; default `0`.
  - `--exclude-host HOST` (`s`, repeatable) → `@exclude_hosts`.
  - `--help`, `-h` → prints usage and exits `0`.
- Remaining `@ARGV` entries are access-log file paths; each file is read line by line.
- Reads `%ENV` key `LOG_DIGEST_CONFIG`; defaults to `/etc/log-digest.conf`.
- Reads the config file as `from = to` host-alias lines; blank lines and lines beginning with optional whitespace followed by `#` are skipped. An unreadable config file yields no aliases.
- Log lines are split at the first space to identify the host.

#### Output
- Text mode: summary line `%d requests from %d hosts, %d slow`, followed by one line per entry from `top_n($digest->{hosts}, $top)`.
- JSON mode: canonical JSON encoding of the summary, followed by a newline.
- Exits with `main()`’s return value (`0` on success).
- `--help` prints usage and exits `0`; invalid options or `$top < 1` die with usage or `--top must be positive`.

#### Core logic
- Loads host aliases from `$config_file`; builds an exclusion lookup from `@exclude_hosts`.
- Constructs a digest with `LogDigest->new(level => $level)`.
- For each input file, reads each line, extracts its first field as the host, resolves the alias if present, and skips the line if the resolved host is excluded.
- Adds each remaining full line to the digest with `$digest->add($line)`.
- Calls `$digest->summarize`; emits canonical JSON or the text summary and busiest-host entries.

#### Subs
- `usage() -> string` — Builds command-line usage text.
- `read_aliases($path) -> %aliases` — Reads host aliases from a config file.
- `main() -> 0` — Processes log files and prints the digest.

#### Constants and configuration
- `$config_file` — `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- `$top` — `10`.
- `$level` — `WARN`.
- `$as_json` — `0`.
- `@exclude_hosts` — Empty initially.

#### Dependencies
- `strict`, `warnings` — Enable compile-time and runtime checks.
- `FindBin` — Imports `$Bin`.
- `Getopt::Long` — Imports `GetOptions`.
- `JSON::PP` — Uses `JSON::PP->new`, `canonical`, and `encode`.
- `LogDigest` — Imports `top_n`; uses `new`, `add`, and `summarize`.

#### Exceptions / Errors
- Dies with `--top must be positive\n` when `$top < 1`.
- Dies with `usage()` if `GetOptions` fails.
- Dies with `cannot read $file: $!\n` when an input log cannot be opened.
- Config-file open failure is handled by returning an empty alias map.
