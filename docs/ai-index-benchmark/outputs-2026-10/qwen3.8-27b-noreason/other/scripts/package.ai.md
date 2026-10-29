### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T10:00:53Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for automated backup rotation with retention policies and monthly sales revenue aggregation from CSV order exports.

#### Purpose
- Manages disk space and data safety by creating timestamped backups and enforcing retention limits.
- Generates financial insights by aggregating sales data by region and month from raw CSV exports.

#### Responsibilities
- **Backup Management**: Creates compressed archives of source directories, handles atomic writes via temporary files, and prunes old archives based on a configurable retention count.
- **Sales Reporting**: Parses and validates order CSV files, filters transactions by currency, aggregates revenue by calendar month and region, and outputs filtered reports based on minimum revenue thresholds.

#### Key units
- `rotate_backups.sh`: Bash script that executes `tar` backups, manages `.partial` temp files, and deletes excess archives using `ls -1t` sorting.
- `sales_report.py`: Python module containing `parse_orders` (CSV iterator), `revenue_by_month_and_region` (aggregation logic), and `main` (CLI entry point).
- `Order`: Frozen dataclass in `sales_report.py` representing a single validated transaction with `order_date`, `region`, and `amount`.
- `REQUIRED_COLUMNS`: Constant in `sales_report.py` defining mandatory CSV headers (`order_date`, `region`, `amount`, `currency`).

#### Data flow
- **Backup Pipeline**: `SOURCE_DIR` path is validated → `BACKUP_DIR` is created if missing → `tar` writes to a `.partial` file → file is atomically renamed to a UTC-timestamped `.tar.gz` → existing archives are listed and those exceeding the `KEEP` count are deleted.
- **Sales Pipeline**: CSV files are read via `csv.DictReader` → rows are filtered by `currency` and validated for date/amount formats → valid rows are converted to `Order` objects → `Decimal` amounts are summed in a `defaultdict` keyed by `(YYYY-MM, region)` → results are sorted and printed if they meet the `--min-revenue` threshold.

#### Dependencies
- **System Tools**: `tar`, `gzip`, `date`, `basename`, `dirname`, `du`, `cut`, `ls`, `mkdir`, `mv`, `rm` (for `rotate_backups.sh`).
- **Python Standard Library**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal` (for `sales_report.py`).
- **Input Data**: CSV files with specific column structures for sales data; directory paths for backup sources.

#### Cross-cutting
- **Error Handling**: Both scripts use strict validation; `rotate_backups.sh` uses `set -euo pipefail` and specific exit codes (2 for input errors), while `sales_report.py` raises `ValueError` for missing columns or malformed data, wrapping `InvalidOperation` for non-numeric amounts.
- **Atomicity & Safety**: `rotate_backups.sh` uses `trap` to clean up partial files on interruption; `sales_report.py` uses immutable dataclasses and `Decimal` for precise financial calculations.
- **Configuration**: Both scripts accept CLI arguments for key parameters (`KEEP` count for backups; `--currency` and `--min-revenue` for reports).
