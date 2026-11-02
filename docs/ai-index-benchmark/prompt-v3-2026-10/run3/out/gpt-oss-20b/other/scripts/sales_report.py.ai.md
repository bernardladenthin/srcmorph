### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:45:24Z
- G: 
- A: 0.0.0
- X: file
---
> Generates monthly revenue reports from CSV order exports, filtering by currency and minimum revenue.

#### Purpose
- Produce revenue per month and region from order CSV files.
- Filter by currency and minimum revenue threshold.

#### Type
- Python script (module) with `main` entry point.

#### Input
- Positional arguments: list of CSV file paths (`csv_files`).
- Optional flags: `--currency` (default "EUR"), `--min-revenue` (Decimal, default 0).
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue<TAB>currency`.
- Exits with status 0 on success, 1 if no orders found.

#### Core logic
- `parse_orders(path, currency)`:
  - Open CSV, validate required columns.
  - Skip rows with non‑matching currency.
  - Convert `order_date` to `date`, `amount` to `Decimal`.
  - Yield `Order` dataclass instances.
- `revenue_by_month_and_region(orders)`:
  - Aggregate totals in `defaultdict(Decimal)` keyed by `(YYYY-MM, region)`.
  - Return sorted dictionary of totals.
- `main(argv)`:
  - Parse CLI arguments.
  - Collect all orders from provided files.
  - If none, error message and exit 1.
  - Iterate aggregated totals, print those meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – read and filter orders.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregate revenue.
- `main(argv=None) -> int` – command‑line entry point.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` if CSV missing required columns or contains invalid data.
- `ValueError` propagated for parsing errors with file and line context.
