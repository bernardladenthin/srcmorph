### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:37:01Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates order CSV data into monthly revenue totals by region for a selected currency.

#### Purpose
- Parses orders from one or more CSV exports and reports monthly revenue by region.
- Filters results by currency and minimum revenue.

#### Type
- Python module; defines the frozen `Order` dataclass.

#### Input
- CSV files with `order_date`, `region`, `amount`, and `currency` columns.
- CLI options: `--currency` (default `EUR`) and `--min-revenue` (default `0`).

#### Output
- Prints tab-separated month, region, and formatted revenue.
- Returns exit code `1` when no matching orders are found; otherwise `0`.

#### Core logic
- `parse_orders` skips rows in other currencies and yields parsed `Order` values.
- `revenue_by_month_and_region` sums amounts by `YYYY-MM` and region, then sorts the totals.
- `main` loads orders from all input files, filters totals by minimum revenue, and prints results.

#### Public API
- `Order(order_date, region, amount)` -> `Order` — stores an order’s date, region, and amount.
- `parse_orders(path, currency)` -> iterator of `Order` — reads matching-currency orders.
- `revenue_by_month_and_region(orders)` -> `dict` — aggregates monthly regional revenue.
- `main(argv=None)` -> `int` — runs the report command.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` for missing required columns or invalid matching-currency rows; messages include the file and, for row errors, line number.
- File-opening errors propagate from `open`.
