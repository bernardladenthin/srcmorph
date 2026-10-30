### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:37:27Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> This package provides operational utilities for rotating directory backups and generating monthly regional sales-revenue reports from order CSVs.

#### Purpose
- Create dated compressed backups and enforce archive retention.
- Aggregate order data into revenue reports filtered by currency and minimum revenue.

#### Responsibilities
- **Backup rotation:** Validate inputs, create a UTC-timestamped `.tar.gz` archive, and remove older archives beyond the retention limit.
- **Sales reporting:** Parse matching-currency CSV orders, aggregate revenue by month and region, filter totals, and print a tab-separated report.

#### Key units
- `rotate_backups.sh` — creates and prunes compressed directory backups.
- `Order` — frozen dataclass holding an order’s date, region, and amount.
- `parse_orders` — yields validated orders matching the selected currency.
- `revenue_by_month_and_region` — aggregates and sorts monthly regional totals.
- `main` — parses report options, processes input files, and returns a status code.

#### Data flow
- **Backups:** Source directory → temporary compressed archive → finalized archive → retention pruning.
- **Reports:** CSV files → currency-filtered `Order` values → monthly regional totals → minimum-revenue filter → tab-separated output.

#### Dependencies
- Backup script: Bash utilities including `tar`, `date`, `mkdir`, `mv`, `ls`, and `rm`.
- Sales report: Python standard library modules for CLI parsing, CSV input, date and decimal handling, and aggregation.
- No shared internal dependencies are described.

#### Cross-cutting
- Both utilities validate inputs and report failures through errors or nonzero exit codes.
- Backup retention defaults to 7 archives; report currency defaults to `EUR` and minimum revenue to `0`.
