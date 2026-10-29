### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:50:33Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for automated backup rotation and financial sales reporting from order data.

#### Purpose
- Automates the creation and retention management of timestamped directory backups.
- Aggregates and reports monthly revenue metrics from order CSV exports based on currency and threshold filters.

#### Responsibilities
- **Backup Management**: Handles archive creation, atomic file writing, and retention policy enforcement for directory backups.
- **Sales Analytics**: Parses order data, filters by currency, aggregates revenue by month and region, and formats tab-separated reports.

#### Key units
- `rotate_backups.sh`: Bash script that creates `.tar.gz` archives with UTC timestamps and prunes old backups based on a `KEEP` count.
- `sales_report.py`: Python module containing the `Order` dataclass and functions `parse_orders`, `revenue_by_month_and_region`, and `main` for CSV processing and aggregation.

#### Data flow
- **Backups**: `SOURCE_DIR` is validated, archived to a `.partial` temp file in `BACKUP_DIR`, atomically renamed to a timestamped `.tar.gz`, and old archives are pruned.
- **Sales**: CSV files are read by `parse_orders` (filtering by `currency`), aggregated into a `defaultdict` by `revenue_by_month_and_region`, filtered by `--min-revenue`, and printed as tab-separated lines by `main`.

#### Dependencies
- **External**: Standard Unix utilities (`tar`, `date`, `du`, `ls`, `mv`, `rm`, `mkdir`) for backup operations.
- **Python Standard Library**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, and `decimal` for sales reporting.

#### Cross-cutting
- **Error Handling**: Both scripts use strict validation; `rotate_backups.sh` uses `set -euo pipefail` and `trap` for cleanup, while `sales_report.py` raises `ValueError` for invalid CSV data or missing columns.
- **Configuration**: Both support configurable parameters via CLI arguments (`KEEP` for backups, `--currency`/`--min-revenue` for sales).
