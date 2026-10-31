### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:36:04Z
- G: 
- A: 0.0.0
- X: file
---
> Reads order CSV exports and prints revenue per month and region.

#### Purpose
- Generates monthly sales report from order CSV exports
- Prints revenue per month and region

#### Type
- Python script

#### Input
- CSV files via positional argument csv_files
- Currency filter via --currency argument
- Minimum revenue threshold via --min-revenue argument
- CSV columns: order_date, region, amount, currency

#### Output
- Revenue lines to stdout: month, region, revenue, currency
- Error message to stderr: "no orders found"
- Exit code 0 on success, 1 on no orders

#### Core logic
- Reads CSV files and yields Order objects in specified currency
- Parses order_date as date, region as string, amount as Decimal
- Aggregates revenue by (month, region) tuple
- Sorts totals by month and region
- Filters and prints revenue meeting minimum threshold

#### Public API
- parse_orders(path, currency) -> generator of Order: yields orders from CSV in given currency
- revenue_by_month_and_region(orders) -> dict of (month, region) to Decimal: aggregates revenue
- main(argv=None) -> int: parses arguments and prints report

#### Dependencies
- argparse, csv, sys, defaultdict, dataclass, date, Decimal, InvalidOperation

#### Exceptions / Errors
- ValueError: missing required CSV columns
- ValueError: invalid date or amount in CSV row
- ValueError: currency mismatch skipped silently
