### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:31:43Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides lightweight operational scripts for rotating compressed backups and reporting monthly regional sales revenue from CSV order data.

#### Purpose
- Automates timestamped directory backups with configurable archive retention.
- Converts matching-currency order CSVs into filtered monthly revenue summaries by region.

#### Responsibilities
- **Backup rotation:** `rotate_backups.sh` validates inputs, creates a compressed archive, and removes older matching archives beyond the retention limit.
- **Sales reporting:** `sales_report.py` parses order exports, aggregates revenue by month and region, and prints totals meeting a minimum threshold.

#### Key units
- `rotate_backups.sh` — creates a UTC-dated `.tar.gz` backup and retains the newest configured count.
- `Order` — frozen record containing an order date, region, and amount.
- `parse_orders(path, currency)` — yields validated orders matching the selected currency from a CSV.
- `revenue_by_month_and_region(orders)` — returns sorted revenue totals keyed by month and region.
- `main(argv=None)` — parses report options and prints the sales summary.

#### Data flow
- Backup: source directory → temporary archive → completed timestamped archive in the backup directory → retention cleanup.
- Reporting: CSV files → required-column and currency checks → `Order` records → monthly regional aggregation → minimum-revenue filtering and tab-separated output.

#### Dependencies
- Backup rotation uses Bash and external commands including `tar`, `date`, `ls`, and `rm`.
- Sales reporting uses Python standard-library CSV, date, decimal, argument-parsing, and aggregation facilities.

#### Cross-cutting
- Both scripts are command-line utilities with configurable inputs and defaults.
- Backup rotation uses strict Bash mode and an exit trap to clean up partial archives.
- Sales amounts use `Decimal`; CSV validation and invalid matching-row data produce path- and line-aware errors.
