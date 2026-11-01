### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:16:27Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates order CSV data into monthly revenue totals by region for a selected currency.

#### Purpose
- Reads one or more order CSV exports and reports revenue grouped by month and region.
- Filters report rows by a minimum revenue threshold.

#### Type
- Python script; defines the frozen `Order` dataclass.

#### Input
- Positional CLI argument `csv_files`: one or more CSV file paths.
- CLI option `--currency`: currency to include; defaults to `EUR`.
- CLI option `--min-revenue`: minimum revenue to print; defaults to `Decimal("0")`.
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints tab-separated month, region, and revenue with two decimal places and the selected currency.
- Prints `no orders found` to standard error and returns exit status `1` when no matching orders exist.
- Returns exit status `0` after processing matching orders.

#### Core logic
- `parse_orders` validates required CSV headers, skips rows in other currencies, and yields `Order` values with ISO-parsed dates, stripped regions, and decimal amounts.
- `revenue_by_month_and_region` sums amounts by `YYYY-MM` and region, returning totals sorted by key.
- `main` combines orders from all CSV files, reports the no-orders case, and prints totals meeting `--min-revenue`.

#### Public API
- `Order(order_date, region, amount) -> Order` — stores an order date, region, and amount.
- `parse_orders(path, currency) -> iterator[Order]` — reads matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — aggregates sorted monthly regional totals.
- `main(argv=None) -> int` — parses CLI arguments and prints the report.
- `__main__` entry point — exits with `main()` status.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`
- `--currency`: default `EUR`.
- `--min-revenue`: default `Decimal("0")`.

#### Dependencies
- Standard library imports: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- CSV and file APIs: `open`, `csv.DictReader`.
- Parsing and aggregation APIs: `date.fromisoformat`, `Decimal`, `defaultdict`, `dict`, `sorted`.
- CLI and output APIs: `argparse.ArgumentParser`, `ArgumentParser.add_argument`, `ArgumentParser.parse_args`, `print`, `sys.exit`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` if required CSV columns are missing.
- Invalid ISO dates or decimal amounts raise a path-and-line-number `ValueError`, chained from `ValueError` or `InvalidOperation`.
- File access and CSV parsing errors are not caught.
