### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:13:50Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for retaining timestamped directory backups and producing monthly regional revenue reports from order exports.

#### Purpose
- Create compressed directory backups with configurable archive retention.
- Aggregate matching-currency order CSV data into monthly revenue totals by region.

#### Responsibilities
- **Backup rotation:** Validate inputs, create an atomic `.tar.gz` archive with a UTC timestamp, and delete archives beyond the retention count.
- **Sales reporting:** Parse and validate order CSVs, group revenue by month and region, and print totals that meet a minimum threshold.

#### Key units
- `rotate_backups.sh` — command-line utility to archive one source directory and retain the newest configured number of archives.
- `Order` — immutable `sales_report.py` record containing order date, region, and decimal amount.
- `parse_orders(path, currency)` — validates required CSV columns and yields orders matching the requested currency.
- `revenue_by_month_and_region(orders)` — returns sorted totals grouped by month and region.
- `main(argv=None)` — parses report options and prints qualifying revenue rows.

#### Data flow
- Backup: source directory and destination arguments → temporary archive → timestamped `.tar.gz` → retention cleanup and status output.
- Reporting: CSV files → matching-currency `Order` records → month/region totals → minimum-revenue filter → tab-separated output.

#### Dependencies
- `rotate_backups.sh` uses Bash features and system commands including `tar`, `date`, `mv`, `ls`, and `rm`.
- `sales_report.py` uses Python standard-library modules for CSV and argument parsing, dates, decimal arithmetic, and aggregation.

#### Cross-cutting
- Backup failures terminate under `set -euo pipefail`; an exit trap removes the temporary archive.
- Report amounts use `Decimal`; CSV parse errors for dates and amounts include file and line context.
