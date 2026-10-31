### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:33:11Z
- G: 
- A: 0.0.0
- X: file
---
> Reads order CSV exports and prints total revenue per calendar month and region, filtered by currency and minimum revenue threshold.

#### Purpose
- Monthly sales report from CSV files with order date, region, amount, and currency.
- Prints tab-separated lines: month, region, revenue formatted to two decimals.

#### Type
- Script/module, Python 3.
- Contains `Order` frozen dataclass.

#### Input
- `csv_files`: one or more paths to CSV files with required columns storedorder_date`, `region`, `amount`, `currency`.
- `--currency`: currency filter, default `"EUR"`.
- `--min-revenue`: minimum revenue threshold, default `Decimal("0")`.

#### Output
- Prints to stdout: `month<TAB>region<TAB>revenue.XX currency` for buckets at or above `--min-revenue`.
- Prints `no orders found` to stderr and returns `1` if no orders match.

#### Core logic
- `REQUIRED_COLUMNS` defines mandatory CSV fields.
- `parse_orders` opens file with utf-8, reads via `csv.DictReader`.
- Checks missing columns against `REQUIRED_COLUMNS`; raises `ValueError` listing them.
- Skips rows whose `currency` differs from the requested currency.
- Parses `order_date` with `date.fromisoformat`, strips `region`, converts `amount` with `Decimal`.
- Catches `ValueError` and `InvalidOperation`, raises `ValueError` with file path and line number.
- `revenue_by_month_and_region` sums `Decimal` amounts keyed by `(order_date.strftime("%Y-%m"), region)`.
- Sorts resulting dict by key, yielding chronological month and region order.
- `main` accumulates all orders from all CSV files, returns `1` if empty, otherwise prints filtered results and returns `0`.

#### Public API
- `parse_orders(path, currency)` yields `Order` records from one CSV; skips mismatched currency rows.
- `revenue_by_month_and_region(orders)` -> dict mapping `(month, region)` to total `Decimal` revenue, sorted.
- `main(argv=None)` -> int; runs CLI, returns exit code.
- `Order` frozen dataclass with `order_date: date`, `region: str`, `amount: Decimal`.

#### Dependencies
- `argparse`, `csv`, `sys`
- `collections.defaultdict`
- `dataclasses.dataclass`
- `datetime.date`
- `decimal.Decimal`, `decimal.InvalidOperation`

#### Exceptions / Errors
- `ValueError` on missing columns or malformed order date / amount, with file path and line number.
- `InvalidOperation` caught and re-raised as `ValueError` with context.
- `FileNotFoundError` may surface from `open` for missing input files.
