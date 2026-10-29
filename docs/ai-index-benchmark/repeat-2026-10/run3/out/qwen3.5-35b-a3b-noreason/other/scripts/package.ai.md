### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:45:25Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides utility scripts for automated backup rotation and financial revenue aggregation from CSV exports.

#### Purpose
- Executes system-level backup retention policies and CLI-based financial reporting.

#### Responsibilities
- **Backup Management**: Compresses directories, timestamps archives, and enforces retention counts.
- **Financial Aggregation**: Parses order CSVs, filters by currency, and calculates regional revenue by month.

#### Key units
- `rotate_backups.sh`: Bash utility for creating dated `.tar.gz` archives and deleting excess files.
- `sales_report.py`: Python CLI for parsing CSV orders and generating tab-separated revenue reports.
- `Order` (dataclass): Structured record for `order_date`, `region`, `amount`, and `currency`.
- `parse_orders`: Generator function yielding valid `Order` objects from CSV files.
- `revenue_by_month_and_region`: Aggregation function returning totals keyed by `(YYYY-MM, region)`.

#### Data flow
- **Backup**: Validates inputs -> compresses source to `.partial` -> moves to timestamped archive -> lists and deletes old archives.
- **Sales Report**: Reads CSV files -> validates headers -> filters by currency -> parses dates/amounts -> aggregates by month/region -> sorts and filters by min-revenue -> prints results.

#### Dependencies
- **Internal**: `rotate_backups.sh` and `sales_report.py` operate as standalone utilities within `package.ai`.
- **External (Backup)**: `bash`, `tar`, `date`, `ls`, `mv`, `rm`, `mkdir`, `mapfile`.
- **External (Sales)**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- **Error Handling**: Both scripts exit with code 2 (backup) or 1 (sales) on validation failures or missing data.
- **Validation**: Strict checks for argument types (positive integers), file existence, and CSV column headers.
- **Cleanup**: `rotate_backups.sh` uses `trap` to remove partial archives on failure; `sales_report.py` skips invalid rows with specific error logging.
