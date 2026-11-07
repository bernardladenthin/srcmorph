### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:27:21Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Automates backup rotation and generates monthly sales summaries from CSV order exports.

#### Purpose
- Maintain a rolling set of compressed backups for a specified directory.
- Produce revenue reports grouped by month and region from order CSV files.

#### Responsibilities
- **Backup Rotation**: Create timestamped `.tar.gz` archives, prune older ones, and log actions.
- **Sales Reporting**: Parse CSV orders, filter by currency, aggregate revenue, and output tab‑separated summaries.

#### Key units
- `rotate_backups.sh` – Bash script that builds and manages backup archives.
- `sales_report.py` – Python script exposing `parse_orders`, `revenue_by_month_and_region`, and `main` for CLI usage.

#### Data flow
1. **Backup**:  
   `SOURCE_DIR → tar → temp archive → final archive → BACKUP_DIR`  
   Existing archives are listed, sorted, and the oldest beyond `KEEP` are removed.
2. **Sales Report**:  
   `CSV file → parse_orders → Order dataclass → revenue_by_month_and_region → sorted totals → stdout`.

#### Dependencies
- **Shell**: `tar`, `date`, `du`, `ls`, `rm`, `mv`, `basename`, `dirname`, `cut`.
- **Python**: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- Both tools enforce strict input validation and provide clear exit codes (`0` success, `1/2` error).  
- Logging is performed via standard output, with removal actions echoed for transparency.  
- Error handling uses simple exit status checks and exception propagation for malformed data.
