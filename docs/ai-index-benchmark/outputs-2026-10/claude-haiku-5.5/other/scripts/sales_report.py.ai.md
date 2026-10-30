### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:32:35Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates order CSV exports into monthly revenue per region for one currency.

#### Purpose
- Command-line monthly sales report: sums order amounts by month and region.
- Filters orders by currency and minimum revenue threshold.

#### Type
- Python module (script with `main` entry point); `Order` is a frozen `dataclass`.
- Functions: `parse_orders` (generator), `revenue_by_month_and_region`, `main`.

#### Input
- CLI args via `argparse`: `csv_files` (one or more paths), `--currency` (default `EUR`), `--min-revenue` (`Decimal`, default `0`).
- CSV columns required: `order_date` (ISO date), `region`, `amount` (`Decimal`), `currency`.
- Files opened as UTF-8 with `csv.DictReader`.

#### Output
- Stdout lines: `YYYY-MM<TAB>region<TAB>revenue CURRENCY` with 2 decimals, sorted by (month, region).
- Stderr: `no orders found` when no matching rows.
- Return code: `0` on success, `1` when no orders; passed to `sys.exit`.

#### Core logic
- `parse_orders` validates header presence of all `REQUIRED_COLUMNS`; missing columns raise `ValueError`.
- Skips rows whose `currency` differs from the requested currency.
- Builds `Order` from `date.fromisoformat(order_date)`, stripped `region`, `Decimal(amount)`.
- Parse failures re-raised as `ValueError` with file path and line number (line numbering starts at 2).
- `main` flattens orders across all input files into one list.
- `revenue_by_month_and_region` groups by key `(strftime("%Y-%m"), region)` using `defaultdict(Decimal)`, returns sorted dict.
- Output filtered by `revenue >= min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]`: yields orders in given currency.
- `revenue_by_month_and_region(orders) -> dict[tuple[str, str], Decimal]`: sums amounts per month and region.
- `main(argv=None) -> int`: runs report, returns exit code.
- `Order(order_date: date, region: str, amount: Decimal)`: immutable order record.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` on missing CSV columns.
- `ValueError` on invalid date or amount, with `path:line` context, chained from original error.
- `OSError` from `open` on unreadable files (not handled).
- `argparse` exits on invalid arguments, e.g. non-decimal `--min-revenue`.
