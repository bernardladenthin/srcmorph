### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:38:51Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates order CSV exports into monthly revenue totals by region for a selected currency.

#### Purpose
- Parses orders from one or more CSV files and reports revenue by month and region.
- Filters output by currency and minimum revenue.

#### Type
- Python module; defines frozen `Order` dataclass.

#### Input
- `parse_orders(path, currency)`: CSV file with `order_date`, `region`, `amount`, and `currency` columns.
- `main(argv=None)`: command-line arguments; accepts one or more CSV file paths, `--currency`, and `--min-revenue`.

#### Output
- Prints tab-separated month, region, and revenue with currency to standard output.
- Prints `no orders found` to standard error and returns exit status `1` when no matching orders exist; otherwise returns `0`.

#### Core logic
- `parse_orders` validates required CSV headers, skips rows in other currencies, and yields `Order` values with ISO-parsed dates, stripped regions, and decimal amounts.
- `revenue_by_month_and_region` sums order amounts by `YYYY-MM` and region, returning sorted totals.
- `main` combines orders from all input files, filters totals below the minimum, and prints remaining results with two decimal places.

#### Public API
- `Order(order_date, region, amount) -> Order` — stores a parsed order.
- `parse_orders(path, currency) -> iterator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — calculates sorted revenue totals.
- `main(argv=None) -> int` — runs the report CLI.
- Entry point: `sys.exit(main())` when executed as a script.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.
- `--currency`: defaults to `"EUR"`.
- `--min-revenue`: parsed as `Decimal`; defaults to `Decimal("0")`.
- `csv_files`: positional argument; requires one or more paths.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Uses `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `enumerate`, `defaultdict`, `dict`, `sorted`, `print`, and `sys.exit`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` for missing required columns, including the file path and missing column names.
- Invalid date or amount raises `ValueError` with file path and CSV line number; chains the underlying `ValueError` or `InvalidOperation`.
- File-opening and CSV-reading errors are not handled in the module.
