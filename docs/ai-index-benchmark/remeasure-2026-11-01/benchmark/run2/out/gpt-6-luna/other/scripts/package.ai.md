### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:45:07Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for rotating compressed directory backups and producing currency-filtered monthly regional revenue reports from order CSVs.

#### Purpose
- Create timestamped archives with configurable retention for a source directory.
- Aggregate order CSV data into monthly revenue totals by region and currency.

#### Responsibilities
- **Backup rotation:** Validate inputs, create a gzip-compressed archive, report its path and size, and remove older matching archives beyond the retention limit.
- **Sales reporting:** Validate and parse CSV orders, filter by currency, aggregate revenue by month and region, apply a minimum-revenue threshold, and print a tab-separated report.

#### Key units
- `rotate_backups.sh` — command-line backup and retention utility; defaults to keeping seven archives.
- `Order` — immutable record of an order date, region, and decimal amount.
- `parse_orders(path, currency)` — validates CSV columns and yields orders matching the requested currency.
- `revenue_by_month_and_region(orders)` — returns sorted revenue totals keyed by `(YYYY-MM, region)`.
- `main(argv=None)` — parses report options, combines input files, and prints results.

#### Data flow
- **Backups:** Source directory → temporary `.partial` tarball → timestamped `.tar.gz` archive → newest-first retention cleanup.
- **Sales report:** CSV files → column and row validation → currency-matched `Order` records → monthly regional aggregation → minimum-revenue filtering → tab-separated output.

#### Dependencies
- `rotate_backups.sh` uses Bash and external utilities including `tar`, `date`, `ls`, `du`, and `rm`.
- `sales_report.py` uses Python standard-library CSV, argument parsing, date, decimal, and collection modules.

#### Cross-cutting
- The backup script uses strict Bash settings and an `EXIT` trap to remove temporary archives.
- The report uses `Decimal` for revenue amounts and provides file and line context for malformed rows.
