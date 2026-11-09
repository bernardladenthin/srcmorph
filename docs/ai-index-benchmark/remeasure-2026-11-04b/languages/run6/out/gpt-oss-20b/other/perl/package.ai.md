### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:45:59Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Summarizes web‑server access logs, producing per‑host and per‑status statistics and identifying slow requests.

#### Purpose
- Aggregate and summarize HTTP access logs.
- Expose a reusable `LogDigest` class and a command‑line tool for quick reporting.

#### Responsibilities
- **Log parsing & counting** – `LogDigest` parses individual log lines, normalizes hosts, and tallies request counts, status codes, and slow requests.
- **Reporting** – `summarize` produces a hash of totals, busiest host, and status distribution; `top_n` selects the most frequent hosts.
- **CLI orchestration** – `log_digest.pl` loads host aliases, applies exclusions, feeds files to `LogDigest`, and outputs either plain text or JSON.

#### Key units
- `LogDigest` (module) – constructor `new`, methods `add`, `summarize`, `top_n`, helper `_normalize_host`.
- `log_digest.pl` (script) – `main`, `usage`, `read_aliases`.

#### Data flow
1. **CLI** parses options → creates `LogDigest` with chosen log level.  
2. For each input file: read line → optionally skip excluded hosts → `$digest->add($line)`.  
3. After all files: `$digest->summarize()` → `$summary`.  
4. Output: JSON string or formatted text with total requests, host count, slow request count, and top hosts.

#### Dependencies
- **Internal**: `LogDigest` module used by the script.  
- **External**: `POSIX`, `List::Util`, `Carp`, `Exporter` (module); `FindBin`, `Getopt::Long`, `JSON::PP` (script).

#### Cross-cutting
- Host normalization (lowercase, strip trailing dot) used by both parsing and exclusion logic.  
- Log level validation via `%LEVEL_RANK` ensures consistent verbosity control.  
- Constants (`MAX_LINES`, `SLOW_REQUEST_MS`) guard against excessive input and define slow‑request threshold.  
- Error handling uses `croak` for module errors and `die` for CLI misuse.
