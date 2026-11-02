### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:50:56Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates order CSV data into monthly revenue totals by region for a selected currency.

#### Purpose
- Reads one or more order CSV exports, filters orders by currency, and reports revenue grouped by month and region.
- Omits totals below a configurable minimum revenue.

#### Type
- Python module; defines frozen `Order` dataclass.

#### Input
- `parse_orders(path, currency)`: CSV file path and currency code; requires `order_date`, `region`, `amount`, and `currency` columns.
- `revenue_by_month_and_region(orders)`: iterable of `Order` instances.
- `main(argv=None)`: command-line arguments:
  - `csv_files`: one or more CSV file paths.
  - `--currency`: currency filter.
  - `--min-revenue`: minimum included revenue.
- CSV files are opened as UTF-8 with newline handling.

#### Output
- `Order(order_date, region, amount)` records contain a `date`, stripped region string, and `Decimal` amount.
- Revenue aggregation returns a sorted dictionary keyed by `(YYYY-MM, region)`.
- CLI prints tab-separated month, region, and two-decimal revenue with currency; prints `no orders found` to stderr if no matching orders.
- `main` returns status `0` on success or `1` when no orders are found.

#### Core logic
- `parse_orders` validates required CSV headers, skips rows whose currency differs from the requested currency, and yields parsed `Order` records.
- Parses dates with `date.fromisoformat` and amounts with `Decimal`; strips whitespace from region names.
- `revenue_by_month_and_region` sums amounts by formatted month and region, then sorts the resulting keys.
- `main` combines matching orders from all input files, handles the empty result, filters aggregated totals by minimum revenue, and prints qualifying rows.

#### Public API
- `Order(order_date, region, amount) -> Order` — immutable order data.
- `parse_orders(path, currency) -> generator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — calculates sorted revenue totals.
- `main(argv=None) -> int` — runs the report CLI.
- Internal / entry-point unit: `if __name__ == "__main__"` calls `sys.exit(main())`.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.
- `csv_files`: required positional CLI argument; one or more files.
- `--currency`: defaults to `"EUR"`.
- `--min-revenue`: parsed as `Decimal`; defaults to `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Calls: `open`, `csv.DictReader`, `date.fromisoformat`, `Decimal`, `enumerate`, `defaultdict`, `dict`, `sorted`, `ArgumentParser`, `add_argument`, `parse_args`, `print`, `sys.exit`.

#### Exceptions / Errors
- `ValueError` if required CSV columns are missing; message identifies the file and missing columns.
- `ValueError` for invalid date or amount; message identifies the file and CSV line number, with the parsing exception chained.
- File-opening and CSV-reading errors are not handled locally.
- Returns status `1` and writes to stderr when no matching orders are found.
