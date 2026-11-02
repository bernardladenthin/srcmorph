### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:25:55Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Condenses web‑server access logs into per‑host and per‑status summaries, highlighting slow requests and providing a command‑line interface for quick reporting.

#### Purpose
- Aggregate and summarize HTTP access log entries.
- Expose a CLI for reporting busiest hosts and status‑code distributions.

#### Responsibilities
- **Log parsing & counting** – `LogDigest` parses individual lines, normalizes hosts, tallies per‑host and per‑status counts, and flags slow requests.
- **Summary generation** – `LogDigest::summarize` produces a hash of totals, busiest host, slow‑request count, and status frequencies.
- **CLI orchestration** – `log_digest.pl` loads host aliases, applies exclusions, feeds log lines to `LogDigest`, and outputs results in plain text or JSON.

#### Key units
- `LogDigest` (module) – object with `new`, `add`, `summarize`; exports `parse_line`, `top_n`.
- `log_digest.pl` (script) – command‑line tool; uses `Getopt::Long`, `JSON::PP`, `FindBin`.

#### Data flow
1. **CLI** parses options, reads alias file, builds exclusion set.  
2. For each input file, reads lines, skips excluded hosts, passes raw line to `$digest->add`.  
3. `LogDigest::add` parses the line, updates host/status counters, tracks slow requests.  
4. After all files, `summarize` aggregates totals and identifies busiest host.  
5. CLI formats and prints the summary and top hosts (or JSON).

#### Dependencies
- Internal: `LogDigest` module, `FindBin`, `Getopt::Long`, `JSON::PP`.  
- External: `POSIX` for `strftime`, `List::Util` for `max`/`sum`, `Carp` for error handling.

#### Cross-cutting
- Host normalization (lowercase, strip trailing dot) used by both parser and CLI.  
- Error handling via `croak` in `LogDigest`; CLI dies with usage or file‑open errors.  
- Configuration via `$ENV{LOG_DIGEST_CONFIG}` pointing to an alias file.  
- Constants: `MAX_LINES` (50 000), `SLOW_REQUEST_MS` (1 500 ms), default log level (`WARN`).
