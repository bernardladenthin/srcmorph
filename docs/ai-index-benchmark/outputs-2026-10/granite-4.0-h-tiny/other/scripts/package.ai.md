### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:10:19Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Automated backup creation and sales revenue reporting package

#### Purpose
- Automates directory backups and generates monthly sales reports

#### Responsibilities
- Backup creation and cleanup
- Sales revenue calculation per region

#### Key units
- `rotate_backups.sh`: Creates compressed, dated backups and retains only the newest N archives
- `sales_report.py`: Calculates monthly sales revenue per region for a given currency, filtering out orders below a minimum revenue threshold

#### Data flow
- `rotate_backups.sh` compresses source directory and moves archive to backup directory, retaining only the most recent backups
- `sales_report.py` parses CSV files, aggregates revenue totals by month and region, and filters results based on minimum revenue criteria

#### Dependencies
- `rotate_backups.sh` depends on `tar`, `date`, `ls`, and `mapfile`
- `sales_report.py` depends on `argparse`, `csv`, `sys`, `collections.defaultdict`, and `datetime.date`

#### Cross-cutting
- Uses standard shell utilities and Python standard libraries
- Implements error handling for parameter validation and file processing
- Provides clear command-line interfaces for both scripts
