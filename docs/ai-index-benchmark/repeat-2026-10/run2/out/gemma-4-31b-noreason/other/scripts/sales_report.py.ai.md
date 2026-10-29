### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:58:40Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates monthly revenue by region from CSV order exports for a specific currency.

#### Purpose
* Calculate total revenue per month and region from one or more CSV files.
* Filter data by currency and minimum revenue thresholds.

#### Type
Python script (contains `Order` dataclass).

#### Input
* CSV files containing columns: `order_date`, `region`, `amount`, `currency`.
* Command line arguments: `csv_files` (positional), `--currency` (default "EUR"), `--min-revenue` (default 0).

#### Output
* Standard output: Tab-separated lines of `month`, `region`, and `revenue` formatted to 2 decimal places.
* Exit code: 0 on success, 1 if no orders are found.

#### Core logic
* Validates presence of `REQUIRED_COLUMNS` in CSV headers.
* Filters rows by the specified `currency` during parsing.
* Converts `order_date` to ISO date and `amount` to `Decimal` for precision.
* Aggregates `amount` using a `defaultdict` keyed by a tuple of `(YYYY-MM, region)`.
* Sorts aggregated totals by date and region.
* Filters final output based on the `--min-revenue` threshold.

#### Public API
* `parse_orders(path, currency) -> Generator[Order]` yields valid Order objects from a CSV.
* `revenue_by_month_and_region(orders) -> dict` aggregates revenue by month and region.
* `main(argv=None) -> int` orchestrates CLI parsing, data processing, and printing.

#### Dependencies
* `argparse`
* `csv`
* `sys`
* `collections.defaultdict`
* `dataclasses.dataclass`
* `datetime.date`
* `decimal.Decimal`

#### Exceptions / Errors
* `ValueError`: Raised if CSV columns are missing or if date/amount conversion fails.
* `InvalidOperation`: Handled during `Decimal` conversion of the `amount` field.
