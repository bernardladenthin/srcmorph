### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:44:30Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A small utility package that provides a shell script for rotating directory backups and a Python script for summarizing sales data from CSV exports.

#### Purpose
- Automate creation and retention of compressed backups.
- Generate monthly revenue reports by region from order CSV files.

#### Responsibilities
- **Backup rotation**: timestamped `.tar.gz` archives, keep a configurable number of recent copies.
- **Sales reporting**: parse CSV orders, filter by currency, aggregate revenue per month‑region, apply a minimum threshold.

#### Key units
- `rotate_backups.sh` – Bash script that validates arguments, creates a `.tar.gz` archive, and deletes older backups beyond the `KEEP` count.
- `sales_report.py` – Python module exposing:
  - `parse_orders(path, currency)` – yields `Order` dataclass instances.
  - `revenue_by_month_and_region(orders)` – returns a sorted dict of `(YYYY‑MM, region) → Decimal` totals.
  - `main(argv)` – CLI entry point that orchestrates parsing, aggregation, and output.

#### Data flow
1. **Backup script**:  
   `SOURCE_DIR → tar → temporary archive → final timestamped archive → BACKUP_DIR`  
   Old archives are listed, sorted, and removed if count exceeds `KEEP`.
2. **Sales script**:  
   `CSV files → parse_orders → Order objects → revenue_by_month_and_region → output lines`.

#### Dependencies
- **Shell**: `tar`, `du`, `ls`, `date`.  
- **Python**: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- Both utilities use strict argument validation and clear exit codes (`0` success, `1` or `2` for errors).  
- Error handling is explicit: missing files, invalid data, or bad arguments produce informative messages and non‑zero exits.  
- Configuration is minimal and self‑contained: backup count defaults to 7; sales script defaults to `EUR` currency and `0` minimum revenue.
