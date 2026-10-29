### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:59:43Z
- G: 
- A: 0.0.0
- X: file
---
> This script processes multiple CSV order files to calculate and report total revenue aggregated by month and geographical region, filtering by currency and minimum revenue.

#### Purpose
* Reads order data from specified CSV files.
* Calculates total revenue grouped by month and region.

#### Type
Module (Python script)

#### Input
* `csv_files`: List of file paths containing order data.
* `--currency`: Target currency string (default "EUR").
* `--min-revenue`: Minimum revenue threshold (Decimal, default 0).
* CSV fields: `order_date` (ISO format), `region` (string), `amount` (string convertible to Decimal), `currency` (string).

#### Output
* Prints formatted lines to stdout: `YYYY-MM\tRegion\tRevenue EUR`.
* Exits with status 0 on success, 1 if no orders are found.

#### Core logic
* `parse_orders`: Iterates CSV rows, validates required columns, filters by target currency, and yields `Order` objects, raising errors on data format issues.
* `revenue_by_month_and_region`: Aggregates yielded `Order` amounts into a `defaultdict` keyed by (YYYY-MM, region).
* `main`: Parses command-line arguments, collects all orders from input files, calculates totals, and prints results exceeding `min_revenue`.

#### Public API
parse_orders(path, currency) -> Iterator[Order] : Yields orders matching currency from CSV.
revenue_by_month_and_region(orders) -> dict : Aggregates order amounts by month and region.
main(argv) -> int : Parses arguments, processes orders, prints report.

#### Dependencies
argparse
csv
collections.defaultdict
dataclasses
datetime.date
decimal.Decimal
sys

#### Exceptions / Errors
ValueError: Raised if CSV file is missing required columns or if data conversion (date/amount) fails.
