### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:49:35Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A collection of utilities for managing backups and generating sales revenue reports from CSV exports.  

#### Purpose  
- Automate directory backup rotation with timestamped archives.  
- Produce monthly revenue summaries per region, filtered by currency and threshold.  

#### Responsibilities  
- **Backup Management** – create, timestamp, and prune compressed archives of a source directory.  
- **Sales Reporting** – parse order CSVs, aggregate revenue by month and region, and output filtered results.  

#### Key units  
- `rotate_backups.sh` – Bash script that creates a `tar.gz` backup, keeps a configurable number of recent archives, and removes older ones.  
- `sales_report.py` – Python module exposing `parse_orders`, `revenue_by_month_and_region`, and `main` to generate revenue reports.  
- `Order` dataclass – represents a single order record with `order_date`, `region`, `amount`, and `currency`.  

#### Data flow  
1. **Backup**:  
   - Input: source directory, backup destination, optional keep count.  
   - Process: validate inputs → create temporary archive → move to final path → list existing archives → delete excess.  
   - Output: new archive file, console logs, exit status.  

2. **Sales Report**:  
   - Input: list of CSV files, target currency, minimum revenue.  
   - Process: parse each CSV → filter by currency → convert fields → aggregate by `(month, region)` → filter by threshold.  
   - Output: tab‑separated lines `month<TAB>region<TAB>revenue currency`, exit status.  

#### Dependencies  
- **rotate_backups.sh**: external commands `tar`, `date`, `du`, `ls`, `rm`, `mv`; utilities `basename`, `dirname`.  
- **sales_report.py**: standard libraries `argparse`, `csv`, `sys`, `collections`, `dataclasses`, `datetime`, `decimal`.  

#### Cross-cutting  
- Both utilities enforce strict error handling: exit codes (`0` success, `1` or `2` failure), validation of inputs, and cleanup of temporary resources.  
- Logging is performed via `echo` (shell) or `print` (Python) with clear messages for creation and deletion events.  
- Configuration is minimal and driven by command‑line arguments or positional parameters.
