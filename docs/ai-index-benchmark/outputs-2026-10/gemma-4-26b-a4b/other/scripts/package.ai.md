### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:07:06Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides administrative automation for directory backups and data-driven sales performance reporting.

#### Purpose
* Automate directory archiving and retention management.
* Aggregate and report monthly sales revenue by region and currency.

#### Responsibilities
* **System Maintenance**: Managing filesystem backups through compression and automated rotation of old archives.
* **Data Analysis**: Processing transaction CSV files to generate formatted financial summaries.

#### Key units
* `rotate_backups.sh`: A Bash script that performs `tar` compression and enforces a retention policy via a `KEEP` threshold.
* `sales_report.py`: A Python CLI tool that aggregates `Order` dataclass instances into monthly regional revenue reports.
* `Order`: A dataclass representing validated transaction data including `order_date`, `region`, `amount`, and `currency`.
* `parse_orders`: A generator function that filters and validates CSV rows into `Order` objects.
* `revenue_by_month_and_region`: A logic unit that groups `Decimal` amounts by a composite `(YYYY-MM, region)` key.

#### Data flow
* **Backup Pipeline**: `SOURCE_DIR` $\rightarrow$ `tar` compression (with `.partial` safety) $\rightarrow$ `BACKUP_DIR` $\rightarrow$ retention-based deletion of files exceeding `KEEP` count.
* **Reporting Pipeline**: CSV files $\rightarrow$ `parse_orders` (filtering by `currency`) $\rightarrow$ `revenue_by_month_and_region` (aggregation) $\rightarrow$ stdout report.

#### Dependencies
* **System Tools**: `tar`, `date`, `du`, `ls`, `mkdir`, `rm`.
* **Python Modules**: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
* **Error Handling**: Shell scripts use `set -e` and `trap` for cleanup; Python scripts use `ValueError` for schema validation and exit codes for status reporting.
* **Data Integrity**: Use of `.partial` files in backups to prevent corruption and `Decimal` types in Python for precise financial calculations.
