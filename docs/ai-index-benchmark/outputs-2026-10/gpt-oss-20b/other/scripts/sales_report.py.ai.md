### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:52:11Z
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
- Python script (module) with `Order` dataclass and functions.

#### Input
- CSV files specified on command line.
- Optional `--currency` filter (default EUR).
- Optional `--min-revenue` threshold (Decimal).

#### Output
- Prints lines: `YYYY-MM<TAB>region<TAB>revenue<TAB>currency`.
- Exits with status 0 on success, 1 if no orders found.

#### Core logic
- `parse_orders(path, currency)`:
  - Open CSV, validate required columns.
  - Skip rows with non‑matching currency.
  - Convert `order_date` to `date`, `amount` to `Decimal`.
  - Yield `Order` objects; raise `ValueError` on parse errors.
- `revenue_by_month_and_region(orders)`:
  - Sum `amount` per `(month, region)` key using `defaultdict`.
  - Return sorted dictionary of totals.
- `main(argv)`:
  - Parse arguments.
  - Collect orders from all files.
  - If none, print error and exit 1.
  - Iterate sorted totals, print rows meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – yields orders in specified currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregates revenue.
- `main(argv=None) -> int` – command‑line entry point.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` for missing columns or parsing failures.
- `InvalidOperation` wrapped in `ValueError` for amount conversion errors.
