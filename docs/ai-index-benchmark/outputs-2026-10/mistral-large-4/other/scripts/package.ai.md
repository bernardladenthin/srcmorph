### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:50:01Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Standalone operational utilities for automated timestamped directory backup rotation with retention pruning and CSV-driven monthly sales revenue aggregation by region.

#### Purpose
- Provides independent CLI tools for infrastructure maintenance (backup lifecycle management) and business analytics (sales reporting from order exports)

#### Responsibilities
- Backup rotation: create UTC-timestamped gzip-compressed archives of a source directory and enforce retention by deleting all but the newest `KEEP` backups
- Sales reporting: parse order CSV files, filter by currency and minimum revenue threshold, and aggregate revenue into monthly totals per region

#### Key units
- `rotate_backups.sh`: Bash executable taking `SOURCE_DIR`, `BACKUP_DIR`, and optional `KEEP` (default 7) to produce `${name}_<timestamp>.tar.gz` archives and prune older ones
- `sales_report.py`: Python module with `Order` frozen dataclass, `parse_orders` generator, `revenue_by_month_and_region` aggregator, and `main` CLI entry point returning exit codes

#### Data flow
- Backup pipeline: source directory tree → `tar -czf` to `.partial` temp file → atomic `mv` to timestamped `.tar.gz` in backup dir → newest-first `ls -1t` listing → `rm` of archives beyond retention index
- Reporting pipeline: CSV paths → `csv.DictReader` with `REQUIRED_COLUMNS` validation → currency-filtered `Order` objects via `date.fromisoformat` and `Decimal` → `defaultdict(Decimal)` keyed by `(YYYY-MM, region)` → sorted tab-separated stdout lines or exit 1 if empty

#### Dependencies
- `rotate_backups.sh`: Coreutils (`date`, `du`, `ls`, `basename`, `dirname`, `mkdir`, `rm`, `mv`), `tar` with gzip (`-z`), Bash builtins (`set`, `trap`, `mapfile`, arrays)
- `sales_report.py`: Python standard library (`argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`/`InvalidOperation`)
- No internal dependencies between the two scripts; both are standalone executables

#### Cross-cutting
- Strict CLI validation with distinct exit codes: exit 2 for invalid arguments (bash script), exit 1 for empty result sets (Python script), exit 0 for success
- Defensive error handling: `set -euo pipefail` with `trap` cleanup of `.partial` temp files in bash; contextual `ValueError` with `path:line_no` in Python
- Temporal handling: UTC timestamp formatting (`%Y%m%dT%H%M%SZ`) for backup filenames; ISO 8601 date parsing (`date.fromisoformat`) for order dates
- Atomic output: backup uses temp-file-then-rename pattern; report writes directly to stdout with stderr for empty-state messaging
