### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:06:21Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for data archival via shell scripting and financial analytics via Python aggregation.

#### Purpose
*   Manages automated backup rotation and purging of compressed archives using Bash.
*   Generates filtered monthly sales revenue reports from CSV exports using Python.

#### Responsibilities
*   **Backup Management**: Creates dated tar.gz archives, validates source directories, and purges old backups based on retention counts.
*   **Financial Reporting**: Aggregates order data by month and region, filters by currency and revenue thresholds, and outputs summary statistics.

#### Key units
*   `rotate_backups.sh`: Shell script handling backup creation, validation, and archival rotation logic.
*   `sales_report.py`: Python script parsing CSV exports, aggregating revenue, and generating filtered reports.
*   `Order` (dataclass): Structured data type representing order details (date, region, amount, currency).
*   `Order` (dataclass): Structured data type representing order details (date, region, amount, currency).

#### Data flow
*   **Backup Pipeline**: User inputs source directory and retention count -> Script validates inputs -> Creates timestamped tar.gz archive -> Sorts existing archives by modification time -> Removes archives exceeding retention count -> Outputs confirmation and removed paths.
*   **Report Pipeline**: User inputs CSV file paths and filters (currency, threshold) -> Script validates CSV headers -> Iterates rows filtering by currency -> Parses fields into `Order` objects -> Aggregates amounts by `(YYYY-MM, region)` -> Filters against minimum revenue -> Prints formatted monthly/region summaries.

#### Dependencies
*   **Internal**: `rotate_backups.sh` relies on `tar`, `ls`, `date`, `du`, `cut`, `mapfile`. `sales_report.py` relies on `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.
*   **External**: Standard Unix utilities for backup operations; CSV file exports containing `order_date`, `region`, `amount`, `currency` columns.

#### Cross-cutting
*   **Error Handling**: `rotate_backups.sh` uses exit codes (0 success, 2 validation failure); `sales_report.py` raises `ValueError` for malformed data and handles `InvalidOperation` for decimal conversion.
*   **Configuration**: `rotate_backups.sh` accepts optional `keep` argument; `sales_report.py` defaults to EUR currency and zero revenue threshold.
*   **Data Types**: Consistent use of UTC timestamps for backups and ISO-formatted dates for report aggregation.
