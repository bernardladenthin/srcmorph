### other/perl
- H: 1.0
- C: 68C82992
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:18:25Z
- G: 
- A: 0.0.0
- X: package
- F: [LogDigest.pm](LogDigest.pm.ai.md)
- F: [log_digest.pl](log_digest.pl.ai.md)
---
> Condenses and reports web‑server access log statistics, highlighting host activity, status codes, and slow requests.

#### Purpose
- Aggregate access‑log metrics per host and status code.
- Flag requests exceeding a configurable latency threshold.
- Provide a CLI tool for quick reporting, optionally in JSON.

#### Responsibilities
- **Parsing & Normalization** – extract host, status, and latency from raw log lines.
- **Counting & Aggregation** – maintain per‑host and per‑status tallies, track slow requests.
- **Reporting** – generate a concise summary and top‑N host list, output as text or JSON.
- **Configuration** – load host aliases and exclusion lists from a config file.

#### Key units
- **`LogDigest` (module)** – OO interface for digesting logs; methods: `new`, `add`, `summarize`, `top_n`; exports `parse_line`.
- **`parse_line`** – regex‑based parser returning a hashref of host, status, and latency.
- **`top_n`** – helper that returns the highest‑count keys from a hash.
- **`log_digest.pl` (script)** – orchestrates file reading, alias handling, exclusion filtering, and output formatting.

#### Data flow
1. **Input**: list of log files, optional `--exclude-host`, `--top`, `--level`, `--json`.
2. **Alias loading**: `read_aliases` parses `/etc/log-digest.conf` into `%aliases`.
3. **Digest creation**: `LogDigest->new(level => $level)`.
4. **Line processing**: for each file, read lines → skip excluded hosts → `$digest->add($line)`.
5. **Summarization**: `$digest->summarize` → hashref with totals, host stats, slow count, status counts.
6. **Output**: either plain text or JSON via `JSON::PP`, including top hosts via `top_n`.

#### Dependencies
- **Perl core**: `POSIX`, `List::Util`, `Carp`, `Exporter`, `FindBin`, `Getopt::Long`.
- **CPAN**: `JSON::PP`.
- **Internal**: `LogDigest` module, config file `/etc/log-digest.conf`.

#### Cross-cutting
- **Error handling**: `croak` for invalid log level; `die` for exceeding `MAX_LINES` or file I/O errors.
- **Constants**: `MAX_LINES = 50000`, `SLOW_REQUEST_MS = 1500`, `DEFAULT_LEVEL = 'WARN'`.
- **Host normalization**: lowercasing and stripping trailing dots via `_normalize_host`.
- **Logging level**: configurable via constructor argument, defaulting to `WARN`.
