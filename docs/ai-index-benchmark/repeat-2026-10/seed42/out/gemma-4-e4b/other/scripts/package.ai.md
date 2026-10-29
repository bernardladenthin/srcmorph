### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:53:42Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> This package provides utility scripts for automated system maintenance, specifically managing data backups and generating structured financial sales reports from raw order data.

#### Purpose
* To automate the creation and lifecycle management of compressed system backups.
* To process raw transactional CSV data into aggregated, filtered revenue reports.

#### Responsibilities
* Backup Management: Creating dated, compressed archives and enforcing retention policies via `rotate_backups.sh`.
* Data Ingestion & Transformation: Reading, validating, and parsing order records from multiple CSV files (`sales_report.py`).
* Reporting: Aggregating parsed order data to calculate total revenue segmented by month and region (`sales_report.py`).

#### Key units
* rotate_backups.sh: Shell script responsible for executing backup creation and purging old archives.
* sales_report.py: Python module handling CSV parsing, data aggregation, and report generation.
* Order (dataclass): Represents a single parsed order record within `sales_report.py`.

#### Data flow
* `rotate_backups.sh` takes directory paths and a retention count as input, producing compressed archive files in the destination directory.
* `sales_report.py` takes a list of CSV file paths, filters them by currency and minimum revenue, parses them into `Order` objects, and then aggregates these objects into a final revenue dictionary keyed by (YYYY-MM, region).

#### Dependencies
* **rotate_backups.sh**: Relies on standard Unix utilities (`tar`, `ls`, `date`).
* **sales_report.py**: Depends on Python standard libraries including `argparse`, `csv`, `collections.defaultdict`, and `decimal.Decimal`.

#### Cross-cutting
* **Error Handling**: `rotate_backups.sh` handles invalid input arguments by exiting; `sales_report.py` raises `ValueError` upon data parsing or schema issues.
* **Data Structure**: `sales_report.py` utilizes `dataclasses` for structured representation of input records (`Order`).
