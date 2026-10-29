### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:15:32Z
- G: 
- A: 0.0.0
- X: file
---
> Generates monthly sales revenue per region from CSV order exports.

#### Purpose
- Compute revenue totals by month and region.
- Filter by currency and minimum revenue.

#### Type
- Python script (module). Defines `Order` dataclass, functions, and CLI entry point.

#### Input
- CSV files (`csv_files` positional arguments).
- Optional `--currency` (default EUR).
- Optional `--min-revenue` (Decimal).

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue currency`.
- Exits with status 0 on success, 1 if no orders.

#### Core logic
- `parse_orders(path, currency)`:
  - Open CSV, validate required columns.
  - Skip rows with different currency.
  - Convert `order_date` to `date`, `amount` to `Decimal`.
  - Yield `Order` objects; raise `ValueError` on parse errors.
- `revenue_by_month_and_region(orders)`:
  - Aggregate amounts into `defaultdict(Decimal)` keyed by `(YYYY-MM, region)`.
  - Return sorted dictionary.
- `main(argv)`:
  - Parse CLI args.
  - Collect orders from all files.
  - If none, error message and exit 1.
  - Iterate aggregated totals, print those meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – read orders from CSV.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregate totals.
- `main(argv=None) -> int` – command‑line entry point.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` for missing columns or invalid data.
- `ValueError` propagated from `parse_orders` on parse failures.
