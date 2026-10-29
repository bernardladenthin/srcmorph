### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:49:43Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates monthly revenue by region from order CSV exports, filtering by currency and minimum revenue threshold.

#### Purpose
- Reads one or more order CSV files, filters rows by a target currency, and computes total revenue per calendar month and region.
- Prints a tab-separated report of month, region, and revenue for entries meeting a minimum revenue threshold.

#### Type
- Python module (script) containing one frozen dataclass (`Order`) and three functions (`parse_orders`, `revenue_by_month_and_region`, `main`).

#### Input
- `csv_files`: one or more file paths to order CSV exports (positional, `nargs="+"`).
- `--currency`: ISO currency code to filter on (default `"EUR"`).
- `--min-revenue`: `Decimal` threshold; only rows with revenue ≥ this are printed (default `0`).
- CSV columns required: `order_date` (ISO 8601), `region`, `amount`, `currency`.

#### Output
- `main` returns `0` on success, `1` if no orders matched.
- Prints tab-separated lines: `YYYY-MM<TAB>region<TAB>amount.00 CURRENCY` to stdout.
- Prints `"no orders found"` to stderr when no orders match.

#### Core logic
- `parse_orders` opens a CSV, validates that all `REQUIRED_COLUMNS` are present in the header, then iterates rows: skips rows whose `currency` differs from the target, parses `order_date` via `date.fromisoformat`, strips `region`, converts `amount` to `Decimal`, and yields an `Order` instance.
- `revenue_by_month_and_region` accumulates `Decimal` sums in a `defaultdict` keyed by `(YYYY-MM, region)` tuple, then returns a sorted `dict`.
- `main` collects all `Order` objects across all input files, calls the aggregation, filters by `--min-revenue`, and prints formatted results.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` — yields filtered orders from one CSV.
- `revenue_by_month_and_region(orders) -> dict[tuple[str, str], Decimal]` — aggregates revenue by month/region.
- `main(argv=None) -> int` — CLI entry point; returns exit code.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`

#### Exceptions / Errors
- `ValueError` raised if required CSV columns are missing from the header.
- `ValueError` raised (with file path and line number) if `order_date` is not valid ISO 8601 or `amount` is not a valid `Decimal`.
- `InvalidOperation` caught internally and re-raised as `ValueError`.
