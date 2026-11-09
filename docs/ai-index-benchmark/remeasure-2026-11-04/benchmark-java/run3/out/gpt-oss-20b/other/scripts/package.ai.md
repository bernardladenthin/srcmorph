### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:49:47Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A small utility package that backs up directories and generates monthly sales reports from CSV exports.  

#### Purpose  
- Create timestamped, compressed backups and prune old ones.  
- Aggregate order revenue by month and region from CSV files.  

#### Responsibilities  
- **Backup management**: `rotate_backups.sh` handles directory snapshotting, naming, and retention.  
- **Sales reporting**: `sales_report.py` parses CSV orders, filters by currency, aggregates revenue, and outputs a tab‑delimited report.  

#### Key units  
- **`rotate_backups.sh`** – Bash script that validates arguments, creates a `.tar.gz` archive with a UTC timestamp, and deletes older archives beyond a keep count.  
- **`sales_report.py`** – Python module exposing `parse_orders`, `revenue_by_month_and_region`, and `main`; processes CSV files, aggregates revenue, and prints results.  

#### Data flow  
1. **Backup**:  
   - Input: source directory, backup directory, optional keep count.  
   - Process: validate, tar‑compress, move to final name, list and prune older archives.  
   - Output: new archive file, console messages.  

2. **Sales report**:  
   - Input: list of CSV paths, currency filter, min‑revenue threshold.  
   - Process: parse each CSV, filter by currency, accumulate totals per `(month, region)` using `defaultdict(Decimal)`.  
   - Output: tab‑separated lines `month<TAB>region<TAB>revenue currency`.  

#### Dependencies  
- **`rotate_backups.sh`**: external commands `tar`, `date`, `du`, `ls`, `rm`, `mv`.  
- **`sales_report.py`**: standard library modules `argparse`, `csv`, `sys`, `collections`, `dataclasses`, `datetime`, `decimal`.  

#### Cross-cutting  
- Both units enforce strict input validation and provide clear exit codes (`0` success, `1` or `2` error).  
- Error handling uses exceptions (`ValueError`, `decimal.InvalidOperation`) in Python and shell `exit 2` for argument issues.  
- Both scripts output informative console messages and clean up temporary artifacts (`trap` in Bash, no temp files in Python).
