### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:53:03Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Summarizes CSV order exports as monthly revenue totals by region in a selected currency.

#### Purpose
- Reads one or more order CSV files, filters orders by currency, and reports revenue grouped by month and region.
- Omits totals below a configurable minimum revenue.

#### Type
- Python script; defines immutable `Order` dataclass.

#### Input
- `parse_orders(path, currency)`: CSV file with `order_date`, `region`, `amount`, and `currency` columns.
- `main(argv=None)`: positional `csv_files` and CLI options `--currency` and `--min-revenue`.
- Order dates use ISO format; amounts are parsed as `Decimal`.

#### Output
- Prints qualifying rows as tab-separated month, region, and revenue with two decimal places and currency.
- Prints `no orders found` to stderr and returns `1` if no matching orders exist; otherwise returns `0`.

#### Core logic
- `parse_orders` validates required CSV columns, skips rows in other currencies, and yields `Order` values with stripped region names.
- `revenue_by_month_and_region` sums amounts by `(YYYY-MM, region)` and returns totals in sorted key order.
- `main` combines orders from all input files, filters grouped totals using the minimum revenue, and prints qualifying results.

#### Public API
- `Order(order_date, region, amount) -> Order` — immutable order data.
- `parse_orders(path, currency) -> iterator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — aggregates monthly regional revenue.
- `main(argv=None) -> int` — parses arguments and prints the report.
- Entry point: `sys.exit(main())`.
- CLI: `csv_files` — one or more CSV paths; `--currency` — currency filter; `--min-revenue` — minimum included total.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`
- `--currency`: default `EUR`.
- `--min-revenue`: default `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` when required columns are missing or a matching-currency row has an invalid date or amount; row errors include file path and line number.
- File-opening and CSV parsing errors are not handled here.
