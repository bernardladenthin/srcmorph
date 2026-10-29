### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:45:13Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates order revenue by month and region from CSV exports, filtered by currency and minimum revenue threshold.

#### Purpose
- Reads one or more order CSV files, filters rows by currency, and prints total revenue grouped by `YYYY-MM` and region.
- Supports a `--min-revenue` filter to suppress low-revenue rows.

#### Type
- Python script (module-level functions + `@dataclass`), CLI entry point via `argparse`.

#### Input
- CLI positional: one or more CSV file paths (`csv_files`).
- CLI option `--currency`: filter currency code (default `"EUR"`).
- CLI option `--min-revenue`: `Decimal` threshold (default `0`).
- CSV columns required: `order_date`, `region`, `amount`, `currency`.

#### Output
- Stdout: tab-separated lines `YYYY-MM <TAB> region <TAB> amount.xx CURRENCY`, one per (month, region) pair where revenue ≥ threshold.
- Stderr: `"no orders found"` if no matching orders.
- Exit code: `0` on success, `1` when no orders found.

#### Core logic
- `parse_orders` opens a CSV, validates required columns are present, iterates rows, skips non-matching currency, parses each row into a frozen `Order` dataclass (`date.fromisoformat`, `Decimal`), and yields orders.
- `revenue_by_month_and_region` accumulates `Decimal` sums in a `defaultdict` keyed by `(YYYY-MM, region)`, returns a sorted dict.
- `main` collects all orders from all files, short-circuits if empty, then prints rows passing the `min-revenue` filter.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` — yields filtered orders from one CSV.
- `revenue_by_month_and_region(orders) -> dict[tuple[str, str], Decimal]` — aggregates revenue by month/region.
- `main(argv=None) -> int` — CLI entry point.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`

#### Exceptions / Errors
- `ValueError` raised if required CSV columns are missing (includes file path and column names).
- `ValueError` raised on per-row parse failure (wraps `ValueError` or `InvalidOperation`, includes file path and line number).
