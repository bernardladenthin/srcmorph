### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:38:57Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides command-line utilities for retaining dated compressed backups and producing monthly regional sales-revenue reports from CSV exports.

#### Purpose
- Create and prune timestamped `.tar.gz` backups of a source directory.
- Aggregate CSV order data into monthly revenue totals by region and currency.

#### Responsibilities
- **Backup rotation:** `rotate_backups.sh` validates its inputs, creates a compressed archive via a temporary path, then retains only the newest configured number.
- **Sales reporting:** `sales_report.py` parses orders, filters by currency, aggregates by month and region, and applies a minimum-revenue threshold to report rows.

#### Key units
- `rotate_backups.sh` — standalone Bash script for dated backups and archive retention; defaults to keeping seven.
- `Order` — frozen record for an order’s date, region, and decimal amount.
- `parse_orders(path, currency)` — validates CSV columns and yields orders matching the selected currency.
- `revenue_by_month_and_region(orders)` — returns monthly regional totals sorted by key.
- `main(argv=None)` — parses sales-report CLI arguments, gathers orders, and prints qualifying totals.

#### Data flow
- **Backups:** source directory → temporary compressed archive → final timestamped archive → prune older matching archives in the destination.
- **Sales report:** CSV files → required-column validation and currency filtering → monthly/region aggregation → minimum-revenue filtering → tab-separated output.

#### Dependencies
- Backup script uses Bash and external commands including `tar`, `date`, `mkdir`, `mv`, `du`, `ls`, and `rm`.
- Sales report uses Python standard-library modules for CSV and argument parsing, dates, decimals, data records, and aggregation.

#### Cross-cutting
- The utilities are independent command-line programs with no described shared data flow or internal collaboration.
- Backup failures use strict Bash execution settings; invalid retention counts and missing source directories exit with status `2`.
- Sales parsing reports missing columns or invalid dates and amounts as `ValueError`; no matching orders produces stderr output and status `1`.
