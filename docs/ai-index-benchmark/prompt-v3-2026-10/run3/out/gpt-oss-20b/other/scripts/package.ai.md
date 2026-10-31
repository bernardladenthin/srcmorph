### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:46:27Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Automates backup rotation and generates monthly revenue reports from order CSVs.

#### Purpose
- Automate creation and pruning of directory backups.
- Compute monthly revenue summaries from order exports.

#### Responsibilities
- **Backup Management** – create timestamped `.tar.gz` archives, keep a configurable number of recent copies.
- **Revenue Reporting** – parse CSV orders, filter by currency, aggregate by month and region, output tab‑separated totals.

#### Key units
- `rotate_backups.sh` – Bash script that validates inputs, builds archive names, creates compressed backups, and removes old ones.
- `sales_report.py` – Python module exposing:
  - `parse_orders(path, currency)` – yields `Order` dataclass instances from CSV.
  - `revenue_by_month_and_region(orders)` – aggregates revenue into a `Dict[(month, region), Decimal]`.
  - `main(argv)` – CLI entry point handling arguments and printing results.

#### Data flow
1. **Backup script**  
   `SOURCE_DIR → tar → .partial → .tar.gz → BACKUP_DIR → prune older archives`.
2. **Sales report**  
   `CSV files → parse_orders → Order objects → revenue_by_month_and_region → print lines`.

#### Dependencies
- **Shell**: `tar`, `date`, `du`, `ls`, `rm`, `mkdir`, `basename`, `cut`, `echo`, `trap`.
- **Python**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Cross-cutting
- Both units enforce strict error handling (`set -euo pipefail` in Bash; `ValueError` propagation in Python).
- Configuration constants (`KEEP` default 7, `REQUIRED_COLUMNS`) centralize defaults.
- Output is human‑readable: console messages for backups, tab‑separated lines for reports.
