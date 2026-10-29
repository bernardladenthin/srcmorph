### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:58:30Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Automates data backup and generates aggregated sales reports from CSV exports.

#### Purpose
- Automate creation and retention of directory backups.
- Produce monthly revenue summaries by region from order CSVs.

#### Responsibilities
- **Backup Management** – create timestamped `.tar.gz` archives, enforce retention policy.
- **Data Processing** – parse CSV orders, filter by currency, aggregate revenue per month/region, output formatted report.

#### Key units
- `rotate_backups.sh` – Bash script that validates inputs, builds archive names, performs compression, and cleans old backups.
- `sales_report.py` – Python module exposing:
  - `Order` dataclass (order_date, region, amount, currency).
  - `parse_orders(path, currency)` – yields `Order` objects, validates CSV structure.
  - `revenue_by_month_and_region(orders)` – returns `Dict[(YYYY‑MM, region), Decimal]` totals.
  - `main(argv)` – CLI entry point handling arguments and printing results.

#### Data flow
1. **Backup**: `rotate_backups.sh` receives source and target directories, creates a compressed archive named with UTC timestamp, then deletes older archives beyond the keep count.
2. **Report**: `sales_report.py` reads CSV files supplied on the command line, filters rows by currency, converts dates and amounts, aggregates totals per month/region, and prints lines where revenue meets the minimum threshold.

#### Dependencies
- Bash utilities: `tar`, `date`, `du`, `ls`, `basename`, `dirname`, `mkdir`, `rm`, `echo`.
- Python libraries: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- Strict error handling (`set -euo pipefail` in Bash; `ValueError` propagation in Python).
- Consistent use of UTC timestamps for naming and reporting.
- Modular design: separate scripts for backup and reporting, each with a clear public API.
