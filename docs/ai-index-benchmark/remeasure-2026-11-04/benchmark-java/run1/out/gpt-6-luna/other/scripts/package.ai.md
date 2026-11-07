### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:13:40Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides command-line utilities for rotating compressed directory backups and summarizing order revenue by month and region.

#### Purpose
- Create timestamped archives with configurable retention for a source directory.
- Aggregate CSV orders into monthly regional revenue reports for a selected currency.

#### Responsibilities
- **Backup rotation:** Validate source and retention settings, create a compressed archive, and remove older matching archives beyond the retention limit.
- **Sales reporting:** Parse and currency-filter order CSVs, aggregate revenue by month and region, and print totals meeting a minimum threshold.

#### Key units
- `rotate_backups.sh` — creates a UTC-dated `.tar.gz` archive and retains the newest configured number.
- `Order` — frozen dataclass representing an order’s date, region, and amount.
- `parse_orders(path, currency)` — validates CSV headers and yields matching-currency orders.
- `revenue_by_month_and_region(orders)` — returns sorted totals keyed by month and region.
- `main(argv=None)` — parses report options, combines input files, and prints qualifying totals.

#### Data flow
- Backup: source directory → temporary `.partial` tarball → final timestamped archive → newest-first retention listing and removal of excess archives.
- Sales: CSV files → header validation and currency filtering → parsed `Order` records → monthly regional sums → threshold-filtered tab-separated output.

#### Dependencies
- Backup rotation uses Bash features and external commands including `tar`, `date`, `mkdir`, `mv`, `ls`, and `rm`.
- Sales reporting uses Python standard-library CSV, argument parsing, date, decimal, and collection utilities.

#### Cross-cutting
- Both are command-line scripts with configurable inputs and explicit status or error behavior.
- Backup failures are governed by strict Bash settings and an `EXIT` cleanup trap; sales parsing reports invalid headers, dates, or amounts with file context.
