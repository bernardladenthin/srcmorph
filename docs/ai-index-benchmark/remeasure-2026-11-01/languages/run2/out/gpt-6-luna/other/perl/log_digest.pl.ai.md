### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:38:29Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes access logs by request status and host, printing busiest hosts and aggregate counts as text or JSON.

#### Purpose
- Command-line access-log digest tool with configurable host aliases and exclusions.

#### Type
- Script; implicit `main` package; procedural.
- Imports `top_n` from `LogDigest`; no exports or objects defined in this file.

#### Input
- `--top` / `--n` sets `$top`; integer, default `10`.
- `--level` sets `$level`; string, default `WARN`.
- `--json` / `--no-json` sets `$as_json`; default `0`.
- `--exclude-host` appends to `@exclude_hosts`.
- `--help` / `-h` prints usage and exits successfully.
- Remaining `@ARGV` entries are access-log files, read line by line.
- Reads `%ENV` key `LOG_DIGEST_CONFIG`; defaults to `/etc/log-digest.conf`.
- Reads the config file as host-alias lines (`from = to`); missing or unreadable config yields no aliases.

#### Output
- JSON mode: canonical JSON encoding of the summary, followed by a newline.
- Text mode: `N requests from N hosts, N slow`, then one line per result from `top_n($digest->{hosts}, $top)`.
- Returns exit status `0` from `main`; help exits `0`.

#### Core logic
- Load host aliases from the configured file; ignore blank lines and lines beginning with optional whitespace followed by `#`.
- Build an exclusion lookup from `@exclude_hosts`.
- Create a `LogDigest` object with the requested `level`.
- For each input file, extract the first space-delimited field as the host; skip lines whose aliased host (or original host if no alias) is excluded.
- Add remaining lines to the digest, summarize it, and print JSON or text output.

#### Subs
- `usage() -> string` — builds command usage text.
- `read_aliases($path) -> hash` — loads host aliases from a file.
- `main() -> 0` — reads logs, summarizes, and prints results.

#### Constants and configuration
- `$config_file` — `$ENV{LOG_DIGEST_CONFIG} // '/etc/log-digest.conf'`.
- `$top` — `10`.
- `$level` — `'WARN'`.
- `$as_json` — `0`.
- `@exclude_hosts` — initially empty.

#### Dependencies
- `strict` — strict variable and reference checks.
- `warnings` — emits warnings.
- `FindBin` — imports `$Bin`.
- `lib` — adds `$Bin` to `@INC`.
- `Getopt::Long` — imports `GetOptions`.
- `JSON::PP` — canonical JSON encoding.
- `LogDigest` — imports `top_n`; calls `LogDigest->new`.

#### Exceptions / Errors
- `GetOptions` failure: dies with usage text.
- `$top < 1`: dies with `--top must be positive`.
- Input log open failure: dies with `cannot read $file: $!`.
- Config open failure is non-fatal; `read_aliases` returns an empty hash.
