### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:44:52Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates monthly revenue by region from CSV order exports for a specified currency.

#### Purpose
- Reads one or more CSV order exports and filters by currency.
- Calculates and prints total revenue grouped by month and region.

#### Type
Python script (CLI application) using dataclasses and standard library modules.

#### Input
- `csv_files`: List of file paths containing order data.
- `currency`: Target currency string (default "EUR").
- `min-revenue`: Minimum revenue threshold (default 0).
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints tab-separated lines: `YYYY-MM`, `region`, `formatted_amount currency`.
- Returns exit code 1 if no orders found; 0 on success.
- Writes "no orders found" to stderr if no valid data exists.

#### Core logic
- Validates CSV headers against `REQUIRED_COLUMNS` (`order_date`, `region`, `amount`, `currency`).
- Filters rows where `row["currency"]` matches the target `currency`.
- Parses `order_date` to `date` object and `amount` to `Decimal`.
- Skips rows with invalid dates or amounts, raising `ValueError` with line number.
- Aggregates `amount` into a dictionary keyed by `(YYYY-MM, region)`.
- Sorts aggregated results by month and region.
- Filters results where `revenue >= min-revenue` before printing.

#### Public API
- `parse_orders(path, currency) -> Generator[Order]`: Yields valid `Order` objects from a CSV file.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]`: Returns sorted revenue totals.
- `main(argv) -> int`: Entry point handling CLI arguments and execution flow.

#### Dependencies
- `argparse`: Command-line argument parsing.
- `csv`: CSV file reading and validation.
- `sys`: Standard I/O and exit codes.
- `collections.defaultdict`: Efficient aggregation of revenue totals.
- `dataclasses.dataclass`: Definition of the `Order` record.
- `datetime.date`: Date parsing and formatting.
- `decimal.Decimal`: Precise monetary arithmetic.

#### Exceptions / Errors
- `ValueError`: Raised if required CSV columns are missing.
- `ValueError`: Raised if `order_date` or `amount` parsing fails (includes line number).
- `InvalidOperation`: Handled internally during `Decimal` conversion.
