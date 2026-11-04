### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:23:26Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for retaining compressed directory backups and turning order exports into currency-specific monthly regional revenue reports.

#### Purpose
- Create timestamped archives with configurable retention for a source directory.
- Aggregate CSV order data into monthly revenue totals by region, with currency and minimum-revenue filters.

#### Responsibilities
- **Backup rotation:** `rotate_backups.sh` creates a compressed archive, reports its path and size, and removes older matching archives beyond the retention limit.
- **Sales reporting:** `sales_report.py` parses matching-currency orders, groups revenue by month and region, and prints qualifying totals.

#### Key units
- `rotate_backups.sh` — validates paths and retention count, creates an archive through a temporary partial file, then applies retention.
- `Order` — immutable order record containing date, region, and decimal amount.
- `parse_orders(path, currency)` — validates CSV headers and yields orders matching the currency.
- `revenue_by_month_and_region(orders)` — returns sorted revenue totals keyed by `(YYYY-MM, region)`.
- `main(argv=None)` — runs the sales-report CLI and returns its status.

#### Data flow
- Backup: source directory → timestamped `.tar.gz` partial archive → completed archive in the backup directory → newest-first retention cleanup.
- Reporting: one or more CSV files → required-column validation and currency filtering → monthly regional aggregation → minimum-revenue filtering and tab-separated output.

#### Dependencies
- `rotate_backups.sh` uses Bash and external utilities including `tar`, `date`, `mkdir`, `mv`, `du`, `ls`, and `rm`.
- `sales_report.py` uses Python standard-library CSV, argument-parsing, date, decimal, and aggregation facilities; input columns are `order_date`, `region`, `amount`, and `currency`.

#### Cross-cutting
- Backup script uses strict Bash mode and an `EXIT` trap to remove a partial archive on failure.
- Sales reporting uses `Decimal` for amounts; missing CSV columns and invalid dates or amounts produce errors that include file context.
