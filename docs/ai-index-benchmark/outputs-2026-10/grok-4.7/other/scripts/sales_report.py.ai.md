### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:45:20Z
- G: 
- A: 0.0.0
- X: file
---
I will index the Python source file according to the user's instructions.

The file is a Python script that reads CSV order exports and prints monthly revenue per region. It defines an Order dataclass and functions to parse orders, compute revenue by month and region, and run the main entry point.> Aggregates order CSV exports into monthly revenue totals by region for a chosen currency.

#### Purpose
- Builds a monthly sales report from order CSV exports.
- Prints revenue per calendar month and region, filtered by currency and a minimum revenue threshold.

#### Type
- Python script/module with frozen dataclass `Order`; CLI entry `main`.

#### Input
- One or more CSV paths (`csv_files`) with columns `order_date`, `region`, `amount`, `currency`.
- CLI `--currency` (default `EUR`) and `--min-revenue` (`Decimal`, default `0`).
- Optional `argv` passed to `main`.

#### Output
- Stdout TSV lines: `YYYY-MM`, region, revenue to two decimals, and currency.
- Exit code `0` on printed report; `1` and stderr `no orders found` when no matching orders.
- `ValueError` when required columns are missing or a row date/amount is invalid.

#### Core logic
- `parse_orders` opens each CSV as UTF-8 `csv.DictReader`, checks `REQUIRED_COLUMNS`, and yields `Order` only when `currency` matches.
- `Order` stores `order_date` (`date.fromisoformat`), stripped `region`, and `amount` as `Decimal`.
- `revenue_by_month_and_region` sums amounts in a `defaultdict(Decimal)` keyed by `("%Y-%m", region)`, then returns a dict sorted by those keys.
- `main` concatenates orders from all files, skips groups below `--min-revenue`, and prints remaining totals.

#### Public API
- `Order(order_date, region, amount) -> Order` — frozen order row.
- `parse_orders(path, currency) -> Iterator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — totals by month and region.
- `main(argv=None) -> int` — parses CLI and prints the report.
- `REQUIRED_COLUMNS` — required CSV column names.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` if CSV fieldnames omit any of `REQUIRED_COLUMNS`, or if `order_date`/`amount` fail (`ValueError`, `InvalidOperation`), including path and line number.
- Process status `1` when the filtered order list is empty.
