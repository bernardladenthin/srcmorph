### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:13:10Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates order CSV exports into monthly revenue totals by region for a selected currency, with an optional minimum-revenue filter.

#### Purpose
- Reads one or more order CSV files and reports matching-currency revenue grouped by month and region.
- Skips orders in other currencies and filters totals below the requested minimum.

#### Type
- Python module; defines frozen `Order` dataclass with fields `order_date: date`, `region: str`, and `amount: Decimal`.

#### Input
- `main(argv=None)`: command-line arguments.
- Positional `csv_files`: one or more CSV paths; each file must contain `order_date`, `region`, `amount`, and `currency`.
- `--currency`: currency to include; defaults to `EUR`.
- `--min-revenue`: minimum displayed total; parsed as `Decimal`, defaults to `Decimal("0")`.
- CSV data is read as UTF-8 with newline handling.

#### Output
- Prints qualifying rows to stdout as `YYYY-MM<TAB>region<TAB>amount currency`, with amounts formatted to two decimal places.
- Prints `no orders found` to stderr and returns `1` if no matching orders were read.
- Returns `0` after reporting results or when matching orders exist but all totals are filtered out.

#### Core logic
- `parse_orders` verifies the required CSV headers, skips rows whose currency differs from the requested currency, and yields `Order` values.
- Parses dates with ISO format, strips whitespace from regions, and parses amounts as `Decimal`.
- `revenue_by_month_and_region` sums order amounts by `(YYYY-MM, region)` and returns totals sorted by key.
- `main` combines orders from all input files, reports an empty-result error, then prints totals meeting `--min-revenue`.
- Module entry point exits with `main()`’s status.

#### Public API
- `Order(order_date, region, amount) -> Order` — immutable order record.
- `parse_orders(path, currency) -> iterator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — groups and sorts revenue totals.
- `main(argv=None) -> int` — parses arguments and prints the report.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`
- `--currency`: default `EUR`.
- `--min-revenue`: default `Decimal("0")`.
- `main(argv=None)`: `argv` defaults to `None`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Calls: `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `enumerate`, `defaultdict`, `sorted`, `print`, `sys.exit`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` listing missing required columns.
- Invalid matching-row dates or amounts raise `ValueError` annotated with CSV path and line number; wraps `ValueError` or `InvalidOperation`.
- File-opening and CSV-reading errors are not handled here.
