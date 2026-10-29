### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:41:49Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Rotates and prunes dated backups, and generates monthly sales revenue reports from CSV order data.

#### Purpose
- Manages dated backup rotation with retention limits and aggregates sales revenue from CSV exports by month and region.

#### Responsibilities
- **Backup rotation**: Creates timestamped compressed archives of a source directory and prunes old archives to maintain a configurable retention count.
- **Sales reporting**: Reads CSV order exports, filters by currency and minimum revenue, and prints aggregated revenue per month and region.

#### Key units
- `rotate_backups.sh` - Shell script that creates dated `.tar.gz` archives and removes excess backups, keeping only the newest N.
- `sales_report.py` - Python script that parses CSV order files and outputs aggregated revenue grouped by month and region.

#### Data flow
- `rotate_backups.sh` takes `source_dir`, `backup_dir`, and optional `keep` count; validates inputs, generates a UTC-timestamped archive, moves it atomically to `backup_dir`, then lists and deletes archives beyond the `keep` threshold.
- `sales_report.py` accepts one or more CSV paths, a currency code, and a minimum revenue threshold; parses each file, validates columns and data, aggregates `Decimal` revenues per `(month, region)`, sorts results lexicographically, and prints tab-separated summaries to stdout.

#### Dependencies
- `rotate_backups.sh`: bash shell with `set -euo pipefail`; `tar`, `mv`, `ls -1t`, `rm`, `du -h`, `date -u`.
- `sales_report.py`: `argparse`, `csv`, `sys` (standard library); `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`; CSV files with `order_date`, `region`, `amount`, `currency` columns.

#### Cross-cutting
- Both scripts validate inputs and exit with error codes on invalid data (`rotate_backups.sh` checks `keep` positivity and `source_dir` existence; `sales_report.py` raises `ValueError` for missing columns or malformed dates/amounts).
- `rotate_backups.sh` uses a trap on EXIT to clean up partial archive temp files on interruption.
- `sales_report.py` exits with code 1 and prints `"no orders found"` to stderr when no matching orders exist.
