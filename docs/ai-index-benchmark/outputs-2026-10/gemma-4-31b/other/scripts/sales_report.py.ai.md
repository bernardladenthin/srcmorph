### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T21:38:39Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates and reports monthly revenue by region from CSV order exports.

#### Purpose
* Process multiple CSV files containing sales data to calculate revenue.
* Filter results by specific currency and a minimum revenue threshold.

#### Type
Python script containing a frozen dataclass `Order` and utility functions.

#### Input
* CSV files containing columns: `order_date`, `region`, `amount`, `currency`.
* CLI arguments: `csv_files` (positional list), `--currency` (string, default "EUR"), `--min-revenue` (Decimal, default 0).

#### Output
* Standard output: Tab-separated lines containing `month` (YYYY-MM), `region`, and `revenue` formatted to 2 decimal places.
* Standard error: Error message if no orders are found.
* Return value: Integer exit code (0 for success, 1 for no data).

#### Core logic
* Validates CSV headers against `REQUIRED_COLUMNS` (`order_date`, `region`, `amount`, `currency`).
* Filters rows by the specified `currency` during CSV parsing.
* Converts `order_date` via `date.fromisoformat` and `amount` via `Decimal`.
* Aggregates revenue using a `defaultdict` with a composite key of `(YYYY-MM, region)`.
* Sorts aggregated totals and filters out entries below the `min_revenue` threshold.

#### Public API
* `parse_orders(path, currency) -> Generator[Order]` : Yields valid `Order` objects from a CSV.
* `revenue_by_month_and_region(orders) -> dict` : Sums revenue grouped by month and region.
* `main(argv) -> int` : Handles CLI arguments and orchestrates data processing.

#### Dependencies
* `argparse`
* `csv`
* `sys`
* `collections.defaultdict`
* `dataclasses.dataclass`
* `datetime.date`
* `decimal.Decimal`

#### Exceptions / Errors
* `ValueError`: Raised if required CSV columns are missing or if date/amount parsing fails.
* `InvalidOperation`: Raised during failed `Decimal` conversion.
