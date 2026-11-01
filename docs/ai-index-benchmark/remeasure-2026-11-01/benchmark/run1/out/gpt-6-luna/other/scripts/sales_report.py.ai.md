### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:23:02Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates monthly revenue by region from order CSV exports, filtered by currency and minimum revenue.

#### Purpose
- Reads one or more order CSV files and reports qualifying revenue totals by month and region.
- Skips orders in currencies other than the selected currency.

#### Type
- Python module; defines frozen `Order` dataclass.

#### Input
- `parse_orders(path, currency)`: CSV file with `order_date`, `region`, `amount`, and `currency` columns.
- `main(argv=None)`: command-line arguments:
  - Positional `csv_files`: one or more CSV paths.
  - `--currency`: currency filter.
  - `--min-revenue`: minimum revenue threshold.

#### Output
- Prints tab-separated month, region, and revenue with currency to stdout.
- Prints `no orders found` to stderr and returns `1` when no matching orders exist; otherwise returns `0`.

#### Core logic
- `Order` stores an order date, trimmed region, and decimal amount.
- `parse_orders` validates required CSV headers, skips rows with a different currency, and yields parsed orders.
- `revenue_by_month_and_region` sums amounts by `YYYY-MM` and region, returning sorted totals.
- `main` combines orders from all files, filters grouped totals by minimum revenue, and prints qualifying rows.

#### Public API
- `Order(order_date, region, amount) -> Order` — stores parsed order data.
- `parse_orders(path, currency) -> generator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — aggregates monthly regional revenue.
- `main(argv=None) -> int` — parses arguments and prints the report.
- `__main__` entry point — exits with `main()` status.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`
- `--currency`: default `"EUR"`.
- `--min-revenue`: default `Decimal("0")`.
- `csv_files`: positional argument; requires at least one file.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Calls: `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `defaultdict`, `sorted`, `ArgumentParser.add_argument`, `parse_args`, `print`, `sys.exit`.

#### Exceptions / Errors
- Raises `ValueError` naming the file and missing columns when required CSV headers are absent.
- Raises chained `ValueError` with file path and line number for invalid dates or amounts.
- File-opening and CSV-reading errors are not handled.
