### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:37:16Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes access-log entries into per-host, per-status, busiest-host, and slow-request counts.

#### Purpose
- Parses web-server access-log lines and accumulates digest statistics.
- Provides a summary and a count-ranked host list.

#### Type
- Module; package `LogDigest`; procedural API with a `new` constructor that blesses a hash.
- Declares `@EXPORT_OK` as `parse_line` and `top_n`; no Exporter setup is present.

#### Input
- `new` accepts named arguments; `level` defaults to `DEFAULT_LEVEL`.
- `add` accepts a log-line string in the expected access-log format.
- `top_n` accepts a count hash reference and a requested number.
- No command-line options, `@ARGV`, environment variables, or files are read.

#### Output
- `parse_line` returns a hash reference with normalized `host`, `time`, `method`, `path`, `status`, and `millis`; returns undef for a non-matching line.
- `add` returns `1` for a parsed entry or `0` for an invalid line.
- `summarize` returns a hash reference containing UTC `generated` timestamp, `total`, distinct `hosts`, `busiest` host count, `slow` request count, and copied `statuses` counts.
- `top_n` returns host keys ordered by descending count, then ascending name.
- Prints nothing and writes no files.

#### Core logic
- `new` validates the requested level and initializes host/status counts, slow-request count, and line count.
- `parse_line` matches host, timestamp, method, path, three-digit status, and elapsed milliseconds; normalizes the host to lowercase and removes one trailing dot.
- `add` increments the line counter, rejects counts beyond `MAX_LINES`, ignores malformed entries, and updates host/status counts.
- Requests taking more than `SLOW_REQUEST_MS` increment the slow count.
- `summarize` derives totals and busiest-host count from host counts and timestamps the result with `strftime` in UTC.
- `top_n` sorts count keys by count descending, with lexical tie-breaking.

#### Subs
- `new(args) -> object` — validate level and initialize digest.
- `parse_line(line) -> hashref or undef` — parse and normalize log entry.
- `add(line) -> 1 or 0` — count valid entry or ignore malformed line.
- `summarize() -> hashref` — build aggregate statistics.
- `top_n(counts, n) -> list` — rank keys and return requested prefix.
- `_normalize_host(host) -> host` — lowercase and trim trailing dot.

#### Constants and configuration
- `MAX_LINES => 50000`
- `DEFAULT_LEVEL => 'WARN'`
- `SLOW_REQUEST_MS => 1500`
- `$VERSION = '1.4'`
- `@EXPORT_OK = qw(parse_line top_n)`
- `%LEVEL_RANK`: `DEBUG => 0`, `INFO => 1`, `WARN => 2`, `ERROR => 3`

#### Dependencies
- `POSIX`: imports `strftime`.
- `List::Util`: imports `max`, `sum`.
- `Carp`: imports `croak`.
- `strict`, `warnings`: enable strictness and warnings.

#### Exceptions / Errors
- `new` croaks with `unknown level '<level>'` if the level is absent from `%LEVEL_RANK`.
- `add` croaks with `more than 50000 lines` after the line limit is exceeded.
