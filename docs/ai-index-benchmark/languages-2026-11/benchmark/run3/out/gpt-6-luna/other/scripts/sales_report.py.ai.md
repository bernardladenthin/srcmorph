### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:00:36Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates order CSV data into monthly revenue totals by region for a selected currency, with an optional minimum-revenue filter.

#### Purpose
- Parse one or more order CSV exports and report qualifying monthly revenue by region.
- Skip orders not matching the selected currency.

#### Type
- Python module; defines frozen `Order` dataclass.

#### Input
- `main(argv=None)`: command-line arguments.
- Positional `csv_files`: one or more CSV paths; each CSV must contain `order_date`, `region`, `amount`, and `currency`.
- `--currency`: currency to include; defaults to `EUR`.
- `--min-revenue`: minimum monthly regional revenue to print; defaults to `Decimal("0")`.
- CSV values: ISO-formatted `order_date`, `region`, decimal `amount`, and `currency`.

#### Output
- Prints tab-separated month, region, and revenue formatted to two decimal places, followed by the selected currency.
- Prints `no orders found` to stderr and returns status `1` when no matching orders are found.
- Returns status `0` otherwise.

#### Core logic
- `parse_orders` validates required CSV headers, skips rows in other currencies, strips region whitespace, and yields `Order` values.
- `revenue_by_month_and_region` sums amounts by `(YYYY-MM, region)` and returns totals sorted by key.
- `main` combines matching orders across files, reports an empty result or prints totals meeting the minimum-revenue threshold.

#### Public API
- `Order(order_date, region, amount) -> Order` — stores a parsed order’s date, region, and amount.
- `parse_orders(path, currency) -> iterator[Order]` — yields matching-currency orders from a CSV.
- `revenue_by_month_and_region(orders) -> dict` — aggregates revenue by month and region.
- `main(argv=None) -> int` — runs the CLI report and returns its status.
- `__main__` entry point — exits with `main()` status.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`
- `--currency`: default `"EUR"`.
- `--min-revenue`: default `Decimal("0")`.
- `csv_files`: required positional argument with one or more values.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Calls: `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `enumerate`, `defaultdict`, `dict`, `sorted`, `ArgumentParser`, `print`, `sys.exit`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` listing missing CSV columns.
- Invalid matching-row dates or amounts raise a path-and-line-number `ValueError`, chained from `ValueError` or `InvalidOperation`.
- File-opening and CSV parsing errors are not handled in this module.
