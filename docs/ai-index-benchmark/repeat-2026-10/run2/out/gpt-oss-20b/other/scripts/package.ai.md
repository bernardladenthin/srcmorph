### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:17:52Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Automates backup rotation and generates monthly sales revenue reports from CSV order exports.

#### Purpose
- Provide a reusable backup rotation utility.
- Offer a command‑line tool to aggregate sales revenue by month and region.

#### Responsibilities
- **Backup Management** – create timestamped `.tar.gz` archives, enforce retention policy, and report actions.
- **Sales Reporting** – parse CSV orders, filter by currency, aggregate revenue per month/region, and output tab‑separated results.

#### Key units
- `rotate_backups.sh` – Bash script that validates inputs, builds archive names, performs compression, and deletes old archives.
- `sales_report.py` – Python module exposing:
  - `Order` dataclass (order_date, region, amount).
  - `parse_orders(path, currency)` – yields `Order` objects from a CSV.
  - `revenue_by_month_and_region(orders)` – returns a sorted dict of `(YYYY‑MM, region) → Decimal`.
  - `main(argv)` – CLI entry point handling arguments and printing results.

#### Data flow
1. **Backup script**  
   `SOURCE_DIR → tar → temporary archive → final `<basename>_YYYYMMDDTHHMMSSZ.tar.gz` → `BACKUP_DIR` → delete older archives beyond `KEEP`.  
2. **Sales report**  
   `CSV files → parse_orders → Order objects → revenue_by_month_and_region → print month, region, revenue`.

#### Dependencies
- **Shell**: `tar`, `date`, `du`, `ls`, `basename`, `dirname`, `mkdir`, `trap`.
- **Python**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Cross-cutting
- Both utilities use clear error handling: exit status 2 for the shell script on invalid parameters, and status 1 for the Python script when no orders are found.
- Output is human‑readable: console messages for backup actions; tab‑separated lines for sales totals.
