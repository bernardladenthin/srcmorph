### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T04:22:44Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a monthly sales report by aggregating revenue per region from CSV exports.

#### Purpose
* Processes order CSV files to calculate total revenue.
* Groups and prints revenue by month and geographic region.

#### Type
Python script (procedural with dataclasses).

#### Input
* `csv_files`: One or more file paths to CSV exports.
* `--currency`: Target currency string (default: "EUR").
* `--min-revenue`: Minimum revenue threshold for inclusion (default: 0).
* CSV Columns: `order_date` (ISO format), `region`, `amount`, `currency`.

#### Output
* Standard output: Tab-separated values of `month`, `region`, and `revenue`.
* Standard error: Error messages for missing columns or empty results.
* Exit codes: 0 for success, 1 for no orders found.

#### Core logic
* Validates CSV headers against `REQUIRED_COLUMNS`.
* Filters rows based on the specified `--currency`.
* Parses `order_date` to ISO date objects and `amount` to `Decimal`.
* Aggregates `amount` into a dictionary keyed by `(YYYY-MM, region)`.
* Sorts results by month and region.
* Filters final results by the `min_revenue` threshold.

#### Public API
parse_orders(path, currency) -> Generator[Order, None, None] Yields validated Order objects from a CSV file.
revenue_by_month_and_region(orders) -> dict Aggregates and sorts revenue by month and region.
main(argv) -> int Executes CLI logic and prints the formatted report.

#### Dependencies
* `argparse`
* `csv`
* `sys`
* `collections.defaultdict`
* `dataclasses.dataclass`
* `datetime.date`
* `decimal.Decimal`

#### Exceptions / Errors
* `ValueError`: Raised if CSV columns are missing or if data parsing (date/decimal) fails.
* `InvalidOperation`: Caught during Decimal conversion and wrapped in `ValueError`.
