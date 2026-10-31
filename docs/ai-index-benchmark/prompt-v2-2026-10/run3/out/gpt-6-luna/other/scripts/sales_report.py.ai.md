### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:00:10Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates CSV orders into monthly revenue totals by region for a selected currency, with an optional minimum-revenue filter.

#### Purpose
- Read one or more order CSV exports and report revenue grouped by month and region.
- Skip orders in other currencies and optionally suppress totals below a threshold.

#### Type
- Python script; defines frozen `Order` dataclass and module-level functions.

#### Input
- CSV files with `order_date`, `region`, `amount`, and `currency` columns.
- CLI positional `csv_files`; optional `--currency` and `--min-revenue`.
- `parse_orders(path, currency)` reads UTF-8 CSV data.

#### Output
- Prints tab-separated month, region, and formatted revenue with currency.
- Prints `no orders found` to stderr and returns exit status `1` when no matching orders exist; otherwise returns `0`.

#### Core logic
- `parse_orders` validates required CSV columns, skips rows whose currency differs from the requested currency, and yields parsed `Order` values.
- Parses dates as ISO format, strips region whitespace, and converts amounts to `Decimal`.
- `revenue_by_month_and_region` sums amounts by `(YYYY-MM, region)` and returns sorted totals.
- `main` combines orders from all input files, filters totals using `min_revenue`, and prints qualifying rows.

#### Public API
- `Order(order_date, region, amount) -> Order` — stores parsed order data.
- `parse_orders(path, currency) -> generator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — aggregates and sorts revenue totals.
- `main(argv=None) -> int` — parses CLI arguments and prints report.
- Internal units: none.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`
- CLI `--currency`: default `"EUR"`.
- CLI `--min-revenue`: `Decimal`, default `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Uses `csv.DictReader`, `date.fromisoformat`, `Decimal`, `defaultdict`, `argparse.ArgumentParser`, `sys.exit`.

#### Exceptions / Errors
- Raises `ValueError` when required CSV columns are missing.
- Wraps invalid dates or amounts in a `ValueError` containing the file path and CSV line number.
- File access and CSV parsing errors are not handled locally.
