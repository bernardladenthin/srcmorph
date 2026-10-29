### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:41:20Z
- G: 
- A: 0.0.0
- X: file
---
> Monthly sales report: reads order CSV exports and prints revenue per month and region.

#### Purpose
- Reads one or more CSV files containing order data and prints aggregated revenue grouped by month and region.
- Filters orders by an optional currency code and a minimum revenue threshold.

#### Type
- Language: Python
- Unit: Script (module-level functions; no classes)
- Entry point: `if __name__ == "__main__": sys.exit(main())`

#### Input
- `csv_files`: one or more paths to CSV files (command-line arguments)
- `currency`: 3-letter currency code (default: `"EUR"`); only rows matching this currency are processed
- `min_revenue`: Decimal threshold (default: `0`); rows with revenue below this are omitted from output

#### Output
- Printed to stdout: tab-separated lines of `month\tregion\trevenue currency`
- Each line represents `(YYYY-MM, region)` with the total revenue as a formatted Decimal
- Exits with code `1` and prints `"no orders found"` to stderr if no matching orders exist

#### Core logic
- Parse each CSV file, skipping rows whose `currency` does not match the specified code
- Validate that required columns (`order_date`, `region`, `amount`, `currency`) are present; raise `ValueError` if missing
- Parse `order_date` via `date.fromisoformat`, `amount` via `Decimal`; raise `ValueError` on malformed data
- Aggregate totals per `(month, region)` using a `defaultdict(Decimal)`
- Sort results lexicographically by `(month, region)` and print those meeting the `min_revenue` threshold

#### Public API
- `parse_orders(path, currency)` -> yields `Order` objects from a single CSV file, skipping non-matching currencies
- `revenue_by_month_and_region(orders)` -> `dict` mapping `(month_str, region)` -> `Decimal` total revenue
- `main(argv=None)` -> entry point; parses arguments, processes files, prints revenue summary

#### Dependencies
- `argparse` (standard library)
- `csv` (standard library)
- `sys` (standard library)
- `collections.defaultdict`
- `dataclasses.dataclass`
- `datetime.date`
- `decimal.Decimal`, `decimal.InvalidOperation`

#### Exceptions / Errors
- `ValueError` raised if a CSV file is missing required columns
- `ValueError` raised with file path and line number if `order_date` or `amount` cannot be parsed
- `ValueError` propagated from `Decimal(row["amount"])` on invalid numeric format
