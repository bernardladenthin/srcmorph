### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:29:27Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides utilities for backing up directories and generating monthly sales reports from CSV order exports.

#### Purpose
- Create dated compressed backups of a directory, retaining only the most recent N archives.
- Generate aggregated monthly sales reports from CSV order files, filtering by currency and minimum revenue.

#### Responsibilities
- **Backup Management**: Validate inputs, create `.tar.gz` archives, enforce retention policy, and clean up temporary files.
- **Sales Reporting**: Parse CSV orders, filter by currency, aggregate revenue by month and region, and output formatted results.

#### Key units
- `rotate_backups.sh` – Bash script that builds a timestamped archive, deletes older backups beyond a configurable `KEEP` count, and reports actions.
- `sales_report.py` – Python module exposing:
  - `parse_orders(path, currency)` – yields `Order` dataclass instances for matching currency.
  - `revenue_by_month_and_region(orders)` – returns a sorted dictionary of revenue sums keyed by `(YYYY‑MM, region)`.
  - `main(argv)` – CLI entry point handling arguments, filtering, and printing results.

#### Data flow
1. **Backup**:  
   `SOURCE_DIR` → `tar` → temporary archive → renamed to final `.tar.gz` → retention check → delete excess archives.
2. **Sales Report**:  
   CSV files → `parse_orders` → `Order` objects → `revenue_by_month_and_region` → formatted output.

#### Dependencies
- **rotate_backups.sh**: `tar`, `date`, `du`, `ls`, `rm`, `mkdir`, `mv`.
- **sales_report.py**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Cross-cutting
- Both utilities enforce strict input validation and provide clear exit codes (`0` success, non‑zero for errors).
- Error handling is explicit: missing arguments, invalid directories, malformed CSV rows, and currency mismatches raise `ValueError` or exit with status `2` (backup) / `1` (report).
- Configuration is minimal and self‑contained: backup retention via positional argument, report defaults via constants (`EUR`, `0`).
