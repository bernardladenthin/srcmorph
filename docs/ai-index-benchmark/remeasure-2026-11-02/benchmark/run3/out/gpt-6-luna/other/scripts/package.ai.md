### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:00:09Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for maintaining dated backups and producing currency-filtered monthly regional sales summaries from CSV exports.

#### Purpose
- Create and rotate compressed directory backups.
- Aggregate order CSV data into monthly revenue reports by region.

#### Responsibilities
- **Backup rotation:** Validate source and retention settings, create a timestamped `.tar.gz` archive, and remove older matching archives beyond the retention limit.
- **Sales reporting:** Parse and validate order CSV files, include only the selected currency, aggregate revenue by month and region, and optionally filter totals by minimum revenue.

#### Key units
- `rotate_backups.sh` — command-line backup and archive-pruning script; defaults retention to seven archives.
- `Order` — frozen dataclass representing a parsed order’s date, region, and amount.
- `parse_orders(path, currency)` — yields validated orders matching the requested currency.
- `revenue_by_month_and_region(orders)` — returns sorted monthly regional revenue totals.
- `main(argv=None)` — parses report options, filters totals, and prints the sales report.

#### Data flow
- Backup: source directory and backup settings → temporary archive → renamed timestamped archive → prune older matching archives.
- Sales: CSV files → validated, currency-matched `Order` records → totals grouped by `YYYY-MM` and region → minimum-revenue filter → tab-separated output.

#### Dependencies
- Backup script uses Bash features and system commands including `tar`, `date`, `mv`, `du`, `ls`, and `rm`.
- Sales report uses Python standard-library CSV, argument parsing, dates, decimals, dataclasses, and aggregation utilities.

#### Cross-cutting
- Both utilities accept command-line inputs and report results through standard output.
- Backup failures exit under strict Bash settings; the script removes its partial archive via an `EXIT` trap.
- Sales input errors include file path and line number; missing matching orders are reported to standard error with status `1`.
