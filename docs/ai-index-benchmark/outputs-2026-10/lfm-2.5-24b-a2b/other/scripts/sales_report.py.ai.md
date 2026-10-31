### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:31:39Z
- G: 
- A: 0.0.0
- X: file
---
> Reads order CSV exports and prints monthly revenue per region for a given currency.

#### Purpose
- Generates a monthly sales report from order CSV exports.
- Aggregates revenue by month and region.

#### Type
- Python script module.
- Contains dataclass Order and functions: parse_orders, revenue_by_month_and_region, main.

#### Input
- Command-line arguments: csv_files, --currency (default EUR), --min-revenue (default 0).
- CSV files must include columns: order_date, region, amount, currency.

#### Output
- Prints month, region, revenue lines for entries meeting min revenue.
- Returns exit code 0 on success, 1 if no orders found.

#### Core logic
- parse_orders validates CSV columns and yields Order objects for matching currency rows.
- revenue_by_month_and_region sums amounts per (year-month, region) and sorts results.
- main collects orders, filters by min revenue, and prints formatted output.

#### Public API
- main(argv=None) -> int: Parses CLI args and prints report.
- parse_orders(path, currency) -> generator: Yields Order objects for matching rows.
- revenue_by_month_and_region(orders) -> dict: Aggregates revenue by month and region.

#### Dependencies
- argparse, csv, sys, collections.defaultdict, dataclasses.dataclass, datetime.date, decimal.Decimal, decimal.InvalidOperation.

#### Exceptions / Errors
- ValueError for missing columns or invalid amount parsing.
- No orders found triggers exit code 1 and stderr message.
