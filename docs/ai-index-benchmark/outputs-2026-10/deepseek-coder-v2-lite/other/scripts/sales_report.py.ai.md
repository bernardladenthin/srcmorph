### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T02:36:28Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices

#### Purpose
- Reads order CSV exports.
- Prints revenue per month and region.

#### Type
- Python script.

#### Input
- CSV file paths.
- Currency for filtering.
- Minimum revenue threshold.

#### Output
- Prints monthly revenue per region.

#### Core logic
- Parses CSV files for orders in the specified currency.
- Skips orders with incorrect columns.
- Skips orders with invalid date formats or amounts.
- Aggregates revenue by month and region.
- Filters and prints revenue above the minimum threshold.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]`: Parses orders from a CSV file.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]`: Aggregates revenue by month and region.
- `main(argv=None) -> int`: Main function to run the script with command-line arguments.

#### Dependencies
- `argparse`: For command-line argument parsing.
- `csv`: For reading CSV files.
- `sys`: For system-related operations.
- `collections.defaultdict`: For aggregating revenue.
- `datetime.date`: For handling dates.
- `decimal.Decimal`: For handling monetary amounts.

#### Exceptions / Errors
- Raises `ValueError` if a CSV file is missing required columns or contains invalid data.
