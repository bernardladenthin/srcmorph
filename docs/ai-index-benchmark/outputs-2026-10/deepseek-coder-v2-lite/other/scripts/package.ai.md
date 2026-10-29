### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T02:47:51Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Creates compressed, dated backups of directories and generates sales reports with VAT calculations.

#### Purpose
- Create a compressed, dated backup of a directory.
- Keep only the newest N archives.
- Reads order CSV exports.
- Prints revenue per month and region.

#### Responsibilities
- **Backup Creation**: `rotate_backups.sh`
  - Create a compressed, dated backup of a directory.
  - Keep only the newest N archives.
- **Sales Report Generation**: `sales_report.py`
  - Reads order CSV exports.
  - Prints revenue per month and region.

#### Key units
- `rotate_backups.sh`: Shell script for backup creation.
- `sales_report.py`: Python script for generating sales reports.

#### Data flow
- `rotate_backups.sh` takes `<source_dir>` and `<backup_dir>` as inputs and outputs compressed backups in `<backup_dir>`.
- `sales_report.py` takes CSV file paths and a currency as inputs, processes them to output monthly revenue per region.

#### Dependencies
- `rotate_backups.sh` depends on `bash` utilities like `basename`, `date`, `dirname`, `tar`, `mkdir`, `ls`, `cut`, `du`, `mapfile`.
- `sales_report.py` depends on Python libraries like `argparse`, `csv`, `sys`, `collections.defaultdict`, `datetime.date`, `decimal.Decimal`.

#### Cross-cutting
- Both scripts use optional parameters and handle exceptions/errors gracefully.
- `sales_report.py` uses a minimum revenue threshold for filtering results.

This structured summary provides a clear overview of the package's responsibilities and the key units involved in its functionality.
