### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:12:32Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A lightweight utility package that backs up directories and aggregates sales data from CSV exports.

#### Purpose
- Create dated, compressed backups while pruning old archives.
- Compute monthly revenue totals by region from order CSV files.

#### Responsibilities
- **Backup Management** – `rotate_backups.sh` handles archive creation, naming, and retention.
- **Sales Reporting** – `sales_report.py` parses CSV orders, filters by currency, aggregates revenue, and outputs tab‑separated summaries.

#### Key units
- **`rotate_backups.sh`** – Bash script; public API: `rotate_backups.sh <source_dir> <backup_dir> [keep]`.
- **`sales_report.py`** – Python module; public API: `parse_orders(path, currency)`, `revenue_by_month_and_region(orders)`, `main(argv=None)`.

#### Data flow
1. **Backup**: User supplies `source_dir` and `backup_dir`; script validates, creates `backup_dir`, tars `source_dir` into a timestamped `tar.gz`, then deletes archives older than the keep count.
2. **Reporting**: User passes CSV files; script parses each row into an `Order` dataclass, filters by currency, aggregates amounts by `(YYYY‑MM, region)`, then prints lines that meet the minimum revenue threshold.

#### Dependencies
- **Shell**: `bash`, `tar`, `date`, `du`, `ls`, `mapfile`, `cut`, `rm`, `echo`, `mkdir`, `basename`, `dirname`.
- **Python**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Cross-cutting
- Both units enforce strict input validation and exit on error (`set -euo pipefail` in Bash; `ValueError` in Python).
- Consistent use of UTC timestamps for naming backups.
- Both provide clear console output for success and cleanup actions.
