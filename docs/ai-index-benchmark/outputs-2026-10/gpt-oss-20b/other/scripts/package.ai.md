### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:54:10Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides utilities for backing up directories and generating monthly sales reports from CSV order data.

#### Purpose
- Automate creation and rotation of compressed directory backups.
- Aggregate and report monthly sales revenue by region from CSV exports.

#### Responsibilities
- **Backup Management** – create timestamped `.tar.gz` archives, keep a configurable number of recent backups, and clean older ones.
- **Sales Reporting** – parse order CSVs, filter by currency, aggregate revenue per month and region, and output formatted results.

#### Key units
- `rotate_backups.sh` – Bash script that validates arguments, builds UTC‑timestamped archive names, creates archives, and removes excess files.
- `sales_report.py` – Python module exposing:
  - `Order` dataclass (order_date, region, amount, currency).
  - `parse_orders(path, currency)` – yields `Order` objects from a CSV.
  - `revenue_by_month_and_region(orders)` – returns a sorted dict of `(month, region)` → `Decimal` totals.
  - `main(argv)` – CLI entry point handling arguments, collecting orders, and printing filtered totals.

#### Data flow
1. **Backup**: `rotate_backups.sh <src> <dest> [keep]` → validates → creates `<src>_YYYYMMDDTHHMMSSZ.tar.gz` → lists existing archives → deletes older ones beyond `keep`.
2. **Sales Report**: `sales_report.py [files] [--currency] [--min-revenue]` → `parse_orders` reads each file → yields `Order` → `revenue_by_month_and_region` aggregates → `main` prints rows meeting `min_revenue`.

#### Dependencies
- **Shell**: `tar`, `date`, `du`, `ls`, `basename`, `dirname`, `mkdir`, `rm`, `echo`.
- **Python**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Cross-cutting
- Error handling: both scripts exit with non‑zero status on invalid input or missing data.
- Logging: simple `echo`/`print` statements provide progress and cleanup messages.
- Configuration: backup retention count is a positional argument with a default; sales report currency and minimum revenue are optional flags.
