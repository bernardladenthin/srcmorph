### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:24:23Z
- G: 
- A: 0.0.0
- X: file
---
> Generates monthly sales reports from CSV order data, displaying revenue by month and region.

#### Purpose
- Processes CSV files to calculate monthly sales by region
- Outputs formatted revenue data

#### Type
- Python script

#### Input
- CSV file paths
- Currency code
- Minimum revenue threshold (optional)

#### Output
- Tab-separated revenue data: month, region, amount, currency

#### Core logic
- Parses CSV files and filters by currency
- Aggregates revenue by month and region
- Formats and prints results based on minimum revenue threshold

#### Public API
- `parse_orders(path, currency) -> generator`: Yields Order objects from CSV
- `revenue_by_month_and_region(orders) -> dict`: Calculates revenue totals
- `main(argv) -> int`: Main entry point, handles argument parsing and execution flow

#### Dependencies
- argparse
- csv
- sys
- collections
- dataclasses
- datetime
- decimal

#### Exceptions / Errors
- ValueError: For missing columns, invalid date formats, or currency mismatches
- FileNotFoundError: If specified CSV files don't exist
