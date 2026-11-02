### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:46:14Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides standalone utilities for rotating compressed directory backups and generating currency-filtered monthly regional sales reports from CSV orders.

#### Purpose
- Create timestamped archives with configurable retention of older backups.
- Aggregate order exports into monthly revenue totals by region and selected currency.

#### Responsibilities
- **Backup rotation (`rotate_backups.sh`):** Validate source and retention settings, create a gzip tar archive, and remove archives beyond the retention limit.
- **Sales reporting (`sales_report.py`):** Parse order CSVs, filter by currency, aggregate revenue by month and region, and print totals meeting a minimum threshold.

#### Key units
- `rotate_backups.sh` — command-line backup creation and retention workflow; defaults to keeping seven archives.
- `Order` — frozen record containing an order date, region, and decimal amount.
- `parse_orders(path, currency)` — yield validated `Order` records for matching-currency CSV rows.
- `revenue_by_month_and_region(orders)` — return sorted monthly revenue totals keyed by `(YYYY-MM, region)`.
- `main(argv=None)` — parse report options, combine input files, filter totals, and print the report.

#### Data flow
- **Backups:** Source directory → timestamped `.tar.gz.partial` archive → renamed final archive → newest-first retention scan → deletion of excess archives.
- **Sales report:** CSV files → required-column and row validation → currency-filtered `Order` records → monthly/region aggregation → minimum-revenue filter → tab-separated output.

#### Dependencies
- `rotate_backups.sh` uses Bash and system utilities including `tar`, `date`, `mkdir`, `mv`, `du`, `ls`, and `rm`.
- `sales_report.py` uses Python standard-library CSV, argument parsing, dates, decimals, and collection utilities.

#### Cross-cutting
- Both are standalone command-line utilities with positional or option-based inputs and explicit status/output behavior.
- Backup failures terminate under strict Bash settings; an `EXIT` trap removes an incomplete archive.
- Sales input validation reports malformed matching rows with file and line context; missing columns raise `ValueError`, while file and CSV-reading errors are unhandled.
