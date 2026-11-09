### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:56:52Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides command‑line utilities for backing up directories and generating monthly revenue reports from order CSV exports.  

#### Purpose  
- Automate creation and rotation of compressed backups.  
- Compute and output revenue summaries by month and region from CSV order data.  

#### Responsibilities  
- **Backup rotation**: `rotate_backups.sh` handles archive creation, naming, and pruning.  
- **Revenue reporting**: `sales_report.py` parses CSVs, filters by currency, aggregates by month/region, and prints tab‑separated reports.  

#### Key units  
- **`rotate_backups.sh`** – Bash script that builds a timestamped `.tar.gz`, keeps the newest N archives, and cleans up old ones.  
- **`sales_report.py`** – Python module exposing  
  - `parse_orders(path, currency)` → `Iterator[Order]`  
  - `revenue_by_month_and_region(orders)` → `Dict[(month, region), Decimal]`  
  - `main(argv)` → `int` (CLI entry point).  

#### Data flow  
1. **Backup**:  
   - Input: source directory, backup directory, optional keep count.  
   - Process: validate, tar‑compress, rename, list existing archives, delete excess.  
   - Output: new archive file, console logs, exit status.  

2. **Report**:  
   - Input: list of CSV file paths, optional currency filter, min‑revenue threshold.  
   - Process: parse each CSV, filter by currency, aggregate amounts per month/region.  
   - Output: stdout lines `YYYY-MM<TAB>region<TAB>revenue<TAB>currency`, exit status.  

#### Dependencies  
- **`rotate_backups.sh`**: external utilities `tar`, `date`, `du`, `ls`, `rm`, `mv`.  
- **`sales_report.py`**: standard library modules `argparse`, `csv`, `sys`, `collections`, `dataclasses`, `datetime`, `decimal`.  

#### Cross‑cutting  
- Both scripts use strict error handling: exit codes `0`/`1`/`2`, clear usage messages.  
- Timestamp formatting follows UTC ISO‑8601 (`YYYYMMDDTHHMMSSZ`).  
- Data validation: `sales_report.py` checks required CSV columns and numeric/decimal conversion.  
- Cleanup: `rotate_backups.sh` removes temporary partial archives on exit via `trap`.
