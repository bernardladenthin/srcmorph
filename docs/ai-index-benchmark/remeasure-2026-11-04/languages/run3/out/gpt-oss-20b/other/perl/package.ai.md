### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:06:05Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Condenses and reports web‑server access‑log activity, highlighting host usage, status‑code distribution, and slow requests.

#### Purpose
- Aggregate access‑log statistics per host and status code.
- Identify slow requests and the busiest hosts.

#### Responsibilities
- **Log parsing & normalization** – extract host, status, and latency from each line.
- **Counting & aggregation** – maintain per‑host, per‑status, and slow‑request tallies.
- **Reporting** – produce human‑readable or JSON summaries, optionally filtering hosts.

#### Key units
- **`LogDigest` module** – OO class exposing `new`, `add`, `summarize`, and exported `parse_line`, `top_n`.  
- **`log_digest.pl` script** – CLI driver that loads host aliases, applies exclusions, feeds lines to `LogDigest`, and outputs the final summary.

#### Data flow
1. **CLI** parses options → creates `LogDigest` with chosen log level.  
2. For each input file: read line → optionally skip excluded hosts → `LogDigest->add`.  
3. After all files: `LogDigest->summarize` → hash of totals, host counts, slow count, status counts.  
4. Script formats the hash as plain text or JSON and prints it.

#### Dependencies
- **Internal**: `LogDigest` (provides parsing, counting, and ranking).  
- **External**: `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, `Exporter`, `FindBin`.

#### Cross-cutting
- **Host normalization**: lower‑case, strip trailing dot (`_normalize_host`).  
- **Error handling**: `croak` for invalid log level or exceeding `MAX_LINES`; script dies on file I/O or bad options.  
- **Configuration**: host alias file (`LOG_DIGEST_CONFIG`) supports `from = to` mappings; comments and blanks ignored.  
- **Constants**: `MAX_LINES = 50000`, `SLOW_REQUEST_MS = 1500`, default log level `WARN`.  
- **Ranking**: `top_n` sorts counts by value descending, then key ascending.
