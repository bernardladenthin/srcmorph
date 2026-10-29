### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:23:15Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates order CSV data into monthly revenue totals by region for a selected currency.

#### Purpose
- Read one or more order CSV exports and report revenue grouped by month and region.
- Filter output by currency and minimum revenue.

#### Type
- Python module; defines the frozen `Order` dataclass.

#### Input
- CSV files with `order_date`, `region`, `amount`, and `currency` columns.
- CLI arguments: one or more CSV paths, optional `--currency` (default `EUR`), and `--min-revenue` (default `0`).

#### Output
- Prints qualifying rows as tab-separated month, region, and revenue with currency.
- Returns exit code `1` and prints `no orders found` to stderr if no orders match; otherwise returns `0`.

#### Core logic
- `parse_orders` validates required CSV columns, skips rows in other currencies, and yields `Order` values with ISO-parsed dates, stripped regions, and decimal amounts.
- `revenue_by_month_and_region` sums amounts by `YYYY-MM` and region, returning sorted totals.
- `main` combines orders from all input files, filters grouped totals by minimum revenue, and prints results.

#### Public API
- `Order(order_date, region, amount)` -> `Order` — stores a parsed order.
- `parse_orders(path, currency)` -> iterator of `Order` — yields matching-currency orders.
- `revenue_by_month_and_region(orders)` -> `dict` — aggregates monthly regional revenue.
- `main(argv=None)` -> `int` — runs the CLI report.
- `REQUIRED_COLUMNS` -> tuple — lists required CSV fields.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` for missing columns or invalid matching-currency row data; row errors include file path and line number.
- File-opening and CSV parsing errors are not handled locally.
