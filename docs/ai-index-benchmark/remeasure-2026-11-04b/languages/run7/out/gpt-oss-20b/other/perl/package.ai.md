### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:46:50Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Summarizes web‑server access logs, counting per‑host requests, status codes, and slow responses, and reports the busiest hosts.

#### Purpose
- Provide a reusable log‑digesting class and a command‑line tool to analyze access logs.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` parses individual log lines, normalizes hosts, and tallies counts.
- **Statistical summarization** – `summarize` produces totals, busiest host, slow‑request count, and status‑code histogram.
- **Ranking helper** – `top_n` returns the top N keys by count.
- **CLI orchestration** – `log_digest.pl` loads host aliases, applies exclusions, feeds lines to `LogDigest`, and outputs a text or JSON summary.

#### Key units
- `LogDigest` (module) – constructor `new(level => $level)`, methods `add($line)`, `summarize()`, exported `parse_line`, `top_n`.
- `log_digest.pl` (script) – `main()`, `usage()`, `read_aliases($path)`.

#### Data flow
1. **CLI** parses options, loads alias file, builds exclusion set.  
2. For each input log file, reads lines, skips excluded hosts, and passes each line to `$digest->add`.  
3. After all files, `$digest->summarize` produces a hashref of statistics.  
4. Output is formatted as plain text or JSON based on `--json`.

#### Dependencies
- **Internal** – `LogDigest` module, `top_n` helper.  
- **External** – `POSIX qw(strftime)`, `List::Util qw(max sum)`, `Carp qw(croak)`, `Exporter`, `Getopt::Long`, `JSON::PP`, `FindBin`, `LogDigest` (for script).

#### Cross-cutting
- Host normalization (`_normalize_host`) ensures consistent counting.  
- `MAX_LINES` limits input size; exceeding it triggers a `croak`.  
- Logging level validation in `new` enforces one of `DEBUG`, `INFO`, `WARN`, `ERROR`.  
- `top_n` sorts by count descending then alphabetically, providing deterministic ranking.  
- Configuration via `LOG_DIGEST_CONFIG` and alias file supports host aliasing and exclusion.
