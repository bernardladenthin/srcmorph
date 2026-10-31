### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:47:40Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for rotating directory backups and producing monthly regional revenue reports from order CSV exports.

#### Purpose
- Create timestamped compressed backups with configurable retention.
- Aggregate CSV order amounts into currency-filtered monthly revenue totals by region.

#### Responsibilities
- **Backup rotation:** `rotate_backups.sh` validates its inputs, archives a source directory, and removes older matching archives beyond the retention limit.
- **Sales reporting:** `sales_report.py` parses and validates order CSVs, groups matching-currency amounts by month and region, and prints totals meeting a minimum revenue threshold.

#### Key units
- `rotate_backups.sh` — command-line utility that creates a UTC-dated `.tar.gz` archive and retains the newest configured number.
- `Order` — frozen record of an order’s date, region, and amount.
- `parse_orders(path, currency)` — yields validated orders matching the requested currency.
- `revenue_by_month_and_region(orders)` — returns sorted monthly and regional revenue totals.
- `main(argv=None)` — parses report options and prints qualifying rows.

#### Data flow
- Backup: source directory → `.partial` archive → final timestamped archive → retention cleanup.
- Report: CSV files → validated, currency-matched `Order` records → monthly regional totals → minimum-revenue filtering and formatted output.

#### Dependencies
- Backup utility uses Bash features and system commands including `tar`, `date`, `du`, `ls`, and `rm`.
- Report utility uses Python standard-library CSV, argument parsing, date, and decimal support.

#### Cross-cutting
- Backup errors use strict shell mode; an exit trap removes an unfinished `.partial` archive.
- Report amounts use `Decimal`; invalid required headers, dates, or amounts raise `ValueError`.
