### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:48:36Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> This package manages operational data hygiene and reporting by rotating backup archives and aggregating sales totals by month and region for review.

#### Purpose
- Maintains backup retention by creating timestamped compressed archives of source directories and deleting older snapshots beyond a configured limit.
- Produces monthly revenue summaries from order CSV exports, filtered by currency and minimum threshold.

#### Responsibilities
- Backup rotation and retention:
  - `rotate_backups.sh` validates inputs, creates timestamped `tar.gz` archives under a backup directory, and prunes older archives to keep only the newest `KEEP`.
- Sales reporting:
  - `sales_report.py` reads one or more CSV inputs, filters to the target currency, groups revenue by `(YYYY-MM, region)`, and prints totals for reporting.

#### Key units
- `rotate_backups.sh` — shell CLI that creates `${name}_YYYYMMDDTHHMMSSZ.tar.gz` backups and removes older archive files beyond a retention count.
- `KEEP` — positive integer retention limit enforced by validation and used to select newest archive snapshots.
- `Order` — frozen dataclass representing parsed CSV order rows with `order_date`, `region`, and `amount`.
- `parse_orders(path, currency)` — reads a CSV file, checks required headers, filters by currency, and yields `Order` records.
- `revenue_by_month_and_region(orders)` — aggregates order amounts by month and region using `defaultdict(Decimal)`.
- `main(argv=None)` — CLI entry point for loading CSVs, checking for data, and printing qualifying totals.

#### Data flow
- Backup flow:
  - input `source_dir`, `backup_dir`, optional `keep` -> validate arguments and directory existence -> create backup directory -> build UTC timestamped archive path -> write partial archive file -> rename to final `.tar.gz` -> list newest archives -> remove older archives beyond `KEEP`.
- Sales flow:
  - input CSV file paths plus `--currency` and `--min-revenue` -> validate headers -> parse rows into `Order` -> filter rows by target currency -> aggregate by `(YYYY-MM, region)` -> sort totals -> print `YYYY-MM\tregion\tamount currency` output; exit nonzero when no valid orders are found.

#### Dependencies
- Internal collaboration:
  - `rotate_backups.sh` is a standalone CLI script with no packaged imports or shared library usage.
  - `sales_report.py` is a standalone module with no package dependencies beyond its own dataclass and functions.
- External modules/tools:
  - Bash: `set`, `trap`, `mapfile`, `mkdir`, `basename`, `dirname`, `date`, `mv`, `rm`, `echo`, `exit`, `tar`, `ls`, `du`, `cut`.
  - Python stdlib: `argparse`, `csv`, `sys`, `defaultdict`, `dataclass`, `date`, `Decimal`, `InvalidOperation`.
- External data sources:
  - filesystem directories for backup source and archive destination
  - input CSV files containing `order_date`, `region`, `amount`, and `currency`

#### Cross-cutting
- Input validation is strict and explicit:
  - `rotate_backups.sh` validates `KEEP`, `SOURCE_DIR`, and positional arguments with direct exit codes and stderr messaging.
  - `sales_report.py` validates CSV headers and row data, raising `ValueError` with file and line details for malformed input.
- Failure handling is immediate:
  - shell script uses `set -euo pipefail` to abort on command failure.
  - Python CLI exits with status `1` when no orders are found and surfaces malformed input as `ValueError`.
- Reporting and retention both use filtering criteria:
  - backup retention is based on newest archive recency
  - sales reporting is based on target currency and minimum revenue threshold
- Output conventions are stable:
  - backup script emits `created ...` and `removed ...` messages
  - sales report prints tab-delimited totals to stdout and writes `no orders found` to stderr for empty results
