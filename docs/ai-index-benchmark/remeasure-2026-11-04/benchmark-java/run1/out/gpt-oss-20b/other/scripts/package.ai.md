### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:19:00Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A small utility bundle that creates dated backups and produces aggregated sales reports from CSV exports.

#### Purpose
- Automate directory backups with retention policy.
- Generate monthly revenue summaries by region from order CSVs.

#### Responsibilities
- **Backup management** – `rotate_backups.sh` handles timestamped archiving and pruning.
- **Data aggregation** – `sales_report.py` parses CSV orders, filters by currency, aggregates revenue, and outputs a tab‑separated report.

#### Key units
- **`rotate_backups.sh`** – Bash script that creates a `.tar.gz` archive of a source directory, names it with UTC timestamp, and deletes older archives beyond a configurable keep count.
- **`sales_report.py`** – Python module exposing:
  - `parse_orders(path, currency)` – yields `Order` objects for rows matching the target currency.
  - `revenue_by_month_and_region(orders)` – returns a sorted mapping of `(YYYY‑MM, region)` to total revenue (`Decimal`).
  - `main(argv)` – CLI entry point that orchestrates parsing, aggregation, and printing.

#### Data flow
1. **Backup script**  
   - Input: source directory, backup directory, optional keep count.  
   - Process: validate, tar‑compress, move to final name, list and prune older archives.  
   - Output: new archive file, console messages, exit status.

2. **Sales report**  
   - Input: list of CSV files, target currency, minimum revenue threshold.  
   - Process: parse each file, filter by currency, aggregate by month/region, filter by threshold.  
   - Output: printed report lines, exit status.

#### Dependencies
- **`rotate_backups.sh`** – external utilities: `tar`, `date`, `du`, `ls`, `rm`, `mv`; shell built‑ins `basename`, `dirname`.
- **`sales_report.py`** – standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- Both units use strict error handling: non‑zero exit codes on invalid input or missing data.
- Logging is minimal, via `echo` or `print` statements.
- No shared code; each script is self‑contained.
