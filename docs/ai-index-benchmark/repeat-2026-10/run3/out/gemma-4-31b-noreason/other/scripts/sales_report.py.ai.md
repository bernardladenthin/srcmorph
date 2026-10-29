### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:31:05Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates monthly sales revenue by region from CSV files for a specific currency.

#### Purpose
* Calculate total revenue per month and region from order exports.
* Filter sales data by currency and minimum revenue threshold.

#### Type
Python script (module).

#### Input
* CSV files containing columns: `order_date`, `region`, `amount`, `currency`.
* Command line arguments: `csv_files` (positional), `--currency` (default "EUR"), `--min-revenue` (Decimal).

#### Output
* Standard output: Tab-separated lines of `month`, `region`, and `revenue` with currency suffix.
* Exit code: 0 on success, 1 if no orders are found.

#### Core logic
* Validates CSV headers against `REQUIRED_COLUMNS`.
* Filters rows by the specified `currency` during parsing.
* Converts `order_date` to ISO date and `amount` to `Decimal` for precision.
* Aggregates totals using a `defaultdict` with a composite key of `(YYYY-MM, region)`.
* Sorts aggregated results by date and region.
* Filters final output based on the `min-revenue` threshold.

#### Public API
* `parse_orders(path, currency) -> Generator[Order]` parses CSV and yields `Order` objects.
* `revenue_by_month_and_region(orders) -> dict` aggregates revenue by month and region.
* `main(argv=None) -> int` handles CLI arguments and orchestrates report generation.

#### Dependencies
* `argparse`
* `csv`
* `sys`
* `collections.defaultdict`
* `dataclasses.dataclass`
* `datetime.date`
* `decimal.Decimal`

#### Exceptions / Errors
* `ValueError`: Raised if CSV columns are missing or if date/amount formats are invalid.
* `InvalidOperation`: Handled during `Decimal` conversion of the amount field.
