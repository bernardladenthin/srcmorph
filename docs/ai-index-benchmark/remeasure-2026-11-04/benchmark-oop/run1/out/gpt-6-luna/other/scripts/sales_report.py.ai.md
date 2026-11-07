### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:22:44Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Aggregates order CSV exports into monthly revenue totals by region for a selected currency, with an optional minimum-revenue threshold.

#### Purpose
- Parses orders from one or more CSV files and reports revenue grouped by month and region.
- Filters orders by currency and suppresses totals below the requested minimum.

#### Type
- Python module; defines frozen dataclass `Order`.

#### Input
- `parse_orders(path, currency)`: CSV file path and currency code.
- `revenue_by_month_and_region(orders)`: iterable of `Order` records.
- `main(argv=None)`: optional argument list; CLI accepts one or more `csv_files`, `--currency`, and `--min-revenue`.
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- `parse_orders` yields matching `Order` records; rows in other currencies are skipped.
- `revenue_by_month_and_region` returns a sorted dictionary keyed by `(YYYY-MM, region)`.
- CLI prints tab-separated month, region, and two-decimal revenue with currency; prints `no orders found` to stderr and returns `1` when no matching orders exist, otherwise returns `0`.

#### Core logic
- `Order` stores an ISO-parsed order date, stripped region, and decimal amount.
- `parse_orders` validates required CSV headers, then reads rows starting at line 2 and yields only rows matching the requested currency.
- `revenue_by_month_and_region` sums amounts by formatted month and region, then sorts the grouped entries.
- `main` combines orders from all input files, exits with an error status if none match, and prints totals meeting `--min-revenue`.
- Module entry point passes `main()` to `sys.exit`.

#### Public API
- `Order(order_date, region, amount)` — immutable order data.
- `parse_orders(path, currency)` → iterator of `Order` — reads matching-currency CSV rows.
- `revenue_by_month_and_region(orders)` → `dict` — aggregates monthly regional revenue.
- `main(argv=None)` → `int` — runs the report CLI.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.
- `--currency`: default `EUR`.
- `--min-revenue`: `Decimal` default `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Calls `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `enumerate`, `strftime`, `ArgumentParser.add_argument`, `parse_args`, `print`, and `sys.exit`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` when required columns are missing, including the file path and missing column names.
- Invalid dates or decimal amounts raise a chained `ValueError` containing the file path and CSV line number.
- File-opening and CSV-reading errors are not handled locally.
