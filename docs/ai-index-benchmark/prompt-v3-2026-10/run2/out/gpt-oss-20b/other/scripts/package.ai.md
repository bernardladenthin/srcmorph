### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:22:16Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Automates backup rotation and generates monthly sales revenue reports from CSV order exports.

#### Purpose
- Automate creation and retention of directory backups.
- Compute revenue totals by month and region from order CSVs.

#### Responsibilities
- **Backup Management** – create dated, compressed archives and prune old ones.
- **Sales Reporting** – parse order CSVs, filter by currency, aggregate revenue, and output tab‑separated summaries.

#### Key units
- `rotate_backups.sh` – Bash script that accepts `<source_dir> <backup_dir> [keep]`, creates `<basename>_YYYYMMDDTHHMMSSZ.tar.gz`, and deletes older archives beyond `keep`.
- `sales_report.py` – Python module exposing:
  - `parse_orders(path, currency)` → `Iterator[Order]` – reads CSV, validates columns, filters by currency, yields `Order` dataclass.
  - `revenue_by_month_and_region(orders)` → `Dict[(YYYY-MM, region), Decimal]` – aggregates amounts.
  - `main(argv=None)` – CLI entry point that parses arguments, aggregates data, and prints `month<TAB>region<TAB>revenue currency`.

#### Data flow
1. **Backup**: `rotate_backups.sh` receives source and destination directories, creates a timestamped tarball, stores it, then lists existing archives, keeping only the newest N.
2. **Reporting**: `sales_report.py` reads each CSV file, parses rows into `Order` objects, aggregates by month and region, filters by `min_revenue`, and prints results.

#### Dependencies
- **Bash**: built‑ins (`set`, `[[`, `echo`, `mkdir`, `basename`, `tar`, `date`, `du`, `cut`, `ls`, `mapfile`, `trap`, `rm`, `mv`) and external utilities (`tar`, `date`, `du`, `cut`, `ls`).
- **Python**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Cross-cutting
- Error handling: both scripts exit with non‑zero status on missing arguments, invalid inputs, or data errors.
- Logging: console output for progress and cleanup actions.
- Configuration: default keep count (7) and default currency (EUR) are hard‑coded constants.
