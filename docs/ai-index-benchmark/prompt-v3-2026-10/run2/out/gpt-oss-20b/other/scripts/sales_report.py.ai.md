### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:20:52Z
- G: 
- A: 0.0.0
- X: file
---
> Generates monthly sales revenue per region from CSV order exports.

#### Purpose
- Compute revenue totals by month and region.
- Filter by currency and minimum revenue threshold.

#### Type
- Python script (`sales_report.py`).

#### Input
- CSV files via positional arguments (`csv_files`).
- Optional flags: `--currency` (default `EUR`), `--min-revenue` (default `0`).
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue currency`.
- Exits with code `1` if no orders found; otherwise `0`.

#### Core logic
- `parse_orders(path, currency)`:
  - Open CSV, validate required columns.
  - Skip rows with non‑matching currency.
  - Convert `order_date` to `date`, `amount` to `Decimal`.
  - Yield `Order` dataclass instances.
- `revenue_by_month_and_region(orders)`:
  - Aggregate `Decimal` amounts keyed by `(YYYY-MM, region)`.
  - Return sorted dictionary of totals.
- `main(argv)`:
  - Parse CLI arguments.
  - Collect orders from all files.
  - If none, report error.
  - Iterate aggregated totals, print those meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – read and filter orders.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregate totals.
- `main(argv=None) -> int` – command‑line entry point.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` on missing columns or parsing errors.
- `ValueError` propagated from `parse_orders` for invalid data rows.
