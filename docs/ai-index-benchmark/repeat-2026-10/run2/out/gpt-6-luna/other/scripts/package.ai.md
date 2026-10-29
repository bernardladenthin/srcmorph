### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:51:55Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides command-line utilities for rotating compressed directory backups and reporting monthly order revenue by region.

#### Purpose
- Create timestamped archives with configurable retention.
- Aggregate order CSV data into currency-filtered monthly regional revenue totals.

#### Responsibilities
- Backup rotation: archive a source directory, publish the completed archive, and remove older matching archives beyond the retention limit.
- Sales reporting: parse and validate order exports, aggregate revenue by month and region, and print totals that meet the minimum threshold.

#### Key units
- `rotate_backups.sh` — creates a UTC-dated `.tar.gz` archive and retains the newest configured number.
- `Order` — dataclass holding a parsed order date, region, and `Decimal` amount.
- `parse_orders(path, currency)` — validates CSV columns and yields orders matching the selected currency.
- `revenue_by_month_and_region(orders)` — returns sorted monthly revenue totals grouped by region.
- `main(argv=None)` — runs the sales-report CLI and returns its exit code.

#### Data flow
- Backup: source directory → temporary archive → final timestamped archive → retention filtering and deletion.
- Sales: one or more CSV files → validated, currency-filtered `Order` records → monthly and regional aggregation → minimum-revenue filter → tab-separated output.

#### Dependencies
- Backup rotation uses Bash features and external commands including `tar`, `date`, `mv`, and `rm`.
- Sales reporting uses Python standard-library modules for CSV and CLI handling, dates, decimals, and aggregation.

#### Cross-cutting
- Both utilities expose command-line inputs and report failures through nonzero exit codes.
- Backup creation uses strict shell error handling and an exit trap to clean up temporary archives.
- Sales parsing reports invalid columns or row values with file and line context; file-opening and CSV-reading errors are not handled locally.
