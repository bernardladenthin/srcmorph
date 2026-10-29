### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T09:55:43Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides standalone utility scripts for system backup rotation and financial sales data aggregation.

#### Purpose
* Automate directory archiving and retention management.
* Generate regional monthly revenue reports from CSV exports.

#### Responsibilities
* **System Maintenance**: Managing timestamped `.tar.gz` archives and pruning old backups via `rotate_backups.sh`.
* **Data Analysis**: Parsing CSV order files, filtering by currency, and aggregating revenue by month and region via `sales_report.py`.

#### Key units
* `rotate_backups.sh`: Bash script for atomic backup creation and rotation.
* `Order`: Python dataclass representing a single sales transaction.
* `parse_orders`: Python generator that validates and yields `Order` objects from CSVs.
* `revenue_by_month_and_region`: Python function that aggregates revenue using composite keys of date and region.

#### Data flow
* **Backup**: `SOURCE_DIR` $\rightarrow$ `tar` (compressed archive) $\rightarrow$ `BACKUP_DIR` $\rightarrow$ Rotation logic (deletion of files exceeding `KEEP` limit).
* **Reporting**: CSV files $\rightarrow$ `parse_orders` (filtering by currency) $\rightarrow$ `revenue_by_month_and_region` (aggregation) $\rightarrow$ Threshold filtering $\rightarrow$ Tab-separated stdout.

#### Dependencies
* **System Tools**: `tar`, `date`, `du`, `ls`.
* **Python Standard Library**: `argparse`, `csv`, `collections.defaultdict`, `datetime`, `decimal`.
