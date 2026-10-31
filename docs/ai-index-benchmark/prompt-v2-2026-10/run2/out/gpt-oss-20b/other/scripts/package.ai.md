### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:55:34Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Automates backup rotation and generates monthly sales summaries from CSV order exports.

#### Purpose
- Automate creation and retention of directory backups.
- Produce aggregated sales reports by month and region.

#### Responsibilities
- **Backup Management** – create timestamped `.tar.gz` archives, enforce retention policy.
- **Sales Reporting** – parse CSV orders, filter by currency, aggregate revenue, output tab‑separated summary.

#### Key units
- `rotate_backups.sh` – Bash script that validates inputs, builds archive names, performs compression, and deletes old backups.
- `sales_report.py` – Python module exposing:
  - `parse_orders(path, currency)` – yields `Order` dataclass instances.
  - `revenue_by_month_and_region(orders)` – returns a sorted revenue dictionary keyed by `(month, region)`.
  - `main(argv)` – CLI entry point handling arguments and printing results.

#### Data flow
1. **Backup**:  
   `SOURCE_DIR` → `tar.gz` archive → `BACKUP_DIR` → retention cleanup.
2. **Reporting**:  
   CSV files → `parse_orders` → `Order` objects → `revenue_by_month_and_region` → filtered output.

#### Dependencies
- Bash: `tar`, `du`, `date`, `ls`, `basename`, `cut`, `mapfile`.
- Python: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- Error handling via exit codes (script) and exceptions (`ValueError`).
- Configuration constants: default keep count (`7`), required CSV columns, default currency (`EUR`).
- Input validation: positive integer for keep count, existence of source directory, CSV column presence.
