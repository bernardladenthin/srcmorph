### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:29:02Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Aggregates CSV order amounts into monthly revenue totals by region for a selected currency, with an optional minimum-revenue filter.

#### Purpose
- Read one or more order CSV exports and report revenue grouped by month and region.
- Skip orders in other currencies; filter displayed totals by minimum revenue.

#### Type
- Python module; defines frozen `Order` dataclass.

#### Input
- `parse_orders(path, currency)`: CSV file with `order_date`, `region`, `amount`, and `currency` columns.
- `main(argv=None)`: command-line arguments; one or more CSV paths, optional currency, and optional minimum revenue.

#### Output
- Prints tab-separated month, region, and two-decimal revenue with currency to stdout.
- Prints `no orders found` to stderr and returns `1` if no matching orders; otherwise returns `0`.

#### Core logic
- `parse_orders` validates required CSV headers, skips rows whose currency differs from the requested currency, and yields `Order` values.
- Parses order dates as ISO dates, strips region whitespace, and parses amounts as `Decimal`.
- `revenue_by_month_and_region` sums amounts by `YYYY-MM` and region, then returns totals sorted by key.
- `main` combines orders from all input files, omits totals below `--min-revenue`, and prints the remaining totals.

#### Public API
- `Order(order_date, region, amount) -> Order` — immutable order data.
- `parse_orders(path, currency) -> iterator[Order]` — yield matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — aggregate and sort revenue totals.
- `main(argv=None) -> int` — parse arguments and run report.
- Internal entry point: `sys.exit(main())` when executed as a script.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`
- CLI positional argument `csv_files`: one or more CSV paths.
- `--currency`: default `EUR`.
- `--min-revenue`: `Decimal`, default `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Calls: `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `defaultdict`, `argparse.ArgumentParser`, `ArgumentParser.add_argument`, `ArgumentParser.parse_args`, `sys.exit`.

#### Exceptions / Errors
- Raises `ValueError` for missing CSV columns, with the file path and missing column names.
- Wraps invalid dates or amounts in `ValueError` with file path and CSV line number; preserves the original exception as the cause.
- Invalid `--min-revenue` input is rejected by `Decimal` argument conversion.
