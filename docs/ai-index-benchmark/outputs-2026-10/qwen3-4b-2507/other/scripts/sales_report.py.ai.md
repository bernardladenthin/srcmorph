### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:14:56Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a monthly revenue report by region from CSV order files, filtering by currency and minimum revenue threshold.

#### Purpose
- Processes CSV order files to compute monthly revenue per region
- Filters orders by specified currency and minimum revenue threshold

#### Type
Python script; contains dataclass, parser, and utility functions

#### Input
- Path to one or more CSV files containing order data
- Currency code (default: EUR)
- Minimum revenue threshold (default: 0)

#### Output
- Tab-separated lines showing month, region, and revenue (formatted to 2 decimal places) for entries meeting the minimum revenue filter

#### Core logic
- Validates CSV files for required columns and correct currency
- Parses each row into an Order dataclass object
- Groups orders by month and region to sum revenue
- Filters results to include only entries above the minimum revenue threshold
- Prints formatted output to stdout

#### Public API
- `parse_orders(path, currency)` → list[Order]  
  Parses a CSV file into order records matching the given currency  
- `revenue_by_month_and_region(orders)` → dict[(str, str): Decimal]  
  Aggregates revenue by month and region  
- `main(argv=None)` → int  
  Entry point: parses arguments, processes files, and prints report

#### Dependencies
csv, argparse, dataclasses, datetime, decimal

#### Exceptions / Errors
- Raises ValueError if CSV lacks required columns or contains invalid date/amount values  
- Reports missing columns or parsing errors with line numbers  
- Exits with code 1 if no orders are found
