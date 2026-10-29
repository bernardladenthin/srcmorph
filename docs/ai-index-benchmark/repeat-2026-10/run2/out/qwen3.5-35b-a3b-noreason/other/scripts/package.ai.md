### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:05:50Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for automated backup rotation and financial revenue aggregation from CSV exports.

#### Purpose
- Executes scheduled backup retention policies and generates region-based revenue reports.

#### Responsibilities
- **Backup Management**: Compresses source directories, enforces retention counts, and cleans up obsolete archives.
- **Financial Reporting**: Ingests order CSVs, filters by currency, and aggregates revenue metrics by month and region.

#### Key units
- `rotate_backups.sh`: Bash utility creating timestamped `.tar.gz` archives and enforcing retention limits.
- `sales_report.py`: CLI application parsing CSV orders to calculate and print aggregated revenue.
- `parse_orders`: Generator function yielding validated `Order` dataclasses from CSV files.
- `revenue_by_month_and_region`: Aggregation function returning sorted revenue totals by `(month, region)` key.
- `main`: Entry point handling argument parsing and report execution flow.

#### Data flow
- **Backup Pipeline**: Validates inputs -> creates temporary `.partial` archive -> atomically moves to final timestamped name -> lists existing archives -> deletes excess oldest files.
- **Report Pipeline**: Parses CSV rows -> filters by currency -> converts amounts to `Decimal` -> aggregates into `(YYYY-MM, region)` dictionary -> sorts and filters by threshold -> outputs tab-separated report.

#### Dependencies
- **Internal**: `rotate_backups.sh` and `sales_report.py` operate as independent utilities within the `other/scripts` package.
- **External (Bash)**: `tar`, `date`, `ls`, `mv`, `rm`, `du`, `cut`, `mapfile`.
- **External (Python)**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- **Error Handling**: Bash script uses `set -euo pipefail` for strict failure; Python script raises `ValueError` or `InvalidOperation` for data validation failures.
- **Validation**: Both scripts enforce strict input constraints (positive integers for retention, required CSV columns, valid date/amount formats).
- **Atomicity**: Backup creation uses a temporary file and atomic move to prevent partial writes.
