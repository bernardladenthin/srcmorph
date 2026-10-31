### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:54:29Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a monthly sales report from CSV order exports, aggregating revenue by month and region in a specified currency.

#### Purpose
- Reads CSV order files.
- Aggregates revenue per month and region.
- Prints results filtered by minimum revenue.

#### Type
- Python script (module) with `dataclass` `Order`.

#### Input
- CSV files via positional arguments.
- Optional `--currency` (default `EUR`).
- Optional `--min-revenue` (default `0`).

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue currency`.
- Exits with status 0 on success, 1 if no orders found.

#### Core logic
- `parse_orders(path, currency)`:
  - Open CSV, validate required columns.
  - Skip rows with different currency.
  - Convert `order_date` to `date`, `amount` to `Decimal`.
  - Yield `Order` objects.
- `revenue_by_month_and_region(orders)`:
  - Sum amounts into `defaultdict(Decimal)` keyed by `(YYYY-MM, region)`.
  - Return sorted dictionary.
- `main(argv)`:
  - Parse arguments.
  - Collect all orders from all files.
  - If none, error message and exit 1.
  - Iterate sorted totals, print those meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – yields orders in given currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregates revenue.
- `main(argv=None) -> int` – command‑line entry point.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` if CSV missing required columns or row data invalid.
- `ValueError` propagated from `parse_orders` for malformed rows.
