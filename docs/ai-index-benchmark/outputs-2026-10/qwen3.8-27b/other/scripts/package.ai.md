### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:46:41Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Standalone operational and analytical utility scripts for backup rotation and sales revenue reporting.

#### Purpose
- Collection of self-contained CLI scripts for infrastructure maintenance (backup lifecycle) and business analytics (revenue aggregation).

#### Responsibilities
- **Backup rotation** — compress a directory into a timestamped `tar.gz` archive and enforce a "keep newest N" retention policy per source name.
- **Sales reporting** — read order CSV exports, filter by currency and minimum revenue, and print aggregated revenue grouped by month and region.

#### Key units
- `rotate_backups.sh` — bash script; validates inputs, creates atomic `.partial` → final archive via `tar -czf`, prunes old archives beyond `KEEP` count using `ls -1t` + `mapfile`.
- `sales_report.py` — Python CLI; `parse_orders` yields frozen `Order` dataclass instances from CSV, `revenue_by_month_and_region` accumulates `Decimal` sums in a `defaultdict` keyed by `(YYYY-MM, region)`, `main` orchestrates filtering and tab-separated stdout output.

#### Data flow
- **Backup**: `SOURCE_DIR` → `tar -czf` → `.partial` temp → atomic `mv` to `<basename>_<UTC-timestamp>.tar.gz` → `ls -1t` sort → `rm -f` old archives beyond retention.
- **Sales report**: CSV files → `parse_orders` (currency filter, column validation, `date.fromisoformat` + `Decimal` parsing) → `revenue_by_month_and_region` (aggregation) → `min-revenue` threshold filter → tab-separated stdout.

#### Dependencies
- **External (backup)**: `tar`, `gzip`, `date`, `du`, `basename`, `dirname`, `ls`, `mv`, `rm`, `mkdir`, bash `mapfile`.
- **External (sales)**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`.
- No cross-dependency between the two scripts.

#### Cross-cutting
- Both scripts are standalone CLIs with no shared library or common base types.
- Both use strict input validation with explicit exit codes (exit 2 for backup validation failures; exit 1 for no-data in sales report).
- Both handle partial/failure states defensively: `trap ... EXIT` for temp-file cleanup in bash; `ValueError` with file path and line number context in Python.
