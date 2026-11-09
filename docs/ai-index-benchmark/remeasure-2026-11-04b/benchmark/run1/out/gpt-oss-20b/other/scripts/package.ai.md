### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:55:21Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A small utility package that backs up directories and generates monthly sales reports from CSV order exports.

#### Purpose
- Automate creation and pruning of compressed directory backups.
- Aggregate and report revenue by month and region from order CSV files.

#### Responsibilities
- **Backup Management** – create timestamped `.tar.gz` archives, enforce retention policy.
- **Sales Reporting** – parse CSV orders, filter by currency, aggregate revenue, output tab‑separated report.

#### Key units
- `rotate_backups.sh` – Bash script that validates arguments, builds archive names, creates archives with `tar`, and removes old backups.
- `sales_report.py` – Python module exposing:
  - `parse_orders(path, currency)` – yields `Order` dataclass instances.
  - `revenue_by_month_and_region(orders)` – returns `Dict[(YYYY‑MM, region), Decimal]`.
  - `main(argv)` – CLI entry point handling arguments and printing the report.

#### Data flow
1. **Backup**: `rotate_backups.sh` receives source and target directories, creates a compressed archive, then lists existing archives to delete those beyond the keep count.
2. **Reporting**: `sales_report.py` reads each CSV file, filters rows by currency, converts dates and amounts, aggregates totals per month/region, then prints rows that meet the minimum revenue threshold.

#### Dependencies
- **Shell**: `tar`, `date`, `du`, `ls`, `rm`, `mkdir`, `mv`.
- **Python**: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- Both utilities enforce strict error handling (`set -euo pipefail` in Bash; explicit `ValueError` checks in Python).
- Input validation: positive integer for keep count; required CSV columns; valid currency and amount parsing.
- Output conventions: Bash prints status messages; Python prints tab‑separated report lines.
