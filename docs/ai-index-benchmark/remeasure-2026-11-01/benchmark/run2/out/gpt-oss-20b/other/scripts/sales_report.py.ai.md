### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:47:58Z
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
- Python script (`.py`).

#### Input
- Positional arguments: list of CSV file paths.
- Optional arguments: `--currency` (default `EUR`), `--min-revenue` (Decimal, default `0`).
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints lines: `month<TAB>region<TAB>revenue currency`.
- Exits with status 0 on success, 1 if no orders found.

#### Core logic
- `parse_orders(path, currency)`:
  - Open CSV, validate required columns.
  - Skip rows with non‑matching currency.
  - Convert `order_date` to `date`, `amount` to `Decimal`.
  - Yield `Order` dataclass instances.
- `revenue_by_month_and_region(orders)`:
  - Sum `amount` per `(YYYY-MM, region)` key using `defaultdict`.
  - Return sorted dictionary of totals.
- `main(argv)`:
  - Parse CLI args.
  - Collect all orders from provided files.
  - If none, error message and exit 1.
  - Iterate sorted totals, print entries meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – yields orders in specified currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregates revenue.
- `main(argv=None) -> int` – command‑line entry point.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` if CSV missing required columns or row parsing fails.
- `ValueError` propagated from `parse_orders` for invalid data.
