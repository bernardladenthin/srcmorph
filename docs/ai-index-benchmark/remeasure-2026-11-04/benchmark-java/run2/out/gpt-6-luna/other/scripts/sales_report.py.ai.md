### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:31:01Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Aggregates CSV order amounts into monthly revenue totals by region for a selected currency, with an optional minimum-revenue filter.

#### Purpose
- Reads one or more order CSV exports and reports revenue by month and region.
- Skips orders in other currencies and filters displayed totals by minimum revenue.

#### Type
- Python script; defines frozen `Order` dataclass.

#### Input
- `parse_orders(path, currency)`: CSV path and currency code; requires `order_date`, `region`, `amount`, and `currency` columns.
- `revenue_by_month_and_region(orders)`: iterable of `Order` instances.
- `main(argv=None)`: command-line arguments.
- CLI: one or more positional `csv_files`; `--currency` defaults to `EUR`; `--min-revenue` is a `Decimal` defaulting to `Decimal("0")`.
- CSV rows supply ISO-formatted `order_date`, `region`, `amount`, and `currency`.

#### Output
- Prints tab-separated month, region, and revenue formatted to two decimal places with the selected currency.
- Prints `no orders found` to stderr and returns `1` if no matching orders exist; otherwise returns `0`.
- `parse_orders` yields `Order` records; `revenue_by_month_and_region` returns a sorted dictionary keyed by `(month, region)`.

#### Core logic
- `parse_orders` opens each CSV as UTF-8 with newline handling, validates required headers, and skips rows whose currency does not match.
- Converts matching rows into `Order` values: parses dates with `date.fromisoformat`, strips region whitespace, and parses amounts as `Decimal`.
- Aggregates amounts by `YYYY-MM` and region, then returns totals ordered by key.
- `main` combines orders across input files, reports an empty result to stderr, and prints totals meeting `--min-revenue`.
- Script entry point exits with `main()`’s status.

#### Public API
- `Order(order_date, region, amount)` → `Order`; immutable order date, region, and amount.
- `parse_orders(path, currency)` → generator of `Order`; yields matching-currency CSV orders.
- `revenue_by_month_and_region(orders)` → `dict`; aggregates and sorts revenue totals.
- `main(argv=None)` → `int`; parses CLI and prints report.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`
- `--currency`: default `EUR`.
- `--min-revenue`: default `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Calls: `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `enumerate`, `defaultdict`, `dict`, `sorted`, `ArgumentParser.add_argument`, `parse_args`, `print`, `sys.exit`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` naming the CSV path and missing required columns when headers are absent.
- Invalid matching-row date or amount raises `ValueError` with path and line number, chained from `ValueError` or `InvalidOperation`.
- File-opening and CSV-reading errors are not caught.
