### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:56:01Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides utilities for backing up directories and generating monthly sales reports from CSV order exports.

#### Purpose
- Automate directory backup rotation.
- Aggregate and report sales revenue by month and region.

#### Responsibilities
- **Backup Rotation**: Create timestamped compressed archives, enforce retention policy.
- **Sales Reporting**: Parse CSV orders, filter by currency, aggregate revenue, output tab‑separated report.

#### Key units
- `rotate_backups.sh` – Bash script that creates `<basename>_YYYYMMDDTHHMMSSZ.tar.gz` archives and deletes older ones beyond a configurable count.
- `sales_report.py` – Python module exposing:
  - `parse_orders(path, currency)` – yields `Order` dataclass instances.
  - `revenue_by_month_and_region(orders)` – returns `Dict[(month, region), Decimal]`.
  - `main(argv)` – CLI entry point producing tab‑separated revenue lines.

#### Data flow
1. **Backup**: `rotate_backups.sh` receives source and backup directories, optional keep count → validates inputs → creates temporary `.tar.gz.partial` → renames to final archive → lists existing archives → removes those beyond the keep threshold.
2. **Reporting**: `sales_report.py` receives CSV file paths and options → parses each file with `parse_orders` → aggregates via `revenue_by_month_and_region` → filters by `--min-revenue` → prints `month<TAB>region<TAB>revenue<TAB>currency`.

#### Dependencies
- **Shell**: Bash built‑ins (`set`, `echo`, `mkdir`, `basename`, `date`, `tar`, `du`, `ls`, `rm`, `mv`) and external utilities (`tar`, `du`, `date`, `ls`).
- **Python**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Cross-cutting
- Error handling: both scripts exit with non‑zero status on validation failures (`rotate_backups.sh` exits `2`; `sales_report.py` exits `1` if no orders).
- Configuration via command‑line arguments (`--currency`, `--min-revenue`, optional keep count).
- Use of UTC timestamps for deterministic archive naming.
