### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:09:24Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates monthly revenue by region from order CSV exports, filtering by currency and minimum revenue threshold.

#### Purpose
- Reads one or more order CSV files, filters rows by a target currency, and computes total revenue per (month, region) pair.
- Prints tab-separated results to stdout; exits with code 1 if no matching orders are found.

#### Type
- Python module (script) with one frozen dataclass and three functions.

#### Input
- CLI args: one or more `csv_files` (paths), `--currency` (default `"EUR"`), `--min-revenue` (default `Decimal("0")`).
- CSV columns required: `order_date`, `region`, `amount`, `currency`.
- `order_date` parsed via `date.fromisoformat`; `amount` parsed via `Decimal`.

#### Output
- Stdout: lines of `YYYY-MM<TAB>region<TAB>revenue (2 dp) currency`, sorted by (month, region).
- Stderr: `"no orders found"` when zero orders match.
- Return: `0` on success, `1` when no orders found.

#### Core logic
- `parse_orders` opens a CSV, validates that all `REQUIRED_COLUMNS` are present in the header, then iterates rows.
- Rows whose `currency` field does not match the target are skipped.
- Each valid row is converted to an `Order` dataclass; parse failures raise `ValueError` with file path and line number.
- `revenue_by_month_and_region` accumulates `Decimal` sums in a `defaultdict` keyed by `(YYYY-MM, region)`, then returns a sorted `dict`.
- `main` collects all orders across all files, applies the `--min-revenue` filter, and prints qualifying entries.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – yields filtered orders from one CSV.
- `revenue_by_month_and_region(orders) -> dict[tuple[str,str], Decimal]` – aggregates totals by month and region.
- `main(argv=None) -> int` – CLI entry point; parses args, aggregates, prints, returns exit code.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` raised if required CSV columns are missing (message includes file path and missing column names).
- `ValueError` raised per-row on invalid `order_date` or `amount` (message includes file path, line number, and original error).
- `InvalidOperation` from `Decimal` parsing is caught and re-raised as `ValueError`.
