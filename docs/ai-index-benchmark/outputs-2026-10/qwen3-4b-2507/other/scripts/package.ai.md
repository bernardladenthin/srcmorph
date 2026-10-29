### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:15:43Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Generates and manages time-based backups and regional revenue reports from order data

#### Purpose
- Automates the creation and rotation of directory backups with configurable retention  
- Processes order data to produce filtered, aggregated monthly revenue reports by region

#### Responsibilities
- Backup rotation and management  
- Order data parsing and revenue aggregation

#### Key units
- `rotate_backups.sh` – creates and manages timestamped, compressed backups of directories with retention control  
- `sales_report.py` – parses CSV order files, filters by currency and revenue threshold, and outputs aggregated monthly revenue per region  

#### Data flow
- Input directory → `rotate_backups.sh` → creates timestamped tar.gz backup → retains only latest N backups  
- CSV order files → `sales_report.py` → parsed into Order dataclass → grouped by month and region → filtered by currency and minimum revenue → output formatted report

#### Dependencies
- `rotate_backups.sh`: GNU tar, GNU date, bash (with array and trap support)  
- `sales_report.py`: csv, argparse, dataclasses, datetime, decimal  

#### Cross-cutting
- Input validation and error reporting (exit codes or exceptions on invalid paths, missing columns, or invalid values)  
- Command-line argument parsing (via argparse)  
- Logging to stdout with clear error messages and action logs  
- Use of dataclasses for structured order representation and type safety
