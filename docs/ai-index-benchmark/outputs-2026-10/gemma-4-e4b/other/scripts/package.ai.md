### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:00:26Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> This package provides utility scripts for automated system maintenance, specifically backup rotation, and data processing for generating financial sales reports from CSV sources.

#### Purpose
* To automate the creation and pruning of compressed system backups.
* To process raw order data from CSV files into structured, aggregated revenue reports.

#### Responsibilities
* Backup Management: Creating dated, compressed archives and enforcing retention policies via `rotate_backups.sh`.
* Data Ingestion and Transformation: Reading, validating, and parsing order records from CSV files using `sales_report.py`.
* Reporting: Aggregating parsed order data by month and region to produce a final revenue summary.

#### Key units
* `rotate_backups.sh`: Shell script responsible for executing backup creation and deletion logic.
* `sales_report.py`: Python module handling CSV parsing, data aggregation, and report generation.
* `Order` (implied by `sales_report.py`): Data structure representing a single parsed order record.

#### Data flow
* `rotate_backups.sh` takes directory paths and a retention count as input, producing compressed archive files in the destination directory.
* `sales_report.py` takes a list of CSV file paths and filtering criteria (currency, min-revenue) as input, processing them through `parse_orders` to yield `Order` objects, which are then aggregated by `revenue_by_month_and_region` into a final printed report.

#### Dependencies
* `rotate_backups.sh` depends on standard Unix utilities: `tar`, `du`, `ls`, `mapfile`, `basename`, `date`.
* `sales_report.py` depends on Python standard libraries: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime.date`, `decimal.Decimal`.

#### Cross-cutting
* Error Handling: `rotate_backups.sh` handles invalid arguments and missing directories by exiting; `sales_report.py` raises `ValueError` on data conversion/schema issues and exits with code 1 if no orders are found.
* Configuration: `sales_report.py` uses CLI arguments (`--currency`, `--min-revenue`) to configure the reporting scope.
