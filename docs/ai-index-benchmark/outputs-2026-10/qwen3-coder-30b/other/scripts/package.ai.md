### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:21:53Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Automates backup management and generates revenue reports from CSV data.

#### Purpose
- Automates creation and retention of compressed directory backups
- Processes order data to produce currency-filtered monthly revenue summaries

#### Responsibilities
- Backup management: generates timestamped tar.gz archives and enforces retention limits
- Data processing: reads CSV files, aggregates revenue by region and month, applies filters

#### Key units
- [rotate_backups.sh](rotate_backups.sh.ai.md): shell script for backup archiving and cleanup
- [sales_report.py](sales_report.py.ai.md): Python script for revenue aggregation and reporting
- [Order](sales_report.py.ai.md#L14): dataclass representing individual order records
- [revenue_by_month_and_region](sales_report.py.ai.md#L42): function aggregating order amounts by time and region

#### Data flow
- Backup process: input directory → timestamped archive → retention cleanup
- Report generation: CSV files → order parsing → revenue aggregation → filtered output

#### Dependencies
- External: `tar`, `date`, `ls`, `du` commands for backup script
- Internal: [argparse](sales_report.py.ai.md#L1), [csv](sales_report.py.ai.md#L2), [sys](sales_report.py.ai.md#L3), [defaultdict](sales_report.py.ai.md#L5), [dataclass](sales_report.py.ai.md#L14), [date](sales_report.py.ai.md#L17), [Decimal](sales_report.py.ai.md#L18) for report script

#### Cross-cutting
- Error handling: both scripts exit with codes 1/2 on failure, report to stderr
- Input validation: scripts verify parameters and file existence before processing
- Date/time handling: [date](sales_report.py.ai.md#L17) parsing in Python, [date](sales_report.py.ai.md#L17) command usage in shell
- Data types: [Decimal](sales_report.py.ai.md#L18) for precise monetary calculations, [defaultdict](sales_report.py.ai.md#L5) for efficient aggregation
