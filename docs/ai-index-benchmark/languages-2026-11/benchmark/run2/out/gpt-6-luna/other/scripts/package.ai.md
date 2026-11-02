### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:39:30Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides command-line utilities for rotating compressed directory backups and summarizing order revenue by month and region.

#### Purpose
- Create dated archives of a source directory while enforcing a retention limit.
- Aggregate CSV orders into monthly, regional revenue totals for a selected currency.

#### Responsibilities
- **Backup rotation (`rotate_backups.sh`):** Validate inputs, create a gzip-compressed archive, report its path and size, and delete older matching archives beyond the retention count.
- **Sales reporting (`sales_report.py`):** Parse and validate order CSVs, aggregate revenue by month and region, and print totals filtered by currency and minimum revenue.

#### Key units
- `rotate_backups.sh` — Bash command-line script accepting a source directory, backup directory, and optional retention count (`KEEP`, default `7`).
- `Order` — frozen dataclass holding an order date, region, and decimal amount.
- `parse_orders(path, currency)` — validates CSV headers and yields parsed orders matching the requested currency.
- `revenue_by_month_and_region(orders)` — returns sorted revenue totals grouped by `YYYY-MM` and region.
- `main(argv=None)` — parses report CLI arguments, combines input files, applies the minimum-revenue filter, and prints results.

#### Data flow
- **Backups:** Source directory → temporary timestamped `.tar.gz` archive → final archive via rename → newest-first archive listing → remove entries beyond `KEEP`.
- **Sales report:** CSV files → header and currency filtering → parsed `Order` values → monthly/region totals → minimum-revenue filtering → tab-separated output.

#### Dependencies
- `rotate_backups.sh` uses Bash features and external commands including `tar`, `date`, `mkdir`, `mv`, `du`, `ls`, and `rm`.
- `sales_report.py` uses Python standard-library modules for CSV and CLI parsing, dates, decimal amounts, dataclasses, and aggregation.

#### Cross-cutting
- Both utilities are command-line oriented and report results directly to standard output.
- The backup script uses strict Bash mode and an `EXIT` trap to remove its partial archive; the report returns status `1` and writes `no orders found` to standard error when no matching orders exist.
