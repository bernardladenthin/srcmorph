### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T09:59:38Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates monthly sales revenue by region from order CSV exports, filtering by currency and minimum revenue threshold.

#### Purpose
- Reads one or more order CSV files, filters rows by a target currency, and computes total revenue per calendar month and region.
- Prints a tab-separated report of month, region, and revenue for entries meeting a configurable minimum-revenue threshold.

#### Type
- Python script (module-level functions + one frozen dataclass); no class hierarchy.

#### Input
- `csv_files`: one or more paths to CSV files with columns `order_date`, `region`, `amount`, `currency` (defined in `REQUIRED_COLUMNS`).
- `--currency`: target currency filter (default `"EUR"`); rows with a different `currency` value are skipped.
- `--min-revenue`: `Decimal` threshold (default `Decimal("0")`); only month/region totals ≥ this value are printed.

#### Output
- Tab-separated lines to stdout: `YYYY-MM<TAB>region<TAB>revenue.00 CURRENCY`.
- Returns `0` on success, `1` if no orders are found (message to stderr).

#### Core logic
- `parse_orders` opens each CSV via `csv.DictReader`, validates that all `REQUIRED_COLUMNS` are present, then iterates rows (starting at line 2).
- Skips rows where `row["currency"] != currency`; parses `order_date` with `date.fromisoformat`, strips `region`, converts `amount` to `Decimal`.
- Yields immutable `Order(order_date, region, amount)` dataclass instances.
- `revenue_by_month_and_region` accumulates `Decimal` sums in a `defaultdict` keyed by `(YYYY-MM, region)` tuple, then returns a sorted `dict`.
- `main` collects all orders across all CSV files, exits early with code 1 if the list is empty, otherwise prints each `(month, region)` pair whose revenue ≥ `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` — yields filtered orders from one CSV.
- `revenue_by_month_and_region(orders) -> dict[tuple[str,str], Decimal]` — aggregates revenue by month and region.
- `main(argv=None) -> int` — CLI entry point; parses args, runs pipeline, prints report.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`

#### Exceptions / Errors
- `ValueError` raised if required CSV columns are missing (includes file path and missing column names).
- `ValueError` raised per-row on malformed `order_date` or `amount` (includes file path, line number, and original error).
- `InvalidOperation` caught and re-wrapped as `ValueError` for non-numeric `amount` fields.
