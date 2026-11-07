### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:59:22Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Summarizes order CSV exports as monthly revenue totals by region for a selected currency.

#### Purpose
- Reads one or more order CSV files and aggregates matching-currency sales by month and region.
- Prints totals meeting a minimum revenue threshold.

#### Type
- Python command-line script; defines frozen `Order` dataclass.

#### Input
- Positional `csv_files`: one or more CSV paths; each must contain `order_date`, `region`, `amount`, and `currency`.
- `--currency`: currency to include; defaults to `EUR`.
- `--min-revenue`: minimum total to print; parsed as `Decimal`, defaults to `Decimal("0")`.
- CSV fields: ISO-formatted `order_date`, `region`, decimal `amount`, and `currency`.

#### Output
- Prints tab-separated month, region, and revenue formatted to two decimal places with the selected currency.
- Writes `no orders found` to stderr and returns status `1` when no matching orders exist; otherwise returns `0`.

#### Core logic
- `parse_orders` validates required CSV headers, skips rows in other currencies, and yields `Order` records with parsed dates, stripped regions, and decimal amounts.
- `revenue_by_month_and_region` sums amounts by `(YYYY-MM, region)` and returns totals sorted by key.
- `main` collects orders across input files, reports an empty result, or prints totals at or above the threshold.

#### Public API
- `Order(order_date, region, amount) -> Order` — immutable parsed order.
- `parse_orders(path, currency) -> iterator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — aggregates monthly regional revenue.
- `main(argv=None) -> int` — parses CLI arguments and runs report.
- Entry point: `sys.exit(main())`.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.
- `--currency`: default `EUR`.
- `--min-revenue`: default `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Uses `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `enumerate`, `print`, and `sys.exit`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` for missing required columns or invalid date/amount values; row errors include the file path and line number.
- File-opening and CSV-reading errors are not caught.
