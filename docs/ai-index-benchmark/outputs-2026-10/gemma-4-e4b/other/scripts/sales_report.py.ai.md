### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:59:30Z
- G: 
- A: 0.0.0
- X: file
---
> This script processes multiple CSV order files to generate a monthly and regional revenue report, filtering by currency and minimum revenue.

#### Purpose
* Reads order data from specified CSV files.
* Calculates and prints total revenue aggregated by month and region.

#### Type
Module (Python script)

#### Input
* `csv_files`: List of paths to CSV files containing order data.
* `--currency`: Target currency string (default "EUR").
* `--min-revenue`: Minimum revenue threshold (Decimal, default 0).
* CSV fields: `order_date` (ISO format), `region` (string), `amount` (string convertible to Decimal), `currency` (string).

#### Output
* Prints formatted lines to stdout: "YYYY-MM\tRegion\tRevenue EUR".
* Exits with status 1 if no orders are found.

#### Core logic
* `parse_orders`: Iterates CSV rows, validates required columns, filters by target currency, and yields `Order` objects after parsing date and amount.
* `revenue_by_month_and_region`: Aggregates `Order` amounts into a `defaultdict` keyed by (YYYY-MM, region).
* `main`: Parses CLI arguments, collects all orders using `parse_orders`, calculates totals using `revenue_by_month_and_region`, and filters/prints results based on `min_revenue`.

#### Public API
parse_orders(path, currency) -> Iterator[Order] : Yields orders matching currency from CSV.
revenue_by_month_and_region(orders) -> dict : Aggregates revenue by month and region.
main(argv) -> int : Executes report generation based on CLI args.

#### Dependencies
argparse, csv, sys, collections.defaultdict, dataclasses, datetime.date, decimal.Decimal

#### Exceptions / Errors
* ValueError: Raised by `parse_orders` if CSV is missing required columns or if data conversion fails (date/amount).
* System exit code 1: If no orders are processed.
