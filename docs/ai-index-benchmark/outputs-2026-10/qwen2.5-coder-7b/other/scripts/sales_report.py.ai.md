### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:18:07Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a monthly sales report for specified regions based on order CSV exports.

#### Purpose
- Reads order CSV exports.
- Calculates revenue per month and region.
- Filters orders by currency.
- Prints revenue reports with a minimum revenue threshold.

#### Type
- Python script

#### Input
- `csv_files`: List of CSV file paths.
- `currency`: Currency code (default "EUR").
- `min_revenue`: Minimum revenue threshold (default 0).

#### Output
- Prints revenue per month and region in the format `month\tregion\trevenue currency`.
- Returns 0 if successful, 1 if no orders are found.

#### Core logic
- Parses each CSV file to extract orders in the specified currency.
- Aggregates total revenue by month and region.
- Filters and prints revenue reports based on the minimum revenue threshold.

#### Public API
- `main(argv=None) -> int`: Main function to execute the script.
- `parse_orders(path, currency) -> Order`: Generator function to parse orders from a CSV file.
- `revenue_by_month_and_region(orders) -> dict`: Calculates total revenue by month and region.

#### Dependencies
- `argparse`: For command-line argument parsing.
- `csv`: For reading CSV files.
- `sys`: For standard input/output operations.
- `collections.defaultdict`: For aggregating revenue totals.
- `dataclasses.dataclass`: For defining the `Order` data class.
- `datetime.date`: For handling date objects.
- `decimal.Decimal`: For precise financial calculations.

#### Exceptions / Errors
- Raises `ValueError` if required columns are missing in the CSV file.
- Raises `ValueError` if an invalid date format is encountered.
- Raises `InvalidOperation` if an invalid amount format is encountered.
