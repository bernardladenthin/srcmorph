### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:00:54Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for rotating compressed directory backups and producing currency-filtered monthly sales revenue reports by region.

#### Purpose
- Create timestamped `.tar.gz` backups while enforcing a configurable retention limit.
- Aggregate order CSVs into monthly regional revenue totals for a selected currency.

#### Responsibilities
- **Backup rotation (`rotate_backups.sh`):** Validate inputs, archive a source directory, and remove older matching archives beyond `KEEP`.
- **Sales reporting (`sales_report.py`):** Parse and validate order CSVs, filter by currency, aggregate revenue by month and region, and optionally apply a minimum-revenue threshold.

#### Key units
- `rotate_backups.sh` — creates a UTC-timestamped archive and prunes older archives for the same source name.
- `Order` — frozen dataclass holding an order’s date, region, and amount.
- `parse_orders(path, currency)` — validates CSV columns and yields orders matching the requested currency.
- `revenue_by_month_and_region(orders)` — returns sorted revenue totals keyed by month and region.
- `main(argv=None)` — parses sales-report CLI options and prints qualifying totals.

#### Data flow
- **Backups:** Source directory and backup destination → `.partial` tar archive → final timestamped archive on success → retention pruning.
- **Sales report:** CSV files → column validation and currency filtering → parsed `Order` values → month/region aggregation → optional minimum-revenue filter → tab-separated output.

#### Dependencies
- `rotate_backups.sh` uses Bash features and system commands including `tar`, `date`, `mv`, and `rm`.
- `sales_report.py` uses Python standard-library CSV, date, decimal, aggregation, and CLI modules.
- The scripts are independent; no shared internal package units are described.

#### Cross-cutting
- Both utilities accept command-line inputs and produce human-readable output.
- Backup creation uses strict shell error handling and removes a partial archive on exit; the sales report raises `ValueError` for missing columns or invalid dates and amounts.
