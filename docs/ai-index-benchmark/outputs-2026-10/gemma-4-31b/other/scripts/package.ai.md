### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T21:43:42Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A collection of administrative utility scripts for system maintenance and business data reporting.

#### Purpose
* Provide standalone automation for directory archiving and retention.
* Generate aggregated financial reports from CSV sales exports.

#### Responsibilities
* **System Maintenance**: Automating compressed backups and managing disk space via rotation (`rotate_backups.sh`).
* **Financial Reporting**: Parsing CSV order data to calculate regional monthly revenue (`sales_report.py`).

#### Key units
* `rotate_backups.sh`: Bash script for timestamped `.tar.gz` archiving and retention management.
* `sales_report.py`: Python script for CSV processing and revenue aggregation.
* `Order` (dataclass): Represents a single sales record in `sales_report.py`.
* `parse_orders`: Generator function for filtering and validating CSV rows.
* `revenue_by_month_and_region`: Aggregator for summing revenue by date and location.

#### Data flow
* **Backup**: `SOURCE_DIR` $\rightarrow$ `tar -czf` $\rightarrow$ `.partial` file $\rightarrow$ timestamped `.tar.gz` in `BACKUP_DIR` $\rightarrow$ deletion of archives exceeding `KEEP` threshold.
* **Reporting**: CSV files $\rightarrow$ `parse_orders` (filtering by currency) $\rightarrow$ `revenue_by_month_and_region` (grouping by YYYY-MM/region) $\rightarrow$ threshold filtering $\rightarrow$ Tab-separated stdout.

#### Dependencies
* **Shell tools**: `tar`, `date`, `ls`, `du`, `mkdir`, `mv`, `rm`.
* **Python modules**: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
* **CLI-driven**: Both tools utilize positional and optional command-line arguments for configuration.
* **Error Handling**: Use of standard exit codes (0 for success, non-zero for errors) and strict input validation for paths and data formats.
