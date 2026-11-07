### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:03:59Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A small utility package that backs up directories and generates monthly sales reports from CSV exports.  

#### Purpose
- Automate creation and rotation of compressed directory backups.  
- Produce revenue summaries by month and region from order CSV files.  

#### Responsibilities
- **Backup Management** – `rotate_backups.sh` handles timestamped tarball creation and retention policy.  
- **Sales Reporting** – `sales_report.py` parses order CSVs, aggregates revenue, and outputs tabular reports.  

#### Key units
- **`rotate_backups.sh`** – Bash script that validates inputs, creates a `.tar.gz` archive, and deletes old backups beyond a keep count.  
- **`sales_report.py`** – Python module exposing:
  - `parse_orders(path, currency)` – yields `Order` dataclass instances from a CSV.  
  - `revenue_by_month_and_region(orders)` – aggregates revenue per `(YYYY‑MM, region)`.  
  - `main(argv=None)` – CLI entry point that orchestrates parsing, aggregation, and printing.  

#### Data flow
1. **Backup**:  
   - User supplies source and backup directories plus optional keep count.  
   - Script validates, creates a timestamped archive, then trims older archives.  
2. **Reporting**:  
   - User supplies CSV files and optional currency/min‑revenue filters.  
   - `parse_orders` reads each file, filters by currency, converts dates and amounts.  
   - `revenue_by_month_and_region` sums amounts per month/region.  
   - `main` prints rows that meet the minimum revenue threshold.  

#### Dependencies
- **Shell**: `tar`, `date`, `du`, `ls`, `rm`, `mv`, `basename`, `dirname`.  
- **Python**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.  

#### Cross-cutting
- Both units enforce strict input validation and clear error handling.  
- The backup script uses `set -euo pipefail` for safety; the Python script raises `ValueError` on malformed CSV data.  
- Configuration is minimal: backup keep count defaults to 7; report currency defaults to EUR.
