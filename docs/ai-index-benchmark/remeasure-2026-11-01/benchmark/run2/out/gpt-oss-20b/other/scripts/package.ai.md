### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:48:22Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A collection of command‑line utilities for managing backups and generating sales analytics from CSV exports.  

#### Purpose  
- Automate directory backup rotation with timestamped archives.  
- Produce monthly revenue summaries by region from order CSV files.  

#### Responsibilities  
- **Backup rotation**: validate inputs, create compressed archives, enforce retention policy.  
- **Sales reporting**: parse CSV orders, filter by currency, aggregate revenue, output tab‑separated results.  

#### Key units  
- **rotate_backups.sh** – Bash script that creates `BACKUP_DIR/<basename>_YYYYMMDDTHHMMSSZ.tar.gz`, keeps the newest N archives, and reports actions.  
- **sales_report.py** – Python module exposing `parse_orders`, `revenue_by_month_and_region`, and `main`; outputs `month<TAB>region<TAB>revenue currency`.  

#### Data flow  
1. **Backup script**  
   - Input: `SOURCE_DIR`, `BACKUP_DIR`, optional `KEEP`.  
   - Process: validate, tar‑compress, timestamp, move, list, delete old archives.  
   - Output: new archive file, console logs, exit status.  

2. **Sales report**  
   - Input: list of CSV file paths, optional `--currency`, `--min-revenue`.  
   - Process: read each file, yield `Order` objects, aggregate by `(YYYY‑MM, region)`.  
   - Output: printed lines for each qualifying month‑region pair, exit status.  

#### Dependencies  
- **rotate_backups.sh**: `tar`, `date`, `du`, `ls`, `rm`, `mv`, `echo`.  
- **sales_report.py**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.  

#### Cross-cutting  
- Both utilities use shell/command‑line interfaces and exit codes to signal success or failure.  
- Error handling is explicit: `rotate_backups.sh` exits `2` on bad arguments; `sales_report.py` exits `1` if no orders are found.  
- Logging is performed via standard output; no external configuration files are referenced.
