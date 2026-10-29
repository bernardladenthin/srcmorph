### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:06:47Z
- G: 
- A: 0.0.0
- X: file
---
> This script processes multiple CSV order files to calculate and report total revenue aggregated by month and geographical region, filtering by currency and minimum revenue threshold.

#### Purpose
* Reads order data from specified CSV files.
* Calculates total revenue grouped by month and region.

#### Type
Script (Python)

#### Input
* `csv_files`: List of paths to CSV order files (command line argument).
* `--currency`: Target currency string (default "EUR").
* `--min-revenue`: Minimum revenue threshold (Decimal, default 0).
* CSV fields: `order_date` (ISO format), `region` (string), `amount` (string convertible to Decimal), `currency` (string).

#### Output
* Prints formatted lines to stdout: `YYYY-MM\tRegion\tRevenue EUR`.
* Exits with status 1 if no orders matching criteria are found.

#### Core logic
* `parse_orders`: Iterates CSV rows, validates required columns, filters by target currency, and yields `Order` objects, raising errors on data format issues.
* `revenue_by_month_and_region`: Aggregates `Order.amount` into a `defaultdict` keyed by `(YYYY-MM, region)`.
* `main`: Parses CLI arguments, collects all orders using `parse_orders`, calculates totals using `revenue_by_month_and_region`, and prints results only if revenue meets `min_revenue`.

#### Public API
parse_orders(path, currency) -> Iterator[Order] : Yields orders matching currency from CSV.
revenue_by_month_and_region(orders) -> dict : Calculates total revenue per month/region.
main(argv) -> int : Executes report generation based on CLI args.

#### Dependencies
argparse
csv
collections.defaultdict
dataclasses
datetime.date
decimal.Decimal

#### Exceptions / Errors
ValueError: Raised by `parse_orders` if CSV is missing required columns or if data conversion fails (e.g., invalid date/amount).
