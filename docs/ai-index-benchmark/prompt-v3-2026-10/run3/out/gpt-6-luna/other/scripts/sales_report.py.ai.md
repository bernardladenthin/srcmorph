### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:37:48Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes order revenue by month and region for a selected currency, with an optional minimum-revenue filter.

#### Purpose
- Reads one or more order CSV exports and reports matching-currency revenue grouped by month and region.
- Skips other currencies and omits groups below the configured revenue threshold.

#### Type
- Python module; defines frozen dataclass `Order`.

#### Input
- `parse_orders(path, currency)`: CSV file path and currency code.
- CSV columns: `order_date`, `region`, `amount`, `currency`.
- `main(argv=None)`: command-line arguments; one or more CSV paths, optional `--currency`, optional `--min-revenue`.

#### Output
- `revenue_by_month_and_region(orders)` returns a sorted dictionary keyed by `(YYYY-MM, region)` with `Decimal` totals.
- Prints qualifying rows as tab-separated month, region, and revenue with two decimal places and currency.
- Prints `no orders found` to stderr and returns status `1` when no matching orders exist; otherwise returns `0`.

#### Core logic
- `parse_orders` validates required CSV headers, skips rows whose currency differs from the requested currency, and yields `Order` values.
- Parses ISO-format order dates and decimal amounts; strips whitespace from region names.
- `revenue_by_month_and_region` sums amounts by formatted month and region, then returns entries sorted by key.
- `main` loads matching orders from all input files, filters grouped totals using `revenue >= min_revenue`, and prints the report.

#### Public API
- `Order(order_date, region, amount) -> Order` — immutable order record.
- `parse_orders(path, currency) -> generator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — groups and sorts revenue totals.
- `main(argv=None) -> int` — parses arguments and prints the report.
- Entry point: `sys.exit(main())`.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.
- `--currency`: default `"EUR"`.
- `--min-revenue`: `Decimal` value, default `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Calls: `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `dict`, `sorted`, `sys.exit`.

#### Exceptions / Errors
- Raises `ValueError` with file path when required CSV columns are missing.
- Raises `ValueError` with file path and line number for invalid dates or decimal amounts; chains the original `ValueError` or `InvalidOperation`.
