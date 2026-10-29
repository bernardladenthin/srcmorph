### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:57:39Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates monthly revenue by region from CSV order exports for a specified currency.

#### Purpose
- Reads one or more CSV order exports and filters by currency.
- Calculates and prints total revenue grouped by month and region.

#### Type
Python script (CLI application) using `dataclass` and `argparse`.

#### Input
- `csv_files`: List of file paths to CSV order exports.
- `currency`: Target currency string (default "EUR").
- `min-revenue`: Minimum revenue threshold (default 0).
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Standard output: Tab-separated lines of `month`, `region`, `revenue`, `currency`.
- Exit code: 0 on success, 1 if no orders found.
- Standard error: "no orders found" message if no valid data exists.

#### Core logic
- Validates CSV headers against `REQUIRED_COLUMNS` (`order_date`, `region`, `amount`, `currency`).
- Iterates rows, skipping entries where `currency` does not match the target.
- Parses `order_date` to `date` object and `amount` to `Decimal`.
- Aggregates `amount` into a dictionary keyed by `(YYYY-MM, region)`.
- Sorts aggregated results by month and region.
- Filters results where revenue meets `min-revenue` threshold before printing.

#### Public API
- `parse_orders(path, currency) -> Generator[Order]`
  - Yields valid `Order` objects from a CSV file.
- `revenue_by_month_and_region(orders) -> Dict`
  - Returns sorted dictionary of revenue totals by month and region.
- `main(argv) -> int`
  - Parses CLI arguments and executes the report generation.

#### Dependencies
- `argparse`: Command-line argument parsing.
- `csv`: CSV file reading and validation.
- `sys`: System exit and standard streams.
- `collections.defaultdict`: Revenue aggregation.
- `dataclasses.dataclass`: Order data structure.
- `datetime.date`: Date parsing and formatting.
- `decimal.Decimal`: Precise currency arithmetic.

#### Exceptions / Errors
- `ValueError`: Raised if CSV columns are missing or data parsing fails (invalid date/amount).
- `InvalidOperation`: Raised during `Decimal` conversion of invalid amount strings.
