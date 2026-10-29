### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:51:17Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates order CSV data into monthly revenue totals by region for a selected currency.

#### Purpose
- Reads one or more order CSV exports and reports monthly revenue by region.
- Filters orders by currency and optionally suppresses totals below a minimum revenue.

#### Type
- Python module with `Order` dataclass and command-line entry point.

#### Input
- CSV files with `order_date`, `region`, `amount`, and `currency` columns.
- CLI options: `--currency` (default `EUR`) and `--min-revenue` (default `0`).

#### Output
- Prints qualifying totals as tab-separated month, region, and formatted revenue.
- Returns exit code `1` and writes `no orders found` to stderr when no matching orders exist; otherwise returns `0`.

#### Core logic
- `parse_orders` validates required CSV columns, skips rows in other currencies, and yields parsed orders.
- Parses ISO dates, strips region whitespace, and converts amounts to `Decimal`.
- `revenue_by_month_and_region` sums amounts by `YYYY-MM` and region, returning sorted totals.
- `main` combines orders across files, filters totals by minimum revenue, and prints results.

#### Public API
- `Order(order_date, region, amount)` -> `Order` — stores a parsed order.
- `parse_orders(path, currency)` -> iterator of `Order` — yields matching-currency orders.
- `revenue_by_month_and_region(orders)` -> `dict` — aggregates revenue by month and region.
- `main(argv=None)` -> `int` — runs the report CLI.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` for missing columns or invalid date/amount values; errors include file path and, for row errors, line number.
- File-opening and CSV-reading errors are not handled locally.
