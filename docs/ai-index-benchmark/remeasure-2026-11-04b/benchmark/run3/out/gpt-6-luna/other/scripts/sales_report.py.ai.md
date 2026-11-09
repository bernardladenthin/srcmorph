### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:07:31Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Aggregates order CSV exports into monthly revenue totals by region for a selected currency, with an optional minimum-revenue filter.

#### Purpose
- Read one or more order CSV files and report qualifying monthly revenue by region.
- Skip orders in other currencies; reject files missing required columns or containing invalid matching-currency orders.

#### Type
- Python command-line script.
- Frozen dataclass `Order`.

#### Input
- Positional `csv_files`: one or more CSV paths.
- `--currency`: currency to include; default `EUR`.
- `--min-revenue`: minimum revenue to print; parsed as `Decimal`, default `Decimal("0")`.
- CSV columns: `order_date`, `region`, `amount`, `currency`; dates use ISO format.

#### Output
- Prints tab-separated month, region, and revenue formatted to two decimal places with the selected currency.
- Prints `no orders found` to stderr and returns status `1` when no matching orders are found.
- Returns status `0` after reporting results, including when the minimum-revenue filter excludes all totals.

#### Core logic
- `parse_orders` checks each CSV header for all `REQUIRED_COLUMNS`.
- Reads rows starting at line 2; skips rows whose currency differs from the requested currency.
- Converts matching rows to `Order`: ISO date, stripped region, and decimal amount.
- `revenue_by_month_and_region` sums amounts by `(YYYY-MM, region)` and returns totals in sorted key order.
- `main` collects orders from every input file, handles the empty-order case, filters totals by minimum revenue, and prints results.

#### Public API
- `Order(order_date, region, amount) -> Order` — immutable order data.
- `parse_orders(path, currency) -> iterator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — totals revenue by month and region.
- `main(argv=None) -> int` — parses arguments and runs report.
- Entry point: `main()` via `sys.exit`.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`
- CLI `--currency`: default `EUR`.
- CLI `--min-revenue`: default `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Uses `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `enumerate`, `print`, and `sys.exit`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` with the file path and missing column names when required headers are absent.
- Invalid matching-row dates or amounts raise a chained `ValueError` containing the file path and line number.
- File access and CSV parsing errors are not caught.
