### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:46:44Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for rotating compressed directory backups and reporting monthly regional sales revenue from order CSVs.

#### Purpose
- Create timestamped backups with configurable retention.
- Aggregate matching-currency orders into monthly revenue totals by region, with an optional minimum-revenue filter.

#### Responsibilities
- **Backup rotation:** Archive a source directory as a gzip-compressed tar file, publish it via a temporary `.partial` path, and remove older matching archives beyond the retention count.
- **Sales reporting:** Parse order CSVs, skip rows in other currencies, aggregate amounts by month and region, and print qualifying totals.

#### Key units
- `rotate_backups.sh` — validates inputs, creates the dated archive, and enforces backup retention.
- `Order` — frozen dataclass holding an order’s date, region, and amount.
- `parse_orders(path, currency)` — validates CSV headers and yields parsed orders matching the selected currency.
- `revenue_by_month_and_region(orders)` — returns sorted monthly revenue totals grouped by region.
- `main(argv=None)` — processes sales-report arguments and prints tab-separated results.

#### Data flow
- **Backups:** Source directory → temporary tar.gz archive → final timestamped archive → newest-first retention cleanup.
- **Sales report:** One or more CSV files → currency-filtered `Order` records → month/region aggregation → minimum-revenue filter → standard output.

#### Dependencies
- `rotate_backups.sh` uses Bash and system commands including `tar`, `date`, `mv`, and `rm`; reads and writes the source and backup directories.
- `sales_report.py` uses Python standard-library CSV, argument parsing, dates, decimals, and collections; reads CSV files and writes results to standard output.

#### Cross-cutting
- Both scripts are command-line utilities with explicit inputs and output behavior.
- Backup creation uses an `EXIT` trap to remove partial archives; sales CSV validation reports malformed headers or row values through `ValueError`.
