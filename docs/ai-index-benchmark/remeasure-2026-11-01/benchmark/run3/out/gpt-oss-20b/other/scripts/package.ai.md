### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:08:34Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides utilities for backing up directories and generating monthly sales revenue reports from CSV order exports.  

#### Purpose  
- Automate creation and rotation of compressed directory backups.  
- Compute and output revenue totals by month and region from order CSV files.  

#### Responsibilities  
- **Backup Management** – `rotate_backups.sh` handles timestamped archive creation, retention policy, and cleanup.  
- **Sales Reporting** – `sales_report.py` parses CSV orders, aggregates revenue, and formats a tab‑separated report.  

#### Key units  
- **rotate_backups.sh** – Bash script that validates inputs, creates a `.tar.gz` archive, and enforces a keep‑count policy.  
- **sales_report.py** – Python module exposing `parse_orders`, `revenue_by_month_and_region`, and `main` for CLI usage.  
- **Order dataclass** – Represents an order with `order_date`, `region`, `amount`, and `currency`.  

#### Data flow  
1. **Backup**:  
   - Input: source directory, backup directory, optional keep count.  
   - Process: validate, tar‑compress, rename with UTC timestamp, list existing archives, delete excess.  
   - Output: new archive file and console logs.  

2. **Sales Report**:  
   - Input: one or more CSV files, optional currency filter, minimum revenue threshold.  
   - Process: read CSV, filter by currency, convert fields, aggregate by `(month, region)`.  
   - Output: printed lines `month<TAB>region<TAB>revenue currency`; exit status indicates success or no data.  

#### Dependencies  
- **rotate_backups.sh** – `tar`, `date`, `du`, `ls`, `rm`, `mv`, `mkdir`, `cut`.  
- **sales_report.py** – `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.  

#### Cross-cutting  
- Both utilities enforce strict error handling: exit codes (`0` success, `1` or `2` failure), validation of required inputs, and informative console messages.  
- `sales_report.py` uses `Decimal` for monetary precision and raises `ValueError` for malformed data, ensuring robust parsing.  
- `rotate_backups.sh` employs `set -euo pipefail` for safe shell execution and traps to clean temporary files.
