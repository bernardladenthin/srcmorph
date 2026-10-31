### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:47:04Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates CSV order amounts into monthly revenue totals by region for a selected currency.

#### Purpose
- Reads one or more order CSV exports and reports revenue grouped by month and region.
- Filters orders by currency and output rows by minimum revenue.

#### Type
- Python module; defines frozen `Order` dataclass.

#### Input
- CSV files with `order_date`, `region`, `amount`, and `currency` columns.
- `main(argv=None)` command-line arguments: positional `csv_files`, optional `--currency`, optional `--min-revenue`.

#### Output
- Prints qualifying rows as `month<TAB>region<TAB>revenue currency`; prints `no orders found` to stderr when no matching orders exist.
- `main` returns status `0` when orders are found, otherwise `1`.

#### Core logic
- `parse_orders` validates required CSV headers, skips rows whose currency does not match, and yields `Order` records with ISO-parsed dates, stripped regions, and decimal amounts.
- `revenue_by_month_and_region` sums amounts by `YYYY-MM` and region, returning a sorted dictionary.
- `main` combines orders from all files, filters totals using the minimum revenue, and formats amounts to two decimal places.

#### Public API
- `Order(order_date, region, amount)` — immutable order date, region, and amount.
- `parse_orders(path, currency)` — yields matching-currency orders from a CSV file.
- `revenue_by_month_and_region(orders)` — calculates sorted monthly regional totals.
- `main(argv=None)` — parses arguments and prints the report.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`
- `--currency`: default `"EUR"`.
- `--min-revenue`: `Decimal`, default `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Calls `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `argparse.ArgumentParser`, `print`, and `sys.exit`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` when required columns are missing or a matching row has an invalid date or amount; row errors include file path and line number.
- File-opening and argument-parsing errors are not handled in this module.
