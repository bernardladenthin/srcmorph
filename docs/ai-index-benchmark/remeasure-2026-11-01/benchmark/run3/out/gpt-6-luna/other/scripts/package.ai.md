### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:02:58Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for retaining dated directory backups and producing currency-filtered monthly regional revenue reports from order exports.

#### Purpose
- Create compressed, timestamped backups while enforcing an archive-retention limit.
- Convert order CSV exports into monthly revenue totals by region, filtered by currency and minimum revenue.

#### Responsibilities
- **Backup rotation:** Validate source and retention inputs, create a gzip-compressed archive, clean up partial output on exit, and remove archives beyond the retention limit.
- **Sales reporting:** Parse and validate order CSV rows, aggregate matching-currency amounts by month and region, and print qualifying totals.

#### Key units
- `rotate_backups.sh` — creates a UTC-dated `.tar.gz` archive and retains the newest requested count.
- `Order` — frozen record for an order’s date, region, and decimal amount.
- `parse_orders(path, currency)` — validates CSV headers and yields orders matching the selected currency.
- `revenue_by_month_and_region(orders)` — returns sorted totals keyed by month and region.
- `main(argv=None)` — runs the sales-report CLI and returns its status.

#### Data flow
- **Backups:** Source directory → validation → temporary compressed archive → final timestamped archive → retention-based deletion of older matching archives.
- **Sales:** CSV files → required-column and currency checks → parsed `Order` records → monthly regional aggregation → minimum-revenue filter → tab-separated output.

#### Dependencies
- `rotate_backups.sh` uses Bash features and system commands including `tar`, `date`, `mkdir`, `mv`, `du`, `ls`, and `rm`.
- `sales_report.py` uses Python standard-library CSV, argument parsing, date, decimal, dataclass, and collection utilities; consumes CSV columns `order_date`, `region`, `amount`, and `currency`.

#### Cross-cutting
- Backup failures are governed by Bash strict mode; an `EXIT` trap removes the temporary archive.
- Sales amounts use `Decimal`; missing required columns and invalid dates or amounts raise `ValueError`, with row context for parse errors.
