### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:19:50Z
- G: 
- A: 0.0.0
- X: file
---
> Generates monthly revenue reports by region from CSV order data, filtering by currency and minimum revenue threshold.

#### Purpose
- Processes CSV order files to compute total revenue per month and region
- Filters results by specified currency and minimum revenue amount
- Outputs formatted tabular data to standard output

#### Type
Python script using dataclasses, defaultdict, and argparse

#### Input
- CSV files containing order data with columns: order_date, region, amount, currency
- Command-line arguments: list of CSV file paths, currency code (default "EUR"), minimum revenue threshold (default 0)
- Standard input for command-line argument parsing

#### Output
- Tabular output to stdout with columns: month, region, revenue amount and currency
- Error messages to stderr when no orders found or parsing fails
- Exit code 1 when no orders match criteria, 0 otherwise

#### Core logic
- Parses each CSV file to extract orders matching specified currency
- Validates required columns are present in each CSV
- Converts date strings to date objects and amount strings to Decimal values
- Aggregates order amounts by month-year and region using defaultdict
- Sorts aggregated results chronologically by month
- Filters and prints results based on minimum revenue threshold

#### Public API
- parse_orders(path, currency) -> generator of Order objects
- revenue_by_month_and_region(orders) -> dict mapping (month, region) to Decimal revenue
- main(argv) -> int exit code

#### Dependencies
- argparse for command-line argument parsing
- csv for CSV file reading
- sys for standard input/output and exit codes
- collections.defaultdict for aggregation
- dataclasses.dataclass for Order struct
- datetime.date for date parsing
- decimal.Decimal for precise monetary calculations

#### Exceptions / Errors
- ValueError raised when CSV files are missing required columns
- ValueError raised when date or amount fields cannot be parsed
- ValueError propagated from Decimal constructor for invalid amount strings
- Prints "no orders found" to stderr when no matching orders exist
