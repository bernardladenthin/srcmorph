### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:44:30Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates order CSV data into monthly revenue totals by region and currency.

#### Purpose
- Reads one or more order CSV exports, filters by currency, and reports revenue by month and region.
- Omits totals below a configurable minimum revenue.

#### Type
- Python module; defines immutable `Order` dataclass.

#### Input
- `parse_orders(path, currency)`: CSV file path and target currency.
- `revenue_by_month_and_region(orders)`: iterable of `Order` records.
- `main(argv=None)`: command-line arguments.
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- `parse_orders` yields `Order` records for matching-currency rows.
- `revenue_by_month_and_region` returns a sorted dictionary keyed by `(YYYY-MM, region)`.
- CLI prints tab-separated month, region, and revenue formatted to two decimal places; returns status `1` if no orders match, otherwise `0`.

#### Core logic
- `Order` stores an order date, stripped region, and decimal amount.
- Validates that each CSV contains all `REQUIRED_COLUMNS`.
- Skips rows whose currency differs from the requested currency.
- Parses dates with ISO format and amounts as `Decimal`; reports malformed rows with file and line number.
- Sums amounts by month and region, then sorts the totals.
- CLI combines orders from all input files, reports matching totals at or above the minimum, and prints `no orders found` to standard error when none match.

#### Public API
- `Order(order_date, region, amount) -> Order` — stores parsed order data.
- `parse_orders(path, currency) -> generator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — aggregates monthly regional revenue.
- `main(argv=None) -> int` — runs the command-line report.
- Internal/entry point: `if __name__ == "__main__"` calls `sys.exit(main())`.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.
- `--currency`: defaults to `"EUR"`.
- `--min-revenue`: parsed as `Decimal`; defaults to `Decimal("0")`.
- `csv_files`: one or more positional input paths.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Uses `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `enumerate`, `strftime`, `sorted`, `print`, and `sys.exit`.

#### Exceptions / Errors
- Raises `ValueError` when required CSV columns are missing.
- Raises `ValueError` with file and line context for invalid dates or amounts; chains the original `ValueError` or `InvalidOperation`.
- Returns exit status `1` and writes `no orders found` to standard error when no orders match the requested currency.
