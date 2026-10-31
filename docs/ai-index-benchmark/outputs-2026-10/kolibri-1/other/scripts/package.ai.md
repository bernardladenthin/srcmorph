### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:40:47Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Generates monthly sales reports from order CSV exports and rotates directory backups.

#### Purpose
- Produces revenue-per-month-and-region sales reports from CSV order exports
- Creates compressed, dated backups of a directory and retains only the newest N archives

#### Responsibilities
- Sales reporting: parses order CSVs, aggregates revenue by month and region, applies currency and revenue filters
- Backup rotation: validates inputs, builds timestamped tar.gz archives, prunes older archives beyond a keep limit

#### Key units
- Order dataclass: represents a single order with order_date, region, amount, currency
- parse_orders(path, currency): CSV reader yielding Order objects in the specified currency
- revenue_by_month_and_region(orders): aggregates revenue into (month, region) to Decimal mapping
- main(argv=None): CLI entry point parsing arguments and printing the report
- rotate_backups.sh(source_dir, backup_dir, keep): backup creation and rotation script

#### Data flow
- CSV files flow through parse_orders into Order objects, then into revenue_by_month_and_region aggregation, then filtered and printed to stdout
- Source directory flows through backup creation into a timestamped tar.gz archive, then pruned by newest-first listing

#### Dependencies
- Python standard library: argparse, csv, sys, defaultdict, dataclass, date, Decimal, InvalidOperation
- Shell utilities: tar, date, du, ls, mapfile

#### Cross-cutting
- argparse-based CLI argument parsing in the Python script
- set -euo pipefail error handling in the shell script
- Decimal-based monetary arithmetic in the Python script
- UTC timestamping in both scripts for archive naming and date parsing
