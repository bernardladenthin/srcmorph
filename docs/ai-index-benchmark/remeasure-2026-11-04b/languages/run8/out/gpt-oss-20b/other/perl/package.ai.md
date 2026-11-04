### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:47:45Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> A Perl utility that aggregates web‑server access logs, producing per‑host and per‑status statistics while flagging slow requests.

#### Purpose
- Aggregate and summarize HTTP access logs.
- Identify slow requests and top request‑heavy hosts.

#### Responsibilities
- **Log parsing & normalization** – extract host, status, latency from each line.
- **Counting & aggregation** – maintain per‑host and per‑status tallies, track slow requests.
- **Reporting** – generate a summary hash and output either plain text or JSON.
- **CLI orchestration** – handle command‑line options, host exclusions, and configuration.

#### Key units
- **`LogDigest` module** – OO class with `new`, `add`, `summarize`, `top_n`; exports parsing helpers.
- **`log_digest.pl` script** – command‑line driver that loads host aliases, filters hosts, feeds lines to `LogDigest`, and prints the final report.

#### Data flow
1. **CLI** parses options → creates `LogDigest` instance.  
2. For each input file: read line → check exclusion → pass to `$digest->add`.  
3. After all files: `$digest->summarize` → hash of totals, hosts, slow count, status counts.  
4. Output: plain text or JSON via `JSON::PP`.

#### Dependencies
- **Internal**: `LogDigest` module.  
- **External**: `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, `Carp`, `Exporter`.

#### Cross-cutting
- **Host normalization**: lower‑case, strip trailing dot.  
- **Logging level validation**: `DEBUG`, `INFO`, `WARN`, `ERROR`.  
- **Error handling**: `croak` for invalid levels or line limits; `die` for CLI errors.  
- **Configuration**: optional host alias file (`LOG_DIGEST_CONFIG`), default `/etc/log-digest.conf`.  
- **Constants**: `MAX_LINES = 50000`, `SLOW_REQUEST_MS = 1500`.
