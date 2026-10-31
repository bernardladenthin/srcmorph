### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:04:20Z
- G: 
- A: 0.0.0
- X: file
---
> Generates monthly revenue totals per region from order CSV exports, filtering by currency and minimum revenue threshold.

#### Purpose
- Reads one or more order CSV files and aggregates revenue by month and region.
- Filters orders by currency and excludes revenue below a configurable minimum threshold.

#### Type
Python script / module with dataclass and CLI entry point.

#### Input
- `csv_files`: one or more paths to CSV exports containing `order_date`, `region`, `amount`, `currency` columns.
- `--currency`: currency code to include (default `EUR`).
- `--min-revenue`: Decimal minimum revenue threshold (default `0`).
- CSV rows parsed via `csv.DictReader` with UTF-8 encoding.

#### Output
- Prints lines of `month\tregion\trevenue currency` to stdout for qualifying month/region pairs.
- Returns exit code `0` on success, `1` when no orders are found.
- Writes `no orders found` to stderr when input yields no matching orders.

#### Core logic
- `parse_orders` opens each CSV, validates required columns, skips rows whose currency does not match, and yields `Order` objects with parsed `date`, stripped `region`, and `Decimal` amount.
- `revenue_by_month_and_region` accumulates `Decimal` totals keyed by `("%Y-%m", region)` using `defaultdict`, then returns a sorted dictionary.
- `main` parses CLI arguments, flattens orders from all files, aggregates revenue, filters by `--min-revenue`, and prints formatted results.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` yields matching orders from a CSV file.
- `revenue_by_month_and_region(orders) -> dict[tuple[str, str], Decimal]` returns sorted monthly/regional revenue totals.
- `main(argv=None) -> int` runs the CLI and returns an exit code.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` raised when a CSV is missing required columns or a row contains an invalid date or amount.
