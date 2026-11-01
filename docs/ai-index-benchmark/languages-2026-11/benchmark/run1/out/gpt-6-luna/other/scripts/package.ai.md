### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:17:02Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for rotating compressed directory backups and producing currency-filtered monthly regional revenue reports from order CSV files.

#### Purpose
- Create timestamped backups with bounded retention.
- Aggregate order data into threshold-filtered monthly revenue totals by region.

#### Responsibilities
- **Backup rotation:** `rotate_backups.sh` validates paths and retention settings, creates a gzip-compressed archive, and removes older matching archives beyond the configured limit.
- **Sales reporting:** `sales_report.py` parses order CSV exports, filters by currency, aggregates revenue by month and region, and prints qualifying totals.

#### Key units
- `rotate_backups.sh` — command-line backup and retention utility; defaults to keeping seven archives.
- `Order` — frozen dataclass holding an order date, region, and decimal amount.
- `parse_orders(path, currency)` — validates CSV headers and yields orders matching the selected currency.
- `revenue_by_month_and_region(orders)` — returns revenue totals grouped and sorted by month and region.
- `main(argv=None)` — parses report options and prints results or the no-orders message.

#### Data flow
- **Backups:** source directory → UTC-timestamped `.tar.gz` archive via a `.partial` file → archive path and size printed → older matching archives removed beyond `KEEP`.
- **Reports:** one or more CSV files → validated, currency-filtered `Order` records → monthly regional totals → rows meeting `--min-revenue` printed as tab-separated output.

#### Dependencies
- `rotate_backups.sh` uses Bash features and external commands including `tar`, `date`, `ls`, `mv`, and `rm`.
- `sales_report.py` uses Python standard-library modules for CSV and CLI handling, dates, `Decimal` arithmetic, and aggregation.
- The scripts are independent utilities with no stated internal collaboration.

#### Cross-cutting
- Both are standalone command-line tools with configurable inputs and defaults.
- Backup archive creation uses an `EXIT` cleanup trap and strict Bash error handling; the report returns status `1` when no matching orders are found and raises `ValueError` for missing CSV columns or invalid date and amount values.
