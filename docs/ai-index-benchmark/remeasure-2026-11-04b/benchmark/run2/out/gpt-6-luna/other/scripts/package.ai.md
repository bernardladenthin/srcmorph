### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:00:00Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides command-line utilities for retaining compressed directory backups and reporting order revenue by month and region.

#### Purpose
- Create timestamped, compressed backups while pruning archives beyond a retention limit.
- Aggregate order CSV data into currency-filtered monthly regional revenue totals.

#### Responsibilities
- **Backup rotation (`rotate_backups.sh`):** Validate source and retention count, archive a directory, and remove older matching archives.
- **Sales reporting (`sales_report.py`):** Parse order CSVs, aggregate matching-currency sales, and print totals meeting a revenue threshold.

#### Key units
- `rotate_backups.sh` — Bash command accepting source directory, backup directory, and optional archive count.
- `Order` — frozen record containing an order date, region, and decimal amount.
- `parse_orders(path, currency)` — validate CSV headers and yield matching-currency orders.
- `revenue_by_month_and_region(orders)` — aggregate amounts by month and region.
- `main(argv=None)` — parse report options and run the sales-report workflow.

#### Data flow
- **Backups:** Source directory → timestamped `.tar.gz` archive → newest-first archive listing → removal of entries beyond `KEEP`.
- **Sales:** CSV files → validated, currency-filtered `Order` records → monthly regional totals → threshold-filtered, tab-separated output.

#### Dependencies
- **Backups:** Bash and system utilities including `tar`, `date`, `mkdir`, `mv`, `du`, `ls`, and `rm`.
- **Sales reporting:** Python standard library modules for CLI parsing, CSV reading, dates, decimal arithmetic, and aggregation.

#### Cross-cutting
- Both scripts are command-line tools with positional inputs; sales reporting also accepts `--currency` and `--min-revenue`.
- Backup execution uses strict Bash error settings and an exit trap to remove partial archives; CSV parsing raises `ValueError` for missing columns or invalid date/amount values.
