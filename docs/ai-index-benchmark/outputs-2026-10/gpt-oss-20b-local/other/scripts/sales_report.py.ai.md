### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:19:32Z
- G: 
- A: 0.0.0
- X: file
---
> Generates monthly sales revenue per region from CSV order exports.

#### Purpose
- Compute revenue totals by month and region.
- Filter by currency and minimum revenue threshold.

#### Type
- Python script (module) with `Order` dataclass, functions, and CLI entry point.

#### Input
- CSV files (`csv_files` positional arguments) with columns: `order_date`, `region`, `amount`, `currency`.
- Optional CLI options: `--currency` (default `EUR`), `--min-revenue` (Decimal, default `0`).

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue currency`.
- Exits with status 0 on success, 1 if no orders found.

#### Core logic
- `parse_orders(path, currency)`:
  - Open CSV, validate required columns.
  - Skip rows with non‑matching currency.
  - Convert `order_date` to `date`, `amount` to `Decimal`.
  - Yield `Order` objects; raise `ValueError` on parsing errors.
- `revenue_by_month_and_region(orders)`:
  - Aggregate `Decimal` amounts keyed by `(YYYY-MM, region)`.
  - Return sorted dictionary of totals.
- `main(argv)`:
  - Parse CLI arguments.
  - Collect all orders from provided files.
  - If none, report error and exit 1.
  - Iterate sorted totals, print those meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – yields orders in specified currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregates revenue.
- `main(argv=None) -> int` – command‑line entry point.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` for missing columns or invalid data rows.
- `ValueError` propagated from `parse_orders` on parsing failures.
