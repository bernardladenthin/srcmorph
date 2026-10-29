### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:07:52Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> This package provides utility scripts for automated system maintenance, specifically backup rotation, and business intelligence reporting on sales data.

#### Purpose
* To automate the creation and pruning of dated, compressed backups for specified directories.
* To process raw CSV order data to generate aggregated revenue reports segmented by month and region.

#### Responsibilities
* Backup Management: Creating timestamped, compressed archives and enforcing retention policies via `rotate_backups.sh`.
* Sales Data Processing: Ingesting, filtering, and aggregating transactional data from multiple CSV files using `sales_report.py`.

#### Key units
* rotate_backups.sh: Script responsible for creating and rotating compressed backups.
* sales_report.py: Python script that calculates total revenue aggregated by month and region from order CSVs.
* Order (dataclass): Represents a single processed order record within `sales_report.py`.

#### Data flow
* `rotate_backups.sh` takes source and destination paths as input, processes the directory contents using `tar`, and outputs a new archive file.
* `sales_report.py` takes a list of CSV file paths, uses `parse_orders` to yield validated `Order` objects, aggregates these objects via `revenue_by_month_and_region`, and outputs formatted revenue totals to stdout.

#### Dependencies
* **rotate_backups.sh**: Relies on standard shell utilities (`tar`, `ls`, `date`).
* **sales_report.py**: Depends on `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime.date`, and `decimal.Decimal`.

#### Cross-cutting
* **Error Handling**: Both scripts feature explicit error handling; `rotate_backups.sh` exits on invalid arguments, while `sales_report.py` raises `ValueError` during data parsing if CSV structure or data types are incorrect.
* **Configuration**: `sales_report.py` uses command-line arguments (`--currency`, `--min-revenue`) to configure the reporting scope.
