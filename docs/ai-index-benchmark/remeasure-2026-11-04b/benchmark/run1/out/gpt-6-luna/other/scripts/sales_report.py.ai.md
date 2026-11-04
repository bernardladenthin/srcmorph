### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:49:54Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Aggregates order CSV exports into monthly revenue totals by region for a selected currency, with an optional minimum-revenue filter.

#### Purpose
- Reads one or more order CSV files and reports monthly revenue per region.
- Skips orders in other currencies and filters totals below the configured minimum.

#### Type
- Python command-line script.
- `Order`: frozen dataclass containing `order_date: date`, `region: str`, and `amount: Decimal`.

#### Input
- Positional `csv_files`: one or more CSV paths.
- `--currency`: currency to include; defaults to `EUR`.
- `--min-revenue`: minimum total to print; parsed as `Decimal`, defaults to `Decimal("0")`.
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints qualifying totals to stdout as `YYYY-MM<TAB>region<TAB>amount currency`, with amounts formatted to two decimal places.
- Prints `no orders found` to stderr and returns status `1` if no matching orders are read.
- Returns status `0` after reporting results.

#### Core logic
- `parse_orders` validates required CSV headers, skips rows whose currency differs from the selected currency, and yields `Order` records.
- Parses order dates with ISO format, trims region whitespace, and parses amounts as `Decimal`.
- `revenue_by_month_and_region` sums amounts by `(YYYY-MM, region)` and returns sorted totals.
- `main` combines orders from all input files, filters grouped totals by minimum revenue, and prints qualifying rows.

#### Public API
- `Order(order_date, region, amount) -> Order` — immutable order data.
- `parse_orders(path, currency) -> iterator[Order]` — yield matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — aggregate sorted monthly regional revenue.
- `main(argv=None) -> int` — parse CLI arguments and run report.
- `__main__` entry point — exits with `main()` status.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.
- `--currency`: default `EUR`.
- `--min-revenue`: default `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Referenced functions: `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `enumerate`, `defaultdict`, `sys.exit`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` listing missing required columns.
- Invalid order date or amount raises a path-and-line-number `ValueError`, chained from `ValueError` or `InvalidOperation`.
- File-opening and CSV parsing errors are not handled in this file.
