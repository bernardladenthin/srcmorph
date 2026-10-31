### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:36:33Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates monthly sales revenue per region from order CSV exports, filtered by currency and minimum revenue.

#### Purpose
- Reads one or more order CSV files and prints total revenue per (year-month, region) pair.
- Filters orders to a single currency and hides totals below a threshold.

#### Type
- Python script (module with CLI entry point) in Python 3.
- Contains `dataclass Order` (frozen) and functions `parse_orders` (generator), `revenue_by_month_and_region`, `main`.

#### Input
- CLI args: `csv_files` (one or more paths), `--currency` (default `EUR`), `--min-revenue` (Decimal, default `0`).
- CSV columns required: `order_date` (ISO date), `region` (string, stripped), `amount` (Decimal), `currency`.
- Files read as UTF-8 via `csv.DictReader`.
- Program argument list `argv` (optional, for `main`).

#### Output
- Stdout lines: `YYYY-MM<TAB>region<TAB>amount(2 decimals) CURRENCY`, sorted by (month, region).
- Stderr: `no orders found` when no matching orders.
- Return codes: `0` on success, `1` when no orders; `sys.exit` wraps `main`.

#### Core logic
- `parse_orders(path, currency)`: opens CSV, validates header contains all `REQUIRED_COLUMNS`, else raises `ValueError`.
- Skips rows whose `currency` differs from the requested currency.
- Converts each row to `Order(date, region, Decimal amount)`; invalid date or amount raises `ValueError` with file and line number (line numbering starts at 2).
- `revenue_by_month_and_region(orders)`: groups by key `(strftime("%Y-%m"), region)` into `defaultdict(Decimal)`, sums amounts, returns sorted dict.
- `main`: parses args, flattens orders from all files via generator, exits with 1 if empty, prints rows where `revenue >= min_revenue`.
- Amounts are formatted with two decimal places; no currency conversion is performed.

#### Public API
- `Order(order_date: date, region: str, amount: Decimal)` - immutable order record.
- `parse_orders(path: str, currency: str) -> Iterator[Order]` - yields orders in currency from CSV.
- `revenue_by_month_and_region(orders: Iterable[Order]) -> dict[tuple[str, str], Decimal]` - sums revenue per month and region.
- `main(argv: list[str] | None = None) -> int` - CLI entry; prints report, returns exit code.

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- External files: input CSV exports with required columns.

#### Exceptions / Errors
- `ValueError` when a file lacks required columns (message lists missing names).
- `ValueError` wrapping `ValueError` or `InvalidOperation` for bad date/amount, chained with `from e`, includes `path:line_no`.
- `OSError` from `open` on missing or unreadable files propagates uncaught.
- `argparse` exits with SystemExit on invalid arguments (e.g., non-decimal `--min-revenue`).
- Empty result returns exit code `1` with stderr message.
