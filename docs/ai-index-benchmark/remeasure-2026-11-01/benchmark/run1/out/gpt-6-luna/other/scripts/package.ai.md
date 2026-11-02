### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:23:40Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for rotating compressed directory backups and producing currency-filtered monthly revenue reports from order exports.

#### Purpose
- Create timestamped backups with bounded retention.
- Aggregate qualifying order revenue by month and region from CSV files.

#### Responsibilities
- **Backup rotation:** Validate source and retention settings, archive a directory, and remove older matching archives.
- **Sales reporting:** Parse and validate order CSVs, filter by currency, aggregate revenue, apply a minimum threshold, and print results.

#### Key units
- `rotate_backups.sh` — creates a UTC-dated `.tar.gz` archive and retains the newest configured number.
- `Order` — frozen data record for an order date, trimmed region, and decimal amount.
- `parse_orders(path, currency)` — validates CSV headers and yields orders matching the currency.
- `revenue_by_month_and_region(orders)` — returns sorted revenue totals keyed by month and region.
- `main(argv=None)` — parses report options, combines CSV inputs, filters totals, and returns a process status.

#### Data flow
- Backup: source directory → temporary compressed archive → final timestamped archive → newest-first retention cleanup.
- Reporting: CSV files → header validation and currency filtering → `Order` records → monthly regional totals → minimum-revenue filter → tab-separated output.

#### Dependencies
- Backup rotation uses Bash and external commands including `tar`, `date`, `mv`, `du`, `ls`, and `rm`.
- Sales reporting uses Python standard-library CSV, argument parsing, dates, decimals, and aggregation utilities.

#### Cross-cutting
- Backup rotation uses strict Bash mode and an `EXIT` trap to remove partial archives.
- Reporting uses `Decimal` for revenue and reports invalid headers, dates, or amounts with file context.
