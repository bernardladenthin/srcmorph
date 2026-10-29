### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:55:03Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a monthly sales report from CSV order exports, aggregating revenue by month and region.

#### Purpose
- Reads CSV order files.
- Aggregates revenue per month and region.
- Prints results filtered by minimum revenue.

#### Type
- Python script (module). Defines `Order` dataclass, functions `parse_orders`, `revenue_by_month_and_region`, and `main`.

#### Input
- CSV files via command line (`csv_files`).
- Optional `--currency` filter (default EUR).
- Optional `--min-revenue` threshold (Decimal).

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue currency`.
- Exits with status 0 on success, 1 if no orders found.

#### Core logic
- `parse_orders(path, currency)`:
  - Open CSV, validate required columns.
  - Skip rows with non‑matching currency.
  - Convert `order_date` to `date`, `amount` to `Decimal`.
  - Yield `Order` instances; raise `ValueError` on parsing errors.
- `revenue_by_month_and_region(orders)`:
  - Accumulate totals in `defaultdict(Decimal)` keyed by `(YYYY-MM, region)`.
  - Return sorted dictionary of totals.
- `main(argv)`:
  - Parse arguments.
  - Collect orders from all files.
  - If none, print error and exit 1.
  - Iterate sorted totals; print lines where revenue ≥ `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – yields orders in specified currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregates revenue.
- `main(argv=None) -> int` – command‑line entry point.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` for missing columns or invalid data rows.
- `ValueError` propagated from `parse_orders` on parsing failures.
