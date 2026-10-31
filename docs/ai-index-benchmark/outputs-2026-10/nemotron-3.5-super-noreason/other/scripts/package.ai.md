### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:04:56Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Generates monthly regional revenue reports from order CSV exports and creates dated, retention-limited backups of directories.

#### Purpose
- Aggregate order data by month and region with currency and revenue filtering.
- Compress directories into timestamped archives and enforce a per-directory retention count.

#### Responsibilities
- sales_report.py: parse order CSVs, validate columns and row values, aggregate Decimal revenue by month and region, filter by minimum revenue, and print tab-separated results.
- rotate_backups.sh: validate input arguments, create a backup directory if needed, tar a source directory into a timestamped archive via a partial temp file, and delete archives exceeding the keep limit.

#### Key units
- `sales_report.py`: `Order` dataclass holding parsed date, region, amount, and currency.
- `sales_report.py`: `parse_orders(path, currency)` yields matching orders from a CSV file.
- `sales_report.py`: `revenue_by_month_and_region(orders)` returns sorted monthly/regional Decimal totals.
- `sales_report.py`: `main(argv=None)` CLI entry point returning exit code 0 or 1.
- `rotate_backups.sh`: `rotate_backups.sh <source_dir> <backup_dir> [keep=7]` shell utility returning exit code 0 or 2.

#### Data flow
- sales_report.py: CSV files are read by `csv.DictReader`, rows are validated and converted to `Order` objects, totals are accumulated in a `defaultdict` keyed by `("%Y-%m", region)`, results are sorted and filtered by `--min-revenue`, then printed as `month\tregion\trevenue currency`.
- rotate_backups.sh: a source directory is compressed by `tar` into a `.partial` temp file, renamed to a UTC-timestamped archive, then archives matching the source name are listed newest first and those beyond the KEEP count are removed.

#### Dependencies
- sales_report.py: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- rotate_backups.sh: Bash builtins `set`, `mapfile`, `trap`, `basename`, `dirname`, `date`; external commands `tar`, `mv`, `rm`, `ls`, `du`, `cut`, `mkdir`.

#### Cross-cutting
- Both units validate inputs and exit non-zero on failure: sales_report.py returns 1 with `no orders found` on stderr when no matching orders exist; rotate_backups.sh returns 2 with a usage message for missing directories or invalid KEEP.
- sales_report.py raises `ValueError` for missing CSV columns or invalid date/amount rows; rotate_backups.sh installs an EXIT trap to remove the `.partial` temp file on any failure.
