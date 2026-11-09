### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:11:56Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A utility package that provides command‑line tools for backing up directories and generating monthly sales reports from CSV order exports.  

#### Purpose
- Automate creation and rotation of compressed directory backups.  
- Aggregate and report sales revenue by month and region from CSV data.

#### Responsibilities
- **Backup rotation**: create timestamped `.tar.gz` archives, prune old ones.  
- **Sales reporting**: parse CSV orders, filter by currency, aggregate revenue, output tab‑separated report.

#### Key units
- `rotate_backups.sh` – Bash script that validates arguments, creates a compressed backup, and deletes older archives beyond a keep count.  
- `sales_report.py` – Python script exposing `parse_orders`, `revenue_by_month_and_region`, and `main` to generate revenue summaries.

#### Data flow
1. **Backup**:  
   `SOURCE_DIR` → `tar` → temporary archive → final `.tar.gz` → `BACKUP_DIR`.  
   Existing archives are listed, sorted, and the oldest beyond `KEEP` are removed.
2. **Sales report**:  
   CSV files → `parse_orders` → `Order` objects → `revenue_by_month_and_region` → sorted totals → printed report.

#### Dependencies
- **Shell**: `tar`, `date`, `du`, `ls`, `rm`, `mkdir`.  
- **Python**: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.  
- No external libraries beyond the standard library.

#### Cross-cutting
- Strict error handling: exit codes `0`/`1`/`2`, `ValueError` for malformed input.  
- Consistent use of UTC timestamps for backup naming.  
- Input validation for required columns and positive integer `KEEP`.  
- Clean‑up via `trap` in Bash and exception propagation in Python.
