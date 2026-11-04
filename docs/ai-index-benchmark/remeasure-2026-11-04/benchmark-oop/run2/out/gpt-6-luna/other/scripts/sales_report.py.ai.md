### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:38:13Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Aggregates sales orders from CSV exports into monthly revenue totals by region and currency, with an optional minimum-revenue filter.

#### Purpose
- Reads one or more order CSV files and reports monthly revenue grouped by region.
- Filters orders by currency and report rows by minimum revenue.

#### Type
- Python module; defines frozen `Order` dataclass.

#### Input
- Positional `csv_files`: one or more CSV paths; expects `order_date`, `region`, `amount`, and `currency` columns.
- `--currency`: currency to include; defaults to `EUR`.
- `--min-revenue`: minimum reported total; parsed as `Decimal`, defaults to `Decimal("0")`.
- CSV files are read as UTF-8 with newline handling.

#### Output
- Prints tab-separated month, region, and revenue with two decimal places and the selected currency.
- Prints `no orders found` to stderr and returns status `1` when no matching orders exist.
- Returns status `0` after reporting results.

#### Core logic
- `parse_orders` validates required CSV columns, skips rows in other currencies, and yields `Order` values with ISO-parsed dates, stripped regions, and decimal amounts.
- `revenue_by_month_and_region` sums order amounts by `YYYY-MM` and region, then returns totals sorted by key.
- `main` collects matching orders across input files, handles the empty result, and prints totals meeting the minimum-revenue threshold.
- Script entry point exits with `main()`’s status.

#### Public API
- `Order(order_date, region, amount) -> Order` — immutable sales order record.
- `parse_orders(path, currency) -> iterator[Order]` — yield matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — aggregate and sort monthly regional totals.
- `main(argv=None) -> int` — parse arguments and run report.
- CLI options: `--currency` (passed to `parse_orders`); `--min-revenue` (filters aggregated totals); positional `csv_files` (input paths).

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.
- `--currency` default: `EUR`.
- `--min-revenue` default: `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Uses `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `enumerate`, `print`, and `sys.exit`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` listing missing required columns.
- Invalid date or amount raises a path-and-line-number `ValueError`, chained from `ValueError` or `InvalidOperation`.
- File opening and CSV parsing errors are not handled locally.
