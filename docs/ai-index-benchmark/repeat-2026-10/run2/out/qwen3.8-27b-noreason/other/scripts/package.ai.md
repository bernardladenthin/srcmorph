### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:10:10Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for automated backup rotation with retention policies and regional sales revenue aggregation from order data.

#### Purpose
- Automates the creation and lifecycle management of compressed directory backups.
- Processes order CSV exports to generate monthly revenue reports segmented by region and currency.

#### Responsibilities
- **Backup Management**: Creates timestamped `tar.gz` archives, enforces retention limits by pruning old files, and ensures atomic writes via temporary file handling.
- **Sales Analytics**: Parses and validates order data, filters by currency and revenue thresholds, and aggregates financial totals by month and region.

#### Key units
- `rotate_backups.sh`: Bash script that accepts `SOURCE_DIR`, `BACKUP_DIR`, and `KEEP` arguments to generate and rotate backups.
- `sales_report.py`: Python module containing the `Order` dataclass and functions `parse_orders`, `revenue_by_month_and_region`, and `main` for CLI-driven reporting.

#### Data flow
- **Backups**: Source directory contents are compressed into a `.partial` temporary file, atomically renamed to a timestamped `.tar.gz`, and older archives are deleted based on the `KEEP` count.
- **Sales**: CSV files are read row-by-row, filtered by `currency`, converted to `Order` objects, aggregated into a `defaultdict` keyed by `(month, region)`, filtered by `min-revenue`, and printed as tab-separated values.

#### Dependencies
- **External**: Standard Unix utilities (`tar`, `date`, `ls`, `mv`, `rm`, `mkdir`) for backup operations.
- **Python Standard Library**: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, and `decimal` for sales processing.

#### Cross-cutting
- **Error Handling**: Both scripts use strict validation; `rotate_backups.sh` uses `set -euo pipefail` and specific exit codes, while `sales_report.py` raises `ValueError` for data integrity issues (missing columns, invalid dates/amounts).
- **Atomicity & Safety**: Backup script uses `trap` for cleanup and atomic renames; sales script uses `Decimal` for precise financial calculations to avoid floating-point errors.
