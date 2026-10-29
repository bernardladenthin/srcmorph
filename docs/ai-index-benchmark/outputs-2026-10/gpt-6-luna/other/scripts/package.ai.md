### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T21:22:59Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for rotating dated backups and summarizing order revenue by month and region.

#### Purpose
- Automates compressed backup creation with configurable retention.
- Turns order CSV exports into currency-filtered monthly regional revenue reports.

#### Responsibilities
- **Backup rotation:** validate arguments, archive a source directory, and prune older matching archives.
- **Sales reporting:** parse matching-currency orders, aggregate revenue by month and region, and print totals above a threshold.

#### Key units
- `rotate_backups.sh` — creates a UTC-dated `.tar.gz` archive and retains the newest configured number.
- `Order` — stores a parsed order date, region, and decimal amount.
- `parse_orders(path, currency)` — validates CSV headers and yields orders in the selected currency.
- `revenue_by_month_and_region(orders)` — returns sorted monthly revenue totals grouped by region.
- `main(argv=None)` — runs the sales report CLI and returns its exit status.

#### Data flow
- Backup: source directory → temporary compressed archive → final dated archive → retention pruning.
- Sales report: CSV files → validated, currency-filtered `Order` values → month-and-region totals → minimum-revenue filter → tab-separated output.

#### Dependencies
- Backup rotation uses Bash utilities and external commands including `tar`, `mkdir`, `mv`, `du`, `ls`, and `rm`.
- Sales reporting uses Python standard-library modules for CSV, CLI parsing, dates, decimal amounts, and aggregation.

#### Cross-cutting
- Both utilities expose command-line entry points and configurable inputs.
- Backup failures trigger cleanup of the partial archive; sales parsing errors include file and CSV line context for invalid matching-row data.
