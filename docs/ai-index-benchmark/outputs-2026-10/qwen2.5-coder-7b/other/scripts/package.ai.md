### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:18:27Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Manages backup rotation and sales report generation for specified directories and regions.

#### Purpose
- Rotates backups of a specified directory, keeping only the newest N archives.
- Generates a monthly sales report for specified regions based on order CSV exports.

#### Responsibilities
- Backup management.
- Sales report generation.

#### Key units
- `rotate_backups.sh`: Bash script for rotating backups.
- `sales_report.py`: Python script for generating sales reports.

#### Data flow
- `rotate_backups.sh` reads `<source_dir>`, creates a backup archive, and deletes older archives.
- `sales_report.py` reads `csv_files`, calculates revenue per month and region, and prints revenue reports.

#### Dependencies
- `rotate_backups.sh`: Bash, tar, ls, rm, du.
- `sales_report.py`: argparse, csv, sys, collections.defaultdict, dataclasses.dataclass, datetime.date, decimal.Decimal.

#### Cross-cutting
- Bash scripts handle file operations.
- Python scripts use standard libraries for data processing.
- Both scripts validate input parameters and handle exceptions.
