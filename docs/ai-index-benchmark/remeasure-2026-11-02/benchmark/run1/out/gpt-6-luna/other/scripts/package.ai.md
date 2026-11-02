### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:29:42Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for rotating compressed directory backups and reporting monthly sales revenue by region and currency.

#### Purpose
- Create timestamped archives with configurable retention for a source directory.
- Aggregate order CSVs into monthly regional revenue totals, with currency selection and minimum-revenue filtering.

#### Responsibilities
- **Backup rotation:** Validate source and retention settings, create a `.tar.gz` archive in the backup directory, and remove older matching archives beyond the retention limit.
- **Sales reporting:** Parse matching-currency orders from CSV files, aggregate amounts by month and region, and print filtered totals.

#### Key units
- `rotate_backups.sh` — command-line backup and archive-retention utility; defaults to keeping seven archives.
- `Order` — frozen dataclass containing an order date, region, and amount.
- `parse_orders(path, currency)` — validate CSV columns and yield parsed orders in the requested currency.
- `revenue_by_month_and_region(orders)` — aggregate order amounts into sorted monthly regional totals.
- `main(argv=None)` — parse report options, combine input files, filter totals, and print results.

#### Data flow
- Backup: source directory → timestamped partial archive → final `.tar.gz` → newest-first retention cleanup.
- Sales: CSV files → validated, matching-currency `Order` values → month/region totals → minimum-revenue filter → tab-separated output.

#### Dependencies
- Backup script uses Bash features and system commands including `tar`, `date`, `mkdir`, `mv`, `ls`, and `rm`.
- Sales report uses Python standard-library CSV, argument parsing, date, decimal, and aggregation utilities.

#### Cross-cutting
- Backup failures are fatal under `set -euo pipefail`; an `EXIT` trap removes a partial archive.
- Sales amounts use `Decimal`; missing columns and invalid dates or amounts produce file-specific `ValueError` messages.
