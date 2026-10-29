### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:00:00Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides a collection of administrative and reporting utility scripts for backup management and financial data aggregation.

#### Purpose
* Automate system maintenance via directory backup rotation.
* Generate regional revenue reports from CSV order exports.

#### Responsibilities
* **System Maintenance**: Managing timestamped compressed archives and enforcing retention limits (`rotate_backups.sh`).
* **Financial Reporting**: Parsing CSV data, filtering by currency, and aggregating revenue by month and region (`sales_report.py`).

#### Key units
* `rotate_backups.sh`: Bash script that creates `.tar.gz` archives and prunes files exceeding a `KEEP` threshold.
* `Order`: Python dataclass representing a single transaction with date, region, amount, and currency.
* `parse_orders`: Python generator that validates CSV headers and yields `Order` objects.
* `revenue_by_month_and_region`: Python function that aggregates `Decimal` amounts into a `defaultdict` keyed by month and region.

#### Data flow
* **Backups**: `SOURCE_DIR` $\rightarrow$ `tar` compression $\rightarrow$ temporary file $\rightarrow$ timestamped `.tar.gz` in `BACKUP_DIR` $\rightarrow$ pruning of oldest files.
* **Reports**: CSV files $\rightarrow$ `parse_orders` (currency filter) $\rightarrow$ `revenue_by_month_and_region` (aggregation) $\rightarrow$ minimum revenue filter $\rightarrow$ tab-separated stdout.

#### Dependencies
* **Shell Utilities**: `tar`, `date`, `du`, `ls`, `basename`, `dirname`.
* **Python Standard Library**: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
* **Error Handling**: Use of `set -e` and `trap` in Bash for cleanup; `ValueError` and `InvalidOperation` handling in Python for data validation.
* **Precision**: Use of `decimal.Decimal` in Python to ensure financial accuracy for revenue calculations.
* **CLI Interface**: Both utilities utilize positional and optional command-line arguments for configuration.
