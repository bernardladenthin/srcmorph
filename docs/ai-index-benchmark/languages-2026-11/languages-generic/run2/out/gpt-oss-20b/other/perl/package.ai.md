### other/perl
- H: 1.0
- C: 0597B700
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:35:26Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates and reports web‑server access log statistics, filtering by host and status, and optionally outputting JSON.

#### Purpose
- Parse access logs and produce per‑host and per‑status summaries.
- Identify slow requests and provide top‑N host listings.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` module reads raw log lines, normalizes hosts, counts requests, tracks slow responses, and tallies status codes.
- **Command‑line interface** – `log_digest.pl` script handles options, host aliasing/exclusion, feeds lines to `LogDigest`, and formats the final report (plain text or JSON).

#### Key units
- `LogDigest` (class) – public methods `new`, `add`, `summarize`, `top_n`; internal `_normalize_host`.
- `log_digest.pl` – functions `usage`, `read_aliases`, `main`; script entry point.

#### Data flow
1. **CLI** parses options → loads host aliases/exclusions → creates `LogDigest` instance.  
2. For each input file, each line is read, host is checked against exclusions, then passed to `$digest->add`.  
3. After all files, `$digest->summarize` produces a hash of totals, busiest host, slow count, and status map.  
4. `top_n` extracts the top N hosts from the host count map.  
5. The script prints the summary and top hosts, optionally JSON‑encoded.

#### Dependencies
- **Perl core**: `POSIX`, `List::Util`, `Carp`, `FindBin`, `Getopt::Long`.  
- **CPAN**: `JSON::PP`.  
- **Internal**: `LogDigest` module used by the script.

#### Cross-cutting
- Host normalization via regex in `LogDigest::_normalize_host`.  
- Logging level handling with `%LEVEL_RANK` and `DEFAULT_LEVEL`.  
- Constants: `MAX_LINES` (50 000), `SLOW_REQUEST_MS` (1500 ms).  
- Error handling: `croak` for invalid levels or line limits; `die` for CLI validation and file I/O failures.
