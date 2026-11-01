### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:06:33Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A small utility package that provides a shell script for rotating directory backups and a Python script for summarizing sales revenue from CSV exports.  

#### Purpose  
- Automate creation and retention of timestamped backups.  
- Generate monthly, region‑based sales revenue reports from CSV data.  

#### Responsibilities  
- **Backup rotation**: compress a source directory, timestamp the archive, and keep only the most recent N copies.  
- **Sales reporting**: parse CSV order files, filter by currency and revenue threshold, aggregate by month and region, and output tab‑separated summaries.  

#### Key units  
- `rotate_backups.sh` – Bash script that validates inputs, creates a `.tar.gz` archive, and deletes older backups.  
- `sales_report.py` – Python module exposing `parse_orders`, `revenue_by_month_and_region`, and `main` for CLI usage.  
- `Order` dataclass – represents a single order record with `order_date`, `region`, `amount`, and `currency`.  

#### Data flow  
1. **Backup script**  
   - Input: source directory, backup directory, optional keep count.  
   - Process: validate, tar‑compress, timestamp, move to final name, list existing archives, delete excess.  
   - Output: new archive file and optional deletion messages.  

2. **Sales report**  
   - Input: one or more CSV files, optional `--currency` and `--min-revenue`.  
   - Process: read each file, filter rows, convert fields, aggregate amounts by `(YYYY‑MM, region)`.  
   - Output: printed lines `month<TAB>region<TAB>revenue currency`.  

#### Dependencies  
- **Shell**: `tar`, `date`, `du`, `ls`, `rm`, `mkdir`, `mv`.  
- **Python**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.  

#### Cross-cutting  
- Both utilities enforce strict input validation and clear error handling.  
- The backup script uses `set -euo pipefail` for safety; the Python script raises `ValueError` for malformed CSV data.  
- Timestamp format `YYYYMMDDTHHMMSSZ` (UTC) is consistently used for backup filenames.  
- Currency filtering and revenue thresholding are configurable via command‑line options.
