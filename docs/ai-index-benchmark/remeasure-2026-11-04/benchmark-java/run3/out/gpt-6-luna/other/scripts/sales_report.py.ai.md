### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:46:07Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Aggregates CSV order amounts into monthly revenue totals by region for a selected currency, with an optional minimum-revenue filter.

#### Purpose
- Read one or more order CSV exports and report qualifying monthly revenue by region.
- Skip orders in currencies other than the selected currency.

#### Type
- Python module; defines frozen dataclass `Order`.

#### Input
- `parse_orders(path, currency)`: CSV file with `order_date`, `region`, `amount`, and `currency` columns.
- `main(argv=None)`: command-line arguments; accepts one or more CSV file paths, `--currency`, and `--min-revenue`.

#### Output
- Prints tab-separated month, region, and revenue with currency to standard output.
- Prints `no orders found` to standard error and returns `1` if no matching orders; otherwise returns `0`.

#### Core logic
- `parse_orders` validates required CSV headers, skips rows with a different currency, parses ISO dates and decimal amounts, and strips whitespace from regions.
- `revenue_by_month_and_region` sums amounts by `YYYY-MM` and region, then returns totals in sorted-key order.
- `main` combines orders from all input files, filters aggregated totals against the minimum revenue, and prints revenue to two decimal places.

#### Public API
- `Order(order_date, region, amount) -> Order` — stores a parsed order.
- `parse_orders(path, currency) -> iterator[Order]` — yields matching-currency orders.
- `revenue_by_month_and_region(orders) -> dict` — aggregates revenue by month and region.
- `main(argv=None) -> int` — runs report command-line processing.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`
- `--currency`: default `EUR`.
- `--min-revenue`: parsed as `Decimal`; default `Decimal("0")`.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Uses `csv.DictReader`, `date.fromisoformat`, `Decimal`, `defaultdict`, `argparse.ArgumentParser`, and `sys.exit`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` for missing required columns or invalid date/amount values; row errors include the file path and line number.
- File opening and CSV parsing errors are not handled locally.
