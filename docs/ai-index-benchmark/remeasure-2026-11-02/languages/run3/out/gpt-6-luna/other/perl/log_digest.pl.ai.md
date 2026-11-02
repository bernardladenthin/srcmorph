### log_digest.pl
- H: 1.0
- C: 8D4DDD59
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:54:35Z
- G: 
- A: 0.0.0
- X: file
- P: B953BC65
---
> Summarizes access-log requests by host and status, reporting busiest hosts and aggregate request, host, and slow-request counts as text or JSON.

#### Purpose
- Read host aliases and access-log files, exclude selected hosts, and print a request summary.
- Support text output or canonical JSON output.

#### Type
- Script; package `main`; procedural.
- Imports `top_n` from `LogDigest`; no exports or object construction of its own.

#### Input
- `--top` / `-n` sets `$top`; integer, default `10`.
- `--level` sets `$level`; string, default `WARN`.
- `--json` / `--nojson` sets `$as_json`; default `0`.
- `--exclude-host` appends to `@exclude_hosts`; repeatable.
- `--help` / `-h` prints usage and exits `0`.
- `@ARGV`: access-log file paths, read line by line.
- `%ENV`: `LOG_DIGEST_CONFIG`; defaults to `/etc/log-digest.conf`.
- Config file: reads alias lines as `host = alias`; skips blank and comment lines.
- Log files: reads each line, using its first space-delimited field as the host.

#### Output
- Text: request, host, and slow counts, followed by `top_n` host entries, each on its own line.
- JSON: canonical JSON encoding of the summary, followed by a newline.
- Returns exit status `0` on successful completion.
- Prints usage and exits `0` for help; invalid options or `$top < 1` cause `die`.

#### Core logic
- Read alias mappings from `$config_file`; if it cannot be opened, use an empty mapping.
- Build the excluded-host lookup from `@exclude_hosts`.
- Create a `LogDigest` object with `level => $level`.
- For each `@ARGV` file, read lines; skip a line when its host, mapped through `%aliases` if present, is excluded.
- Add remaining lines to the digest.
- Summarize the digest; emit canonical JSON or print aggregate counts and `top_n($digest->{hosts}, $top)`.

#### Subs
- `usage()` → string: build command usage text.
- `read_aliases($path)` → hash: read host alias mappings.
- `main()` → exit status: process logs and print summary.

#### Constants and configuration
- `$config_file` → `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- `$top` → `10`.
- `$level` → `WARN`.
- `$as_json` → `0`.
- `@exclude_hosts` → empty list.

#### Dependencies
- `FindBin` — imports `$Bin`.
- `Getopt::Long` — imports `GetOptions`.
- `JSON::PP` — canonical JSON encoding.
- `LogDigest` — imports `top_n`; uses `LogDigest->new`, `add`, and `summarize`.
- `strict`, `warnings`, `lib` — pragmas affecting behavior or module search path.

#### Exceptions / Errors
- `GetOptions` failure: `die usage()`.
- `$top < 1`: `die "--top must be positive\n"`.
- Log file open failure: `die "cannot read $file: $!\n"`.
- Config file open failure: returns an empty alias mapping; no exception.
