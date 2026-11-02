### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:42:07Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates and summarizes web‑server access logs, producing per‑host, per‑status, and slow‑request statistics, optionally in JSON.

#### Purpose
- Condense raw access logs into actionable metrics.
- Provide a reusable module and a command‑line tool for log analysis.

#### Responsibilities
- **Log parsing & counting** – extract host, status, and latency from each line.
- **Statistical summarization** – total entries, distinct hosts, busiest host, slow‑request count, status distribution.
- **Top‑N host reporting** – sorted list of most active hosts.
- **CLI orchestration** – read files, apply exclusions, output formatted results.

#### Key units
- **`LogDigest` module** – OO class with `new`, `add`, `summarize`, `top_n`; exports `parse_line`.
- **`log_digest.pl` script** – procedural entry point; parses options, loads host aliases, feeds lines to `LogDigest`, outputs summary.

#### Data flow
1. **CLI** parses options → creates `LogDigest` instance.  
2. For each input file: read line → optionally skip excluded hosts → `LogDigest->add`.  
3. After all files: `LogDigest->summarize` → hash of metrics.  
4. Script formats output (plain text or JSON) and prints.

#### Dependencies
- **Internal**: `LogDigest` (module), `LogDigest::top_n` (exported).  
- **External**: `POSIX`, `List::Util`, `Carp`, `Exporter` (module); `Getopt::Long`, `JSON::PP`, `FindBin` (script).  
- **Configuration**: `/etc/log-digest.conf` for host aliases; `LOG_DIGEST_CONFIG` env var overrides path.

#### Cross-cutting
- **Error handling**: `croak` for invalid levels or line limits; script dies on file read errors.  
- **Constants**: `MAX_LINES=50000`, `SLOW_REQUEST_MS=1500`, default log level `WARN`.  
- **Host normalization**: lowercase, strip trailing dot.  
- **Top‑N sorting**: descending count, alphabetical tie‑break.  
- **JSON output**: uses `JSON::PP` with canonical ordering.
