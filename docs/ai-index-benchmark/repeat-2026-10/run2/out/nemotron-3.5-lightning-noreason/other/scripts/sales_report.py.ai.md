### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:30Z
- G: 
- A: 0.0.0
- X: file
---
> Monthly sales report: reads order CSV exports and prints revenue per month and region.

#### Purpose
- Reads one or more CSV files containing order data
- Aggregates revenue by month and region
- Filters results by minimum revenue threshold and currency

#### Type
Python script; uses `argparse`, `csv`, `dataclasses`, `defaultdict`

#### Input
- `csv_files`: one or more CSV file paths (required)
- `currency`: target currency filter (default: "EUR")
- CSV must contain columns: `order_date`, `region`, `amount`, `currency`
- Each row: order date, region name, monetary amount, currency code

#### Output
- Printed to stdout: `month\tregion\trevenue currency`
- Format: `YYYY-MM\tRegion\tamount.currency`
- Returns exit code 0 on success, 1 if no orders found
- Error messages to stderr for missing columns or invalid data

#### Core logic
- Parse each CSV file, skipping rows with mismatched currency
- Validate required columns exist; raise ValueError if missing
- Parse `order_date` via `date.fromisoformat()`, convert `amount` via `Decimal`
- Accumulate totals in a `defaultdict(Decimal)` keyed by `(month_str, region)`
- Sort results by (month, region) and print rows meeting `--min-revenue` threshold

#### Public API
- `parse_orders(path, currency)` -> yields `Order` objects from one CSV file
- `revenue_by_month_and_region(orders)` -> `dict` of `(month, region) -> total_revenue`
- `main(argv=None)` -> `int` exit code; orchestrates CLI argument parsing and report generation

#### Dependencies
- `argparse` for CLI argument handling
- `csv` for CSV file reading
- `sys` for exit codes and stderr output
- `collections.defaultdict` for revenue aggregation
- `dataclasses.dataclass` for `Order` data structure
- `datetime.date` for order date handling
- `decimal.Decimal` for precise monetary calculations

#### Exceptions / Errors
- `ValueError` raised if CSV missing required columns (`order_date`, `region`, `amount`, `currency`)
- `ValueError` raised with file path and line number on invalid date or amount format
- Currency mismatch causes row to be silently skipped
