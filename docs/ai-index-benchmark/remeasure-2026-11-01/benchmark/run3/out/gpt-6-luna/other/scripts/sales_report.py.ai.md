### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:02:19Z
- G: 
- A: 0.0.0
- X: file
---
> Summarizes order CSV exports as monthly revenue totals by region in a selected currency.

#### Purpose
- Reads one or more order CSV files, filters orders by currency, and reports revenue grouped by month and region.
- Omits totals below a configurable minimum revenue.

#### Type
- Python module; defines the frozen `Order` dataclass.

#### Input
- `parse_orders(path, currency)`: CSV file path and currency code.
- `revenue_by_month_and_region(orders)`: iterable of `Order` objects.
- `main(argv=None)`: command-line arguments.
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints tab-separated month, region, and revenue with two decimal places and currency.
- Returns `1` and prints `no orders found` to standard error when no matching orders exist; otherwise returns `0`.
- `parse_orders` yields `Order` objects; `revenue_by_month_and_region` returns a sorted dictionary of totals.

#### Core logic
- `parse_orders` reads a CSV with `csv.DictReader`, checks required headers, skips rows whose currency does not match, and parses date, stripped region, and decimal amount.
- `revenue_by_month_and_region` sums amounts by `(YYYY-MM, region)` and returns totals sorted by key.
- `main` parses CLI options, combines matching orders from all input files, reports no matches, or prints totals meeting the minimum revenue.

#### Public API
- `Order(order_date, region, amount) -> Order` — immutable parsed order record.
- `parse_orders(path, currency) -> iterator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — aggregates monthly regional revenue.
- `main(argv=None) -> int` — runs the report CLI.
- CLI positional argument `csv_files` — one or more CSV paths.
- `--currency` — selects order currency.
- `--min-revenue` — sets the minimum reported total.
- Entry point: `sys.exit(main())`.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.
- `--currency`: default `"EUR"`.
- `--min-revenue`: default `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Uses `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `defaultdict`, `dict`, `sorted`, `print`, and `sys.exit`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` if required CSV columns are missing.
- Invalid dates or amounts raise a path-and-line-number `ValueError`, chained from `ValueError` or `InvalidOperation`.
- File access and CSV parsing errors are not handled locally.
