### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:54:55Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A backup rotation utility and a sales aggregation script that together handle data preservation and financial reporting from CSV exports.

#### Purpose
- `rotate_backups.sh`: Automates creation and pruning of dated directory backups.
- `sales_report.py`: Aggregates monthly revenue from CSV order exports with currency and threshold filtering.

#### Responsibilities
- **Backup management**: Timestamped archiving, retention policy enforcement, and storage pruning.
- **Sales reporting**: CSV ingestion, currency conversion filtering, revenue aggregation by month and region, and threshold-based output.

#### Key units
- `rotate_backups.sh`: Main script handling archive creation (`tar`), timestamp generation (`date`), and retention iteration (`mapfile`/`rm`).
- `sales_report.py`: `parse_orders` generator yielding `Order` objects from CSV files; `revenue_by_month_and_region` aggregating `Decimal` totals keyed by `(month, region)`; `main` entry point driving CLI argument parsing and output.

#### Data flow
- `rotate_backups.sh`: Accepts `source_dir` and `backup_dir`; validates inputs; creates a timestamped `tar.gz` archive; moves it to `backup_dir`; lists and sorts archives by timestamp; iterates and removes excess archives beyond the `keep` count.
- `sales_report.py`: Reads CSV files; filters rows by `--currency`; validates required columns (`order_date`, `region`, `amount`, `currency`); parses `order_date` into `date.fromisoformat` and `amount` into `Decimal`; accumulates revenue totals in a `defaultdict` keyed by `(YYYY-MM, region)`; sorts results lexicographically; prints groups meeting `--min-revenue` threshold.

#### Dependencies
- `rotate_backups.sh`: `bash`, `tar`, `mv`, `ls`, `rm`, `du`, `date`, `mapfile`.
- `sales_report.py`: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal` / `InvalidOperation`.

#### Cross-cuting
- Both scripts validate inputs and exit with status code 2 on invalid `keep`/retention arguments or missing source data.
- `sales_report.py` uses `ValueError` for missing columns or unparseable date/amount values.
- `rotate_backups.sh` enforces `keep` as a positive integer, exiting with code 2 otherwise.
