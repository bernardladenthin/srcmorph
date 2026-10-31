### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:42:55Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A collection of utility scripts for routine data processing and backup management.

#### Purpose
- Automate creation of dated backups for a specified directory.
- Generate monthly sales revenue reports from CSV order exports.

#### Responsibilities
- **Backup Rotation** – create, timestamp, compress, and prune old archive files.
- **Sales Reporting** – parse CSV orders, aggregate revenue by month and region, filter by currency and threshold.

#### Key units
- `rotate_backups.sh` – Bash script that builds `<basename>_YYYYMMDDTHHMMSSZ.tar.gz` archives, keeps a configurable number of recent copies, and removes older ones.
- `sales_report.py` – Python module exposing:
  - `parse_orders(path, currency)` – yields `Order` dataclass instances.
  - `revenue_by_month_and_region(orders)` – returns a sorted mapping of `(YYYY‑MM, region)` to `Decimal` revenue.
  - `main(argv)` – CLI entry point that prints tab‑separated `month<TAB>region<TAB>revenue currency` lines.

#### Data flow
1. **Backup script**  
   - Input: `SOURCE_DIR`, `BACKUP_DIR`, optional `KEEP`.  
   - Process: validate, tar‑compress, timestamp, store, list existing archives, delete excess.  
   - Output: new archive file and console status messages.

2. **Sales report**  
   - Input: list of CSV files, optional `--currency`, `--min-revenue`.  
   - Process: read each file, filter by currency, parse dates/amounts, accumulate totals per month/region.  
   - Output: printed report lines; exit status indicates success or no data.

#### Dependencies
- **rotate_backups.sh** – Bash built‑ins (`tar`, `date`, `du`, `ls`, etc.) and standard Unix utilities.
- **sales_report.py** – Python standard library: `argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`, `sys`.

#### Cross-cutting
- Both scripts enforce strict error handling: exit on invalid input or command failure, with clear stderr messages.
- Timestamp formatting uses UTC (`YYYYMMDDTHHMMSSZ`) for consistency.
- Configuration defaults are hard‑coded (`KEEP=7`, `currency=EUR`, `min_revenue=0`) but can be overridden via arguments.
