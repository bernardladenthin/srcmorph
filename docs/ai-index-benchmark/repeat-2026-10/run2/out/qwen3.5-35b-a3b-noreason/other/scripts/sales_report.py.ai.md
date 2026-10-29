### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:05:19Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates monthly revenue by region from CSV order exports for a specified currency.

#### Purpose
- Reads CSV order exports and filters by currency to calculate revenue.
- Prints aggregated revenue totals grouped by month and region.

#### Type
Python script (CLI application) using dataclasses and standard library modules.

#### Input
- `csv_files`: List of file paths to CSV order exports.
- `currency`: Target currency string (default "EUR") to filter rows.
- `min-revenue`: Decimal threshold to filter output rows (default 0).
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Standard output: Tab-separated lines of `month`, `region`, `revenue`, `currency`.
- Standard error: "no orders found" message if no valid data exists.
- Exit code: 0 on success, 1 if no orders found or validation fails.

#### Core logic
- Validates CSV files contain required columns (`order_date`, `region`, `amount`, `currency`).
- Iterates through CSV rows, skipping entries where `currency` does not match the target.
- Parses `order_date` to `date` objects and `amount` to `Decimal` for precision.
- Aggregates `amount` into a dictionary keyed by `(YYYY-MM, region)`.
- Sorts aggregated results by month and region.
- Filters results where revenue meets or exceeds `min-revenue` threshold.
- Formats and prints final revenue report.

#### Public API
- `parse_orders(path, currency) -> Generator[Order]`: Yields valid `Order` objects from a CSV file.
- `revenue_by_month_and_region(orders) -> Dict`: Returns sorted dictionary of revenue totals.
- `main(argv) -> int`: Entry point handling argument parsing and report generation.

#### Dependencies
- `argparse`: Command-line argument parsing.
- `csv`: CSV file reading and validation.
- `sys`: System exit and standard streams.
- `collections.defaultdict`: Aggregation logic.
- `dataclasses.dataclass`: Data structure definition.
- `datetime.date`: Date parsing and formatting.
- `decimal.Decimal`: Financial calculations and formatting.

#### Exceptions / Errors
- `ValueError`: Raised if CSV is missing required columns or contains invalid date/amount formats.
- `InvalidOperation`: Raised during `Decimal` conversion of invalid amount strings.
