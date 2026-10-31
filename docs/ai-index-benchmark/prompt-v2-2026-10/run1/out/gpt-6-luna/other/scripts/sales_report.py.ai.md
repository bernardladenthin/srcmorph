### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:33:36Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates order CSV exports into monthly revenue totals by region for a selected currency.

#### Purpose
- Reads one or more order CSV files and reports revenue grouped by month and region.
- Filters orders by currency and suppresses totals below a configurable minimum.

#### Type
- Python module; defines frozen `Order` dataclass.

#### Input
- CSV files with `order_date`, `region`, `amount`, and `currency` columns.
- CLI arguments: one or more CSV paths, `--currency`, and `--min-revenue`.

#### Output
- Prints qualifying totals to stdout as `YYYY-MM<TAB>region<TAB>amount currency`.
- Prints `no orders found` to stderr and returns exit code `1` when no matching orders exist; otherwise returns `0`.

#### Core logic
- `parse_orders` validates required CSV columns, skips rows in other currencies, and yields parsed `Order` values.
- `revenue_by_month_and_region` sums amounts by formatted month and region, then returns sorted totals.
- `main` combines orders from all files, applies the minimum-revenue threshold, and prints qualifying totals.

#### Public API
- `Order(order_date, region, amount) -> Order` — stores a parsed order.
- `parse_orders(path, currency) -> iterator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — aggregates sorted revenue totals.
- `main(argv=None) -> int` — runs the command-line report.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`
- `--currency`: default `"EUR"`.
- `--min-revenue`: `Decimal("0")` default.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Calls `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `dict`, `sorted`, `sys.exit`, and `print`.

#### Exceptions / Errors
- Raises `ValueError` if required columns are missing.
- Wraps invalid dates or amounts in a path-and-line-number `ValueError`.
- File access errors are not handled.
