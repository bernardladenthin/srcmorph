### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:52:52Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A small utility package that backs up directories and produces monthly sales reports from CSV order exports.  

#### Purpose  
- Provide a shell script to create timestamped tar.gz backups and prune old ones.  
- Offer a Python tool to aggregate order revenue by month and region, filtered by currency and threshold.  

#### Responsibilities  
- **Backup management**: directory validation, archive creation, retention policy.  
- **Sales reporting**: CSV parsing, currency filtering, revenue aggregation, CLI interface.  

#### Key units  
- `rotate_backups.sh` – Bash script that creates `"$BACKUP_DIR/${name}_<timestamp>.tar.gz"` and removes older archives beyond the configured `KEEP`.  
- `sales_report.py` – Python module exposing  
  - `parse_orders(path, currency)` – yields `Order` dataclass instances from CSV.  
  - `revenue_by_month_and_region(orders)` – returns a sorted dict of `(YYYY‑MM, region) → Decimal` totals.  
  - `main(argv)` – CLI entry point that prints tab‑separated `month<TAB>region<TAB>revenue currency`.  

#### Data flow  
1. **Backup**:  
   - Input: source directory, backup directory, optional keep count.  
   - Process: validate, tar‑compress, timestamp, prune.  
   - Output: new archive file, optional deletion messages.  

2. **Sales report**:  
   - Input: list of CSV files, target currency, min revenue.  
   - Process: parse each CSV, filter by currency, aggregate by month/region.  
   - Output: printed report lines; exit status indicates success or no data.  

#### Dependencies  
- Bash utilities: `tar`, `du`, `ls`, `date`.  
- Python standard library: `argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`, `sys`.  

#### Cross-cutting  
- Both tools use UTC timestamps for consistency.  
- Error handling is explicit: shell exits with `2` on validation errors; Python raises `ValueError` for malformed CSV data.  
- Both provide clear, concise command‑line interfaces with helpful usage messages.
