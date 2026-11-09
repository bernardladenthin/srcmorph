### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:12:59Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Aggregates CSV order amounts into monthly revenue totals by region for a selected currency.

#### Purpose
- Reads one or more order CSV exports and reports monthly revenue per region.
- Skips orders in other currencies and filters totals below a minimum revenue.

#### Type
- Python script; defines frozen `Order` dataclass.

#### Input
- Positional `csv_files`: one or more CSV file paths; expects `order_date`, `region`, `amount`, and `currency` columns.
- `--currency`: currency to include.
- `--min-revenue`: minimum monthly regional revenue to print.
- CSV rows: ISO-formatted order date, region, decimal amount, and currency.

#### Output
- Prints tab-separated month, region, and revenue formatted to two decimal places with the selected currency.
- Writes `no orders found` to stderr and returns status `1` when no matching orders exist.
- Returns status `0` after reporting results.

#### Core logic
- `parse_orders` validates required CSV headers, filters rows by currency, and yields `Order` instances with parsed dates, stripped regions, and decimal amounts.
- `revenue_by_month_and_region` sums amounts by `(YYYY-MM, region)` and returns totals sorted by key.
- `main` combines orders from all input files, handles the empty result, and prints totals meeting the minimum revenue threshold.
- Script entry point exits with `main()`'s status.

#### Public API
- `Order(order_date, region, amount)` -> `Order` — represents a parsed order.
- `parse_orders(path, currency)` -> iterator of `Order` — yields matching-currency orders.
- `revenue_by_month_and_region(orders)` -> `dict` — aggregates sorted monthly regional totals.
- `main(argv=None)` -> `int` — parses arguments and runs report.
- Internal: `__main__` entry point invokes `sys.exit(main())`.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.
- `csv_files`: positional CLI argument; one or more paths.
- `--currency`: defaults to `"EUR"`.
- `--min-revenue`: parsed as `Decimal`; defaults to `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Uses `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `date.strftime`, `argparse.ArgumentParser`, and `sys.exit`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` when required columns are missing, identifying the file and column names.
- Invalid dates or amounts raise `ValueError` with file and CSV line number; original `ValueError` or `InvalidOperation` is chained.
- File-opening and CSV parsing errors are not caught.
