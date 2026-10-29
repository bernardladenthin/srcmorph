### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:32:27Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A collection of utility scripts for system maintenance and business data reporting.

#### Purpose
* Provide automated infrastructure maintenance via backup rotation.
* Generate financial summaries from raw sales data exports.

#### Responsibilities
* **System Maintenance**: Automating timestamped directory archiving and retention policy enforcement (`rotate_backups.sh`).
* **Data Analysis**: Parsing CSV order exports to aggregate revenue by time and geography (`sales_report.py`).

#### Key units
* `rotate_backups.sh`: Bash script that creates `.tar.gz` archives and prunes old backups based on a `KEEP` threshold.
* `sales_report.py`: Python module that filters and aggregates sales revenue using `Decimal` precision.
* `parse_orders`: Generator in `sales_report.py` that validates CSV headers and yields `Order` objects.
* `revenue_by_month_and_region`: Aggregator in `sales_report.py` using a `defaultdict` to group revenue by month and region.

#### Data flow
* **Backup Pipeline**: `SOURCE_DIR` $\rightarrow$ `tar` (partial file) $\rightarrow$ Final timestamped `.tar.gz` in `BACKUP_DIR` $\rightarrow$ Pruning of oldest files.
* **Reporting Pipeline**: CSV files $\rightarrow$ `parse_orders` (currency filtering) $\rightarrow$ `revenue_by_month_and_region` (aggregation) $\rightarrow$ `min-revenue` filtering $\rightarrow$ Tab-separated stdout.

#### Dependencies
* **System Tools**: `tar`, `date`, `du`, `ls`, `basename`, `dirname`.
* **Python Standard Library**: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
* **Error Handling**: Use of `set -e` and `trap` in Bash for cleanup; `ValueError` and `InvalidOperation` handling in Python for data validation.
* **CLI Interface**: Both utilities rely on positional and optional command-line arguments for configuration.
