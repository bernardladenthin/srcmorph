### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:48:20Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides automated backup rotation with configurable retention and monthly sales revenue reporting from order data.

#### Purpose
- Delivers backup rotation utilities and sales performance reporting from order CSV exports.
- Handles creation of dated compressed archives and generation of monthly revenue reports by region.

#### Responsibilities
- Rotate backup archives: create a dated tar.gz backup of a source directory and retain only the newest N archives.
- Generate sales reports: read order CSV exports and print monthly revenue per region for a given currency.

#### Key units
- rotate_backups.sh: Bash script implementing backup rotation with public API rotate_backups.sh(source_dir, backup_dir, keep) -> void.
- sales_report.py: Python module containing dataclass Order and functions parse_orders, revenue_by_month_and_region, main.
- Order: dataclass representing an order for parsing and aggregation.
- parse_orders(path, currency) -> generator: yields Order objects for matching rows.
- revenue_by_month_and_region(orders) -> dict: aggregates revenue by month and region.
- main(argv=None) -> int: CLI entry point for sales report generation.

#### Data flow
- rotate_backups.sh: Inputs source_dir, backup_dir, keep (default 7). Validates inputs, creates dated archive in backup_dir, prints messages, exits 0 on success.
- sales_report.py: Inputs csv_files, --currency (default EUR), --min-revenue (default 0). Parses CSV via parse_orders, aggregates via revenue_by_month_and_region, prints month/region/revenue lines.

#### Dependencies
- rotate_backups.sh: Bash built-ins (set, trap, mapfile, basename, mkdir, rm, mv) and external utilities tar, date, ls, du.
- sales_report.py: argparse, csv, sys, collections.defaultdict, dataclasses.dataclass, datetime.date, decimal.Decimal, decimal.InvalidOperation.
- Both scripts use standard library modules and common Unix utilities.

#### Cross-cutting
- Both scripts use exit codes to signal success (0) or errors (1 for no orders, 2 for invalid arguments or missing source).
- Error handling includes validation of keep as a positive integer and existence of the source directory.
- Output is printed to stdout/stderr for reports and archive messages.
