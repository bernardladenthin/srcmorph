### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:53:34Z
- G: 
- A: 0.0.0
- X: file
---
> Generates monthly sales revenue per region from CSV order exports, filtering by currency and a minimum revenue threshold.

#### Purpose
- Produces a tab-separated revenue report grouped by year-month and region.
- Filters CSV rows by currency and excludes aggregates below `--min-revenue`.

#### Type
- Python CLI script / module.
- Defines frozen dataclass `Order`; no inheritance or interfaces.

#### Input
- Positional `csv_files`: one or more paths to UTF-8 CSV exports.
- Optional `--currency`: currency code used to filter rows (default `EUR`).
- Optional `--min-revenue`: `Decimal` threshold for printing aggregates (default `0`).
- Required CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Writes `month\tregion\trevenue currency` rows to stdout for qualifying aggregates.
- Returns exit code `0` on success, `1` when no orders are found.
- Emits `no orders found` to stderr when input yields zero orders.

#### Core logic
- `parse_orders` opens each CSV with `csv.DictReader`, validates `REQUIRED_COLUMNS`, skips non-matching currency rows, parses `order_date` with `date.fromisoformat`, strips `region`, converts `amount` to `Decimal`, and yields `Order`.
- `revenue_by_month_and_region` accumulates `amount` into a `defaultdict(Decimal)` keyed by `(order_date.strftime("%Y-%m"), region)` and returns a sorted dictionary.
- `main` parses arguments with `argparse`, flattens orders from all `csv_files`, aggregates revenue, filters by `args.min_revenue`, and prints formatted results.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` yields validated orders of one currency from a CSV file.
- `revenue_by_month_and_region(orders) -> dict[tuple[str, str], Decimal]` returns sorted monthly and regional revenue totals.
- `main(argv=None) -> int` parses CLI arguments, prints the report, and returns the process exit code.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `parse_orders` raises `ValueError` if required CSV columns are missing.
- `parse_orders` raises `ValueError` with file and line number on invalid `order_date` or `amount`, chaining the original `ValueError` or `InvalidOperation`.
- `main` returns exit code `1` when no orders are found across all input files.
