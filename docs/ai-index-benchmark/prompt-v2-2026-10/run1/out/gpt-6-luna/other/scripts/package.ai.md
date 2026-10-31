### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:34:18Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for retaining timestamped directory backups and producing currency-filtered monthly sales revenue reports.

#### Purpose
- Create compressed, retention-limited backups of a source directory.
- Aggregate order CSV data into monthly revenue totals by region.

#### Responsibilities
- **Backup rotation:** Validate source and retention settings, create a UTC-dated `.tar.gz` archive, and remove older archives beyond the configured limit.
- **Sales reporting:** Parse and validate order CSVs, filter by currency, aggregate revenue by month and region, and apply a minimum-revenue threshold.

#### Key units
- `rotate_backups.sh` — archives a source directory and retains the newest configured number of backups.
- `Order` — frozen record of an order’s date, region, and amount.
- `parse_orders(path, currency)` — yields validated orders matching the selected currency.
- `revenue_by_month_and_region(orders)` — returns sorted monthly revenue totals grouped by region.
- `main(argv=None)` — runs the sales report CLI and returns its exit status.

#### Data flow
- **Backups:** Source directory → temporary `.partial` archive → final timestamped archive → retention cleanup; reports the archive path, size, and removed paths.
- **Sales report:** CSV files → required-column validation and currency filtering → month/region aggregation → minimum-revenue filtering → tab-separated output.

#### Dependencies
- Backup rotation uses Bash utilities and system commands including `tar`, `date`, `mkdir`, `mv`, `ls`, and `rm`.
- Sales reporting uses Python standard-library modules including `csv`, `argparse`, `datetime`, and `decimal`.

#### Cross-cutting
- Backup failures terminate under `set -euo pipefail`; an exit trap removes an incomplete archive.
- Sales input errors include path and line number for invalid dates or amounts; missing required columns raise `ValueError`.
