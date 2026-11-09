### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:21:42Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> A Perl toolkit that parses web‑server access logs, aggregates per‑host and per‑status statistics, flags slow requests, and outputs concise summaries or JSON reports.

#### Purpose
- Digest access logs into host‑ and status‑level counts.
- Identify slow requests and provide quick summaries.

#### Responsibilities
- **Log parsing & normalization** – extract host, status, and latency from each line.
- **Aggregation** – maintain counters for hosts, status codes, and slow requests.
- **Reporting** – produce human‑readable or JSON summaries, including top N hosts.
- **CLI orchestration** – read files, apply host exclusions, load alias mappings, and drive the digest.

#### Key units
- `LogDigest` (module) – OO interface (`new`, `add`, `summarize`, `top_n`), internal counters, host normalization.
- `log_digest.pl` (script) – command‑line front‑end, option parsing, config loading, output formatting.

#### Data flow
1. **CLI** parses options → creates `LogDigest` instance.  
2. For each log file: read line → filter by excluded hosts → `LogDigest->add`.  
3. After all lines: `LogDigest->summarize` → hash of totals, hosts, slow count, status counts.  
4. Script formats output (plain or JSON) and prints.

#### Dependencies
- **Perl core**: `POSIX`, `List::Util`, `Carp`, `Exporter`.  
- **External**: `Getopt::Long`, `JSON::PP`, `FindBin`.  
- **Configuration**: `/etc/log-digest.conf` for host alias mappings.

#### Cross-cutting
- Host names are normalized to lowercase and stripped of trailing dots.  
- Logging levels (`DEBUG`, `INFO`, `WARN`, `ERROR`) control verbosity; invalid levels `croak`.  
- A hard limit of 50 000 lines (`MAX_LINES`) protects against runaway input.  
- Slow requests are flagged when latency exceeds 1500 ms (`SLOW_REQUEST_MS`).  
- Top‑N selection uses a deterministic tie‑break: count descending, then lexicographic order.
