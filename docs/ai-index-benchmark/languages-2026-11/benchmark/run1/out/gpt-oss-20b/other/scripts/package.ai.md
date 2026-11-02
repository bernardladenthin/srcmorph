### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:21:36Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides utilities for automated backup rotation and monthly sales revenue reporting.

#### Purpose
- Automate creation and retention of compressed backups.
- Generate revenue summaries by month and region from order exports.

#### Responsibilities
- **Backup Management** – create dated `.tar.gz` archives, keep a configurable number of recent backups.
- **Sales Analytics** – parse CSV orders, filter by currency, aggregate revenue per month and region, output tab‑separated summaries.

#### Key units
- `rotate_backups.sh` – Bash script that validates inputs, builds UTC‑timestamped archive names, and removes old backups.
- `sales_report.py` – Python script exposing `parse_orders`, `revenue_by_month_and_region`, and `main` for command‑line execution.
- `parse_orders(path, currency)` – yields `Order` dataclass instances after CSV validation.
- `revenue_by_month_and_region(orders)` – returns a sorted dictionary of `(YYYY‑MM, region) → Decimal` totals.

#### Data flow
- **Backup**: `SOURCE_DIR` → `tar` → temporary archive → final archive in `BACKUP_DIR` → list & delete older archives.
- **Sales Report**: CSV files → `parse_orders` → `revenue_by_month_and_region` → filtered totals → printed lines.

#### Dependencies
- **rotate_backups.sh**: `tar`, `du`, `ls`, `date`.
- **sales_report.py**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Cross-cutting
- Input validation and error handling (e.g., missing directories, invalid CSV columns).
- Exit codes: `0` success, `1` or `2` for failures.
- Use of constants (`REQUIRED_COLUMNS`, default values) and UTC timestamps for naming.
- Temporary file cleanup via `EXIT` trap in the Bash script.
