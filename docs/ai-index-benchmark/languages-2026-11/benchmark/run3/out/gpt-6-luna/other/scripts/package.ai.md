### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:01:34Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides command-line utilities for rotating dated directory backups and producing currency-filtered monthly regional sales reports from order CSVs.

#### Purpose
- Create compressed backups with configurable retention and remove older archives.
- Aggregate order data into monthly revenue totals by region for a selected currency.

#### Responsibilities
- **Backup rotation (`rotate_backups.sh`):** Archive a source directory as a UTC-dated `.tar.gz`, retain the newest requested number, and clean up partial archives on exit.
- **Sales reporting (`sales_report.py`):** Validate and parse order CSVs, aggregate matching-currency amounts by month and region, and print totals meeting an optional threshold.

#### Key units
- `rotate_backups.sh` — accepts source directory, backup directory, and optional retention count; creates and rotates archives.
- `Order` — frozen dataclass storing an order’s date, region, and amount.
- `parse_orders(path, currency)` — validates CSV headers and yields orders matching the requested currency.
- `revenue_by_month_and_region(orders)` — returns sorted monthly revenue totals keyed by month and region.
- `main(argv=None)` — parses report options, combines CSV inputs, and prints qualifying totals.

#### Data flow
- **Backups:** Positional arguments → source validation → temporary compressed archive → final dated archive → newest-first retention cleanup.
- **Sales:** CSV files → required-column validation and currency filtering → monthly/region aggregation → minimum-revenue filtering → tab-separated output.

#### Dependencies
- `rotate_backups.sh` uses Bash built-ins and system commands including `tar`, `date`, `ls`, `mv`, and `rm`.
- `sales_report.py` uses Python standard-library modules for CSV and argument parsing, dates, decimal amounts, and aggregation.

#### Cross-cutting
- Both utilities are command-line oriented and accept configuration through arguments.
- Backup failures terminate under Bash strict mode; invalid retention counts and missing source directories exit with status `2`.
- Sales CSV schema or row-value errors raise `ValueError`; no matching orders prints an error to stderr and returns status `1`.
