### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:40:56Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Aggregates web‑server access logs into host‑ and status‑code statistics, exposing a CLI for quick reporting.

#### Purpose
- Parse and summarize HTTP access logs.
- Provide a command‑line utility for reporting busiest hosts and status counts.

#### Responsibilities
- **Log parsing & aggregation** – `LogDigest` module handles line parsing, host normalization, slow‑request detection, and statistical aggregation.
- **Reporting** – `log_digest.pl` script reads log files, applies host exclusions, and outputs formatted text or JSON summaries.

#### Key units
- **`LogDigest` (module)** – object with `new`, `add`, `summarize`; exports `parse_line`, `top_n`.  
- **`log_digest.pl` (script)** – CLI entry point; uses `Getopt::Long`, `JSON::PP`, and `LogDigest`.

#### Data flow
1. CLI parses options → creates `LogDigest` with chosen level.  
2. For each input file: read lines → filter by excluded hosts → `LogDigest->add`.  
3. After all files: `LogDigest->summarize` → `$summary`.  
4. Output: JSON string or formatted text with top hosts via `top_n`.

#### Dependencies
- **Internal**: `LogDigest` module, its exported `top_n`.  
- **External**: `POSIX`, `List::Util`, `Carp`, `Exporter` (module); `FindBin`, `Getopt::Long`, `JSON::PP` (script).

#### Cross-cutting
- Host normalization (`_normalize_host`) ensures consistent counting.  
- Logging level validation via `%LEVEL_RANK`.  
- Max line cap (`MAX_LINES`) protects against runaway input.  
- Error handling uses `croak`/`die` with clear messages.  
- Configuration via `/etc/log-digest.conf` for host aliases.
