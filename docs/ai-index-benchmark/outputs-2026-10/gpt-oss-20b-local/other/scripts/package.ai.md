### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:22:32Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Automates backup rotation and generates monthly sales revenue reports from CSV order exports.  

#### Purpose  
- Provide a reusable backup script that creates timestamped archives and enforces retention limits.  
- Offer a CLI tool to aggregate sales revenue by month and region, filtering by currency and threshold.  

#### Responsibilities  
- **Backup Rotation**: directory compression, naming, retention logic.  
- **Sales Reporting**: CSV parsing, data validation, revenue aggregation, CLI handling.  

#### Key units  
- `rotate_backups.sh`: Bash script; public API `rotate_backups.sh <src> <dest> [keep]`.  
- `sales_report.py`: Python module; key functions `parse_orders`, `revenue_by_month_and_region`, `main`.  
- `Order` dataclass: represents an order with `order_date`, `region`, `amount`, `currency`.  

#### Data flow  
1. **Backup**:  
   - Input: source directory, destination, optional keep count.  
   - Process: validate, tar‑gzip, timestamped naming, list & delete old archives.  
   - Output: new `.tar.gz` file, console logs.  

2. **Sales Report**:  
   - Input: one or more CSV files, optional `--currency`, `--min-revenue`.  
   - Process: `parse_orders` → yields `Order`; `revenue_by_month_and_region` aggregates; `main` prints results.  
   - Output: TSV lines `month<TAB>region<TAB>revenue currency`.  

#### Dependencies  
- **Backup**: `tar`, `date`, `du`, `basename`, `ls`, `mapfile`.  
- **Report**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.  

#### Cross-cutting  
- Both scripts enforce strict error handling (`set -euo pipefail` in Bash; `ValueError` propagation in Python).  
- Logging to stdout for status and deletion notices.  
- Use of standard utilities and libraries for portability and reliability.
