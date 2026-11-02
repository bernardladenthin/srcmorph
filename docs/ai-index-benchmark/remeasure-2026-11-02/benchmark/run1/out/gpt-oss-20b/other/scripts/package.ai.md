### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:33:06Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A utility package that backs up directories and generates monthly sales reports from CSV order exports.

#### Purpose
- Automate directory backup rotation.
- Aggregate and report sales revenue by month and region.

#### Responsibilities
- **Backup rotation**: create timestamped tar.gz archives, keep a configurable number of recent backups, and clean up old ones.
- **Sales reporting**: parse CSV orders, filter by currency, aggregate revenue per month and region, and output tab‑separated summaries.

#### Key units
- `rotate_backups.sh` – Bash script that validates inputs, creates a compressed archive, and removes excess backups.
- `sales_report.py` – Python module exposing:
  - `parse_orders(path, currency)` – yields `Order` dataclass instances from a CSV.
  - `revenue_by_month_and_region(orders)` – returns a sorted dict of `(YYYY‑MM, region) → revenue`.
  - `main(argv)` – CLI entry point that orchestrates parsing, aggregation, and printing.

#### Data flow
1. **Backup**:  
   `SOURCE_DIR` → tar archive → `BACKUP_DIR` → retention logic removes older archives.
2. **Sales report**:  
   CSV files → `parse_orders` → `Order` objects → `revenue_by_month_and_region` → printed rows.

#### Dependencies
- **Shell**: `tar`, `date`, `du`, `ls`, `rm`, `mv`, `basename`, `dirname`, `cut`.
- **Python**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- Strict error handling: exit codes `0`, `1`, or `2` indicate success, no data, or argument/validation errors.
- Consistent use of UTC timestamps for backup filenames.
- Currency filtering and decimal arithmetic for accurate revenue calculations.
