### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:40:00Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> This package provides utility scripts for automated system backup rotation and financial sales data aggregation.

#### Purpose
*   Automate directory backup retention with compression and pruning.
*   Aggregate and filter monthly revenue data from CSV order exports.

#### Responsibilities
*   **Backup Management:** Create timestamped `.tar.gz` archives and enforce retention limits.
*   **Financial Reporting:** Parse CSV orders, filter by currency, and calculate regional revenue.

#### Key units
*   `rotate_backups.sh`: Bash script managing archive lifecycle and cleanup.
*   `sales_report.py`: Python CLI entry point for sales data processing.
*   `parse_orders`: Generator function yielding `Order` objects from CSV paths.
*   `revenue_by_month_and_region`: Aggregation function mapping `(YYYY-MM, region)` to `Decimal`.
*   `main`: CLI argument parser handling `csv_files`, `--currency`, and `--min-revenue`.

#### Data flow
*   **Backup:** `source_dir` -> `tar` archive -> `backup_dir` -> retention pruning by modification time.
*   **Sales:** `csv_files` -> `parse_orders` -> `defaultdict` aggregation -> `min-revenue` filter -> tab-separated output.

#### Dependencies
*   **Internal:** Standalone scripts with no internal package imports.
*   **External:** `bash`, `tar`, `date`, `ls`, `mv`, `rm` (backup); `argparse`, `csv`, `decimal`, `datetime` (sales).

#### Cross-cutting
*   **Error Handling:** Exit codes (0 success, 1/2 failure), `trap` cleanup, `ValueError`, `InvalidOperation`.
*   **Validation:** Argument type checks, CSV header verification, `Decimal` parsing.
*   **Configuration:** Default retention count (7), required CSV columns.
