### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T21:22:13Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates order CSV data into monthly revenue totals by region for a selected currency and prints totals meeting a minimum threshold.

#### Purpose
- Reads one or more order CSV exports and reports monthly revenue by region.
- Filters orders by currency and reported revenue by minimum amount.

#### Type
- Python module; defines the `Order` dataclass and command-line entry point.

#### Input
- CSV files with `order_date`, `region`, `amount`, and `currency` columns.
- CLI arguments: one or more file paths, `--currency` (default `EUR`), and `--min-revenue` (default `0`).

#### Output
- Prints tab-separated month, region, and revenue with two decimal places and the selected currency.
- Writes `no orders found` to stderr and returns status `1` if no matching orders exist; otherwise returns `0`.

#### Core logic
- `parse_orders` validates required CSV headers, skips rows in other currencies, and yields `Order` values parsed from ISO dates, stripped regions, and decimal amounts.
- `revenue_by_month_and_region` sums order amounts by `(YYYY-MM, region)` and returns totals sorted by key.
- `main` combines orders from all input files, filters totals against `--min-revenue`, and prints qualifying results.
- Invalid matching-row dates or amounts become contextual `ValueError`s containing the file path and CSV line number.

#### Public API
- `Order(order_date, region, amount) -> Order` — stores a parsed order.
- `parse_orders(path, currency) -> iterator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — aggregates monthly regional revenue.
- `main(argv=None) -> int` — runs the report CLI.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` for missing required columns or invalid matching-row date/amount data.
- File access and CSV parsing errors are not handled locally.
- `main` returns `1` when no matching orders are found.
