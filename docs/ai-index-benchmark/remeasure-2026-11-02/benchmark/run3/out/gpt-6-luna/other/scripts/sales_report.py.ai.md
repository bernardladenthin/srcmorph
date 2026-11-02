### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:59:33Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Aggregates CSV order amounts into monthly revenue totals by region for a selected currency, with an optional minimum-revenue filter.

#### Purpose
- Read one or more order CSV exports and report revenue grouped by month and region.
- Skip orders in other currencies; omit totals below the configured threshold.

#### Type
- Python module; defines frozen `Order` dataclass.

#### Input
- `main(argv=None)`: command-line arguments.
- Positional `csv_files`: one or more CSV file paths.
- `--currency`: currency to include; defaults to `EUR`.
- `--min-revenue`: minimum displayed revenue; parsed as `Decimal`, defaults to `Decimal("0")`.
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints qualifying totals as tab-separated month, region, and revenue formatted to two decimal places with the selected currency.
- Prints `no orders found` to stderr and returns status `1` when no matching orders exist; otherwise returns `0`.
- `parse_orders` yields `Order` objects; `revenue_by_month_and_region` returns a sorted dictionary of totals.

#### Core logic
- `parse_orders` opens each CSV as UTF-8 with newline handling and validates that all `REQUIRED_COLUMNS` are present.
- Skips rows whose `currency` differs from the requested currency.
- Parses matching rows into `Order`: ISO date, stripped region, and decimal amount; reports invalid row data with file path and line number.
- `revenue_by_month_and_region` sums amounts by `YYYY-MM` and region, then returns totals sorted by key.
- `main` combines orders from all files, handles the empty result, filters grouped totals by `min-revenue`, and prints matches.

#### Public API
- `Order(order_date, region, amount)` -> `Order` — immutable parsed order.
- `parse_orders(path, currency)` -> iterator of `Order` — read matching CSV rows.
- `revenue_by_month_and_region(orders)` -> `dict` — aggregate monthly regional revenue.
- `main(argv=None)` -> `int` — run report command-line flow.
- `__main__` entry point calls `sys.exit(main())`.

#### Constants and configuration
- `REQUIRED_COLUMNS`: `("order_date", "region", "amount", "currency")`.
- `--currency`: default `"EUR"`.
- `--min-revenue`: default `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Calls: `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `defaultdict`, `dict`, `sorted`, `argparse.ArgumentParser`, `ArgumentParser.add_argument`, `ArgumentParser.parse_args`, `print`, `sys.exit`.

#### Exceptions / Errors
- `ValueError` if required CSV columns are missing.
- `ValueError` wrapping invalid dates or amounts, annotated with CSV path and line number.
- `InvalidOperation` from decimal parsing is caught and wrapped as `ValueError`.
- No matching orders: stderr message and return code `1`.
