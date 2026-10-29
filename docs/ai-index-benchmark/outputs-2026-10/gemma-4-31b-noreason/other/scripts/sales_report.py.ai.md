### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T09:54:42Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates monthly revenue by region from CSV order exports for a specific currency.

#### Purpose
* Calculate total revenue per month and region from one or more CSV files.
* Filter data by currency and minimum revenue thresholds.

#### Type
Python script (contains `Order` dataclass and utility functions).

#### Input
* CSV files containing columns: `order_date`, `region`, `amount`, `currency`.
* Command-line arguments: `csv_files` (positional), `--currency` (default "EUR"), `--min-revenue` (Decimal).

#### Output
* Standard output: Tab-separated lines of `month`, `region`, and `revenue` formatted to 2 decimal places.
* Exit code: 0 on success, 1 if no orders are found.

#### Core logic
* Validates CSV headers against `REQUIRED_COLUMNS`.
* Filters rows by the specified `currency` and converts `amount` to `Decimal` and `order_date` to `date` objects.
* Aggregates totals using a `defaultdict` with a composite key of `(YYYY-MM, region)`.
* Sorts aggregated results by date and region.
* Filters final output based on the `--min-revenue` threshold.

#### Public API
* `parse_orders(path, currency) -> Generator[Order]` : Yields validated `Order` objects from a CSV file.
* `revenue_by_month_and_region(orders) -> dict` : Sums `Order.amount` grouped by month and region.
* `main(argv) -> int` : Orchestrates CLI parsing, data processing, and printing.

#### Dependencies
* `argparse`
* `csv`
* `sys`
* `collections.defaultdict`
* `dataclasses.dataclass`
* `datetime.date`
* `decimal.Decimal`

#### Exceptions / Errors
* `ValueError`: Raised if CSV columns are missing or if date/amount parsing fails.
* `InvalidOperation`: Handled during `Decimal` conversion of the `amount` field.
