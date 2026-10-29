### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:00:45Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> This package provides operational scripts for automated system maintenance, specifically backup rotation, and business intelligence reporting on sales data.

#### Purpose
* Automate the creation and pruning of compressed backups for specified directories.
* Generate aggregated sales reports from raw CSV order data, grouped by time and geography.

#### Responsibilities
* Backup Management: Handling the creation and lifecycle management of compressed archives using `rotate_backups.sh`.
* Sales Data Processing: Reading, filtering, and aggregating transactional data from multiple CSV files using `sales_report.py`.

#### Key units
* `rotate_backups.sh`: Shell script responsible for creating dated, compressed backups and enforcing retention policies.
* `sales_report.py`: Python module containing logic to parse orders, aggregate revenue by month/region, and filter results.
* `Order` (implied in `sales_report.py`): Data structure representing a single processed order record.

#### Data flow
* `rotate_backups.sh`: Takes `SOURCE_DIR` and `BACKUP_DIR` as input, producing dated `.tar.gz` files in `BACKUP_DIR`.
* `sales_report.py`: Reads raw CSV files (input), uses `parse_orders` to yield validated `Order` objects, aggregates these into a dictionary via `revenue_by_month_and_region`, and outputs formatted results to stdout.

#### Dependencies
* `rotate_backups.sh`: Relies on standard Unix utilities (`tar`, `du`, `ls`, `date`).
* `sales_report.py`: Depends on Python standard libraries including `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime.date`, and `decimal.Decimal`.

#### Cross-cutting
* Error Handling: Both scripts feature explicit exit conditions or raised exceptions (e.g., missing arguments in the shell script, `ValueError` in the Python script) upon invalid input or data format issues.
* Configuration: `sales_report.py` uses command-line arguments (`--currency`, `--min-revenue`) to configure the reporting scope.
