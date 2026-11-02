### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:38:32Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for retaining directory backups and reporting monthly regional sales revenue by currency and region.

#### Purpose
- Create timestamped compressed backups with a configurable retention limit.
- Aggregate order CSV data into monthly, regional revenue reports for a selected currency.

#### Responsibilities
- **Backup rotation:** Archive a source directory as a UTC-timestamped `.tar.gz`, then remove older matching archives beyond the retention count.
- **Sales reporting:** Validate and parse order CSVs, group matching-currency revenue by month and region, apply a minimum-revenue threshold, and print qualifying totals.

#### Key units
- `rotate_backups.sh` — creates a compressed archive and enforces per-source backup retention.
- `Order` — immutable sales record containing order date, region, and decimal amount.
- `parse_orders(path, currency)` — validates CSV columns and yields orders in the requested currency.
- `revenue_by_month_and_region(orders)` — returns sorted monthly, regional revenue totals.
- `main(argv=None)` — parses report options, aggregates input files, and prints results.

#### Data flow
- Backup: source and destination arguments → archive written to a `.partial` path → rename to final timestamped archive → retain newest `KEEP` matching archives.
- Sales: CSV files → validated, currency-filtered `Order` records → month-and-region totals → minimum-revenue filter → tab-separated report.

#### Dependencies
- Backup script uses Bash features and system commands including `tar`, `date`, `mv`, `ls`, and `rm`.
- Sales report uses Python standard-library modules for CSV and argument parsing, dates, decimal arithmetic, and aggregation.

#### Cross-cutting
- Backup failures terminate under `set -euo pipefail`; an exit trap removes any partial archive.
- Sales amounts use `Decimal`; malformed headers, dates, and amounts raise `ValueError` with file context.
