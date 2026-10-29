### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:58:04Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational utilities for automated backup rotation and financial reporting aggregation from CSV order exports.

#### Purpose
- Executes shell-based backup retention policies and Python-based revenue analysis for multi-region order data.

#### Responsibilities
- **Backup Management**: Orchestrates timestamped archiving and cleanup of directory snapshots using shell utilities.
- **Financial Aggregation**: Ingests CSV order streams, validates schemas, and computes revenue totals by month and region.

#### Key units
- `rotate_backups.sh`: Entry point for creating dated `.tar.gz` archives and enforcing retention counts.
- `sales_report.py`: CLI application entry point for parsing CSVs and generating revenue reports.
- `parse_orders(path, currency)`: Generator yielding validated `Order` dataclass instances from CSV files.
- `revenue_by_month_and_region(orders)`: Aggregation function returning sorted revenue dictionaries keyed by `(YYYY-MM, region)`.
- `Order`: Dataclass representing individual order records with `order_date`, `region`, `amount`, and `currency`.

#### Data flow
- **Backup Pipeline**: Validates inputs -> creates temporary `.partial` archive -> moves to final timestamped name -> lists existing archives -> deletes oldest beyond `keep` limit.
- **Report Pipeline**: Parses CLI args -> validates CSV headers -> iterates rows filtering by currency -> parses `amount` to `Decimal` -> aggregates into `(month, region)` dict -> sorts and filters by `min-revenue` -> outputs tab-separated results.

#### Dependencies
- **Internal**: `rotate_backups.sh` relies on `tar`, `date`, `ls`, `mv`, `rm`, `du`, `cut`, `mapfile`.
- **Internal**: `sales_report.py` relies on `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.
- **External**: CSV files containing `order_date`, `region`, `amount`, `currency` columns.

#### Cross-cutting
- **Error Handling**: Both scripts exit with specific codes (0/1/2) and usage messages on validation failure.
- **Resource Safety**: `rotate_backups.sh` uses `trap` to ensure cleanup of partial archive files on failure.
- **Data Precision**: `sales_report.py` enforces `Decimal` arithmetic for currency calculations to avoid floating-point errors.
- **Validation**: Strict schema checks for CSV headers and input parameter types (e.g., positive integer for `keep`).
