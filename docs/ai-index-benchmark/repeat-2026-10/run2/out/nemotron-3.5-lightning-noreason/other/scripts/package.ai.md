### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:51Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> This package provides tools for data management and financial reporting: rotating directory backups and generating monthly sales revenue reports from CSV order exports.

#### Purpose
- Manages backup retention by creating timestamped archives and pruning old files.
- Processes CSV order data to produce aggregated monthly revenue reports grouped by region.

#### Responsibilities
- **Backup rotation**: Creates compressed, dated backups of source directories and retains only the newest N archives.
- **Sales reporting**: Reads CSV order exports, validates structure, aggregates revenue by month and region, and outputs filtered results.

#### Key units
- `rotate_backups.sh` – Shell script that creates a timestamped `.tar.gz` archive of a source directory and deletes older backups to maintain a configurable retention count.
- `sales_report.py` – Python script that reads one or more CSV files containing order data, validates required columns (`order_date`, `region`, `amount`, `currency`), aggregates revenue into a `defaultdict` keyed by `(month, region)`, and prints a sorted report to stdout.

#### Data flow
- `rotate_backups.sh` takes `SOURCE_DIR`, `BACKUP_DIR`, and optional `KEEP`; it validates inputs, creates a compressed archive, lists existing archives sorted by modification time, and removes those exceeding the retention limit.
- `sales_report.py` takes `csv_files`, `currency`, and optional `min_revenue`; it parses each CSV, skips rows with currency mismatches, validates required columns and data formats, accumulates `Decimal` totals in a `defaultdict`, sorts results by `(month, region)`, and prints rows meeting the revenue threshold.

#### Dependencies
- `rotate_backups.sh`: bash, `tar`, `date`, `du`, `ls`, `mv`, `rm`.
- `sales_report.py`: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`.

#### Cross-cutting
- Both scripts enforce input validation: `rotate_backups.sh` checks that `KEEP` is a positive integer and `SOURCE_DIR` exists; `sales_report.py` validates required CSV columns and raises `ValueError` on invalid date or amount formats.
- Both output status/messages to stdout (and stderr for `sales_report.py` errors) and use exit codes to signal success or failure.
