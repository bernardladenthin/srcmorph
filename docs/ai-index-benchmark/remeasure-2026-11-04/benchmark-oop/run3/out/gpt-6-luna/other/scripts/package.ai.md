### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:53:42Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides command-line utilities for rotating compressed directory backups and reporting monthly sales revenue by region and currency.

#### Purpose
- Create timestamped backups while enforcing per-source archive retention.
- Convert order CSV exports into filtered monthly regional revenue summaries.

#### Responsibilities
- **Backup rotation:** validate inputs, archive a source directory, and remove older matching archives beyond the retention limit.
- **Sales reporting:** read and validate order CSVs, select a currency, aggregate revenue by month and region, and print qualifying totals.

#### Key units
- `rotate_backups.sh` — creates a UTC-timestamped `.tar.gz` archive and retains the newest configured number.
- `Order` — immutable sales order data containing date, region, and amount.
- `parse_orders(path, currency)` — validates CSV columns and yields orders matching the selected currency.
- `revenue_by_month_and_region(orders)` — returns sorted monthly revenue totals by region.
- `main(argv=None)` — parses report options, filters totals, and prints the sales report.

#### Data flow
- **Backups:** source directory and backup settings → compressed partial archive → finalized timestamped archive → newest-first retention pruning.
- **Sales:** one or more CSV files → currency-filtered `Order` records → month/region aggregation → minimum-revenue filtering → tab-separated output.

#### Dependencies
- **Backup rotation:** Bash utilities including `tar`, `date`, `mv`, `du`, `ls`, and `rm`; archive names are matched by source basename.
- **Sales reporting:** Python standard library modules for CLI parsing, CSV reading, date and decimal handling, and aggregation.
