### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:09:52Z
- G: 
- A: 0.0.0
- X: file
---
Here is the markdown summary of the `sales_report.py` source file:

```markdown
## Purpose
Calculates monthly sales revenue per region for a given currency, filtering out orders below a minimum revenue threshold.

## Type
Python script (module)

## Input
- CSV files (`csv_files`): Path(s) to CSV export files containing order data.
- `--currency` (default: "EUR"): Currency code for filtering orders.
- `--min-revenue` (default: 0): Minimum revenue threshold to include in output.

## Output
- Prints revenue per month and region for orders meeting the minimum revenue threshold, formatted as:
  ```
  YYYY-MM   Region   Revenue   Currency
  ```

## Core Logic
- Parses CSV files to extract `Order` objects for the specified currency.
- Validates required columns are present in each CSV.
- Aggregates revenue totals by month and region.
- Filters and outputs results based on minimum revenue criteria.

## Public API
- `parse_orders(path, currency)`: Yields `Order` objects from a CSV file.
- `revenue_by_month_and_region(orders)`: Returns a dictionary of monthly revenue totals.

## Dependencies
- `argparse`: For command-line argument parsing.
- `csv`: For reading CSV files.
- `sys`: For standard system output.
- `collections.defaultdict`: For aggregating revenue totals.
- `datetime.date`: For date parsing and formatting.
- `decimal.Decimal`: For precise monetary calculations.

## Exceptions / Errors
- Raises `ValueError` if required columns are missing in a CSV file.
- Raises `ValueError` for invalid numeric conversions in CSV rows.
```
