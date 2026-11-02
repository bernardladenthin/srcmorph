### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:13:04Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates and reports web‑server access‑log statistics per host and status code.

#### Purpose
- Provide a reusable log‑digesting library.
- Offer a CLI tool to generate host‑usage and status‑code summaries.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` parses individual log lines, normalizes hosts, counts requests, tracks slow requests, and tallies status codes.
- **Reporting** – `log_digest.pl` consumes `LogDigest`, applies host exclusions/aliases, and outputs either plain text or JSON summaries.

#### Key units
- **`LogDigest` module** – OO class with `new`, `add`, `summarize`, `top_n`; exports `parse_line`.
- **`log_digest.pl` script** – CLI entry point; parses options, loads host aliases, feeds logs to `LogDigest`, and prints results.

#### Data flow
1. CLI parses options → creates `LogDigest` with chosen level.  
2. For each input file, each line is read → host extracted → exclusion check → `LogDigest->add`.  
3. After all files, `summarize` produces a hash of totals, host counts, slow request count, and status counts.  
4. `top_n` selects the most frequent hosts for display.  
5. Output is formatted as text or JSON.

#### Dependencies
- **Internal**: `LogDigest` module.  
- **External**: `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, `Carp`.  
- **Configuration**: `/etc/log-digest.conf` (host alias mapping).

#### Cross-cutting
- Host normalization (`_normalize_host`) ensures consistent counting.  
- Logging level validation via `%LEVEL_RANK`.  
- Error handling with `croak` and `die` for invalid input or resource failures.  
- Constants (`MAX_LINES`, `SLOW_REQUEST_MS`) govern limits and thresholds.
