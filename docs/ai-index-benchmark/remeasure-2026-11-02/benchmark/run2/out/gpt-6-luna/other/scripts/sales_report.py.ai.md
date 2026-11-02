### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:45:34Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Aggregates CSV order exports into monthly revenue totals by region for a selected currency, with an optional minimum-revenue filter.

#### Purpose
- Read one or more order CSV files and report revenue grouped by month and region.
- Skip orders in other currencies and omit totals below the configured threshold.

#### Type
- Python module; defines the frozen `Order` dataclass.

#### Input
- `parse_orders(path, currency)`: CSV file path and currency code.
- `revenue_by_month_and_region(orders)`: iterable of `Order` values.
- `main(argv=None)`: optional command-line argument list.
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- `parse_orders` yields `Order` values; `revenue_by_month_and_region` returns a sorted dictionary keyed by `(YYYY-MM, region)`.
- CLI prints tab-separated month, region, and formatted revenue; prints `no orders found` to stderr when no matching orders exist.
- `main` returns status `0` on success or `1` when no orders match.

#### Core logic
- `Order` stores an ISO-parsed date, stripped region, and decimal amount.
- `parse_orders` validates required CSV headers, skips rows whose currency differs from the requested currency, and reports malformed matching rows with file and line number.
- `revenue_by_month_and_region` sums amounts using `(order_date.strftime("%Y-%m"), region)` keys and returns keys in sorted order.
- `main` combines orders from all input files, applies the minimum-revenue threshold, and formats qualifying amounts to two decimal places.

#### Public API
- `Order(order_date, region, amount) -> Order` — immutable order record.
- `parse_orders(path, currency) -> iterator[Order]` — parse matching-currency rows.
- `revenue_by_month_and_region(orders) -> dict` — aggregate monthly regional revenue.
- `main(argv=None) -> int` — run report command-line flow.
- CLI arguments:
  - `csv_files` — one or more CSV paths.
  - `--currency` — currency filter; defaults to `EUR`.
  - `--min-revenue` — minimum included total; defaults to `Decimal("0")`.
- Entry point: `sys.exit(main())`.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.
- `--currency`: default `EUR`.
- `--min-revenue`: default `Decimal("0")`.
- Exit statuses: `0` success; `1` no matching orders.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Calls: `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `enumerate`, `defaultdict`, `sorted`, `print`, `argparse.ArgumentParser`, `sys.exit`.

#### Exceptions / Errors
- `ValueError` when required CSV columns are missing.
- `ValueError` wrapping invalid dates or amounts, annotated with file path and CSV line number.
- File-opening and CSV-reading errors are not handled.
