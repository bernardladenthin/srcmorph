### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:23:59Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for rotating directory backups and reporting monthly regional order revenue from CSV exports.

#### Purpose
- Create timestamped compressed backups with configurable retention.
- Aggregate order data into currency-filtered monthly revenue reports by region.

#### Responsibilities
- **Backup rotation:** Validate source and retention settings, create a `.tar.gz` archive, and remove older matching archives beyond the retention limit.
- **Sales reporting:** Parse order CSVs, aggregate revenue by month and region, filter by minimum revenue, and print qualifying results.

#### Key units
- `rotate_backups.sh` — archives a source directory under a UTC-timestamped name and retains the newest configured number of backups.
- `Order` — frozen dataclass holding an order date, region, and decimal amount.
- `REQUIRED_COLUMNS` — required CSV field names for order parsing.
- `parse_orders(path, currency)` — validates CSV columns and yields matching-currency orders.
- `revenue_by_month_and_region(orders)` — returns sorted revenue totals grouped by `YYYY-MM` and region.
- `main(argv=None)` — runs the sales report CLI and returns its exit code.

#### Data flow
- **Backups:** Source directory and retention count → validation → `.partial` tar archive → final timestamped archive → prune older matching archives.
- **Sales report:** CSV paths → currency-filtered `Order` records → monthly and regional totals → minimum-revenue filter → tab-separated output.

#### Dependencies
- Backup rotation uses Bash utilities and external commands including `tar`, `mkdir`, `mv`, `du`, `ls`, and `rm`.
- Sales reporting uses Python standard-library CSV, argument parsing, dates, decimals, and aggregation utilities.

#### Cross-cutting
- Backup rotation uses `set -euo pipefail` and an exit trap to clean up incomplete `.partial` archives.
- Sales parsing reports invalid matching-row data with file path and line number; unmatched reports print `no orders found` to stderr and return exit code `1`.
