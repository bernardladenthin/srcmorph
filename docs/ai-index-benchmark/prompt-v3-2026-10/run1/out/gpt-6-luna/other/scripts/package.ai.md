### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:51:35Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for rotating compressed directory backups and producing currency-filtered monthly regional sales revenue reports.

#### Purpose
- Create timestamped, retention-limited archives of a source directory.
- Aggregate order CSV exports into monthly revenue totals by region.

#### Responsibilities
- **Backup rotation:** Validate source and retention settings, create a compressed archive in the backup directory, and remove older matching archives beyond the retention limit.
- **Sales reporting:** Parse orders for a selected currency, aggregate revenue by month and region, apply a minimum-revenue threshold, and print qualifying totals.

#### Key units
- `rotate_backups.sh` — command-line backup and archive-retention utility; defaults to keeping seven archives.
- `Order` — frozen record containing an order date, region, and decimal amount.
- `parse_orders(path, currency)` — validates CSV columns and yields orders matching the requested currency.
- `revenue_by_month_and_region(orders)` — returns sorted totals keyed by month and region.
- `main(argv=None)` — runs the sales-report CLI and returns its exit status.

#### Data flow
- Backup: source directory and backup directory arguments → timestamped temporary `.partial` tarball → final `.tar.gz` archive → newest-first retention cleanup.
- Sales report: one or more CSV files → required-column validation and currency filtering → `Order` records → month/region revenue aggregation → minimum-revenue filter → tab-separated output.

#### Dependencies
- Backup rotation uses Bash features and system commands including `tar`, `date`, `mkdir`, `mv`, `du`, and `rm`.
- Sales reporting uses Python standard-library modules for CSV and CLI parsing, dates, decimal arithmetic, and aggregation.

#### Cross-cutting
- Backup archive timestamps use UTC; failed execution triggers cleanup of the temporary archive.
- Sales amounts use `Decimal`; invalid dates or amounts raise file- and line-specific `ValueError`s, while no matching orders produces a stderr message and exit status `1`.
