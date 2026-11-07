### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:57:53Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Generates a monthly sales report from CSV order exports, aggregating revenue by month and region for a specified currency.

#### Purpose
- Aggregates order revenue per month and region.
- Filters by currency and minimum revenue threshold.

#### Type
- Python script (`.py`).

#### Input
- Positional arguments: list of CSV file paths (`csv_files`).
- Optional arguments:
  - `--currency`: target currency (default `EUR`).
  - `--min-revenue`: minimum revenue to display (default `0`).
- CSV columns required: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue currency`.
- Exits with status `0` on success, `1` if no orders found.

#### Core logic
- `parse_orders(path, currency)`:
  - Opens CSV, validates required columns.
  - Skips rows with non‑matching currency.
  - Yields `Order(order_date, region, amount)`; raises `ValueError` on parse errors.
- `revenue_by_month_and_region(orders)`:
  - Sums `amount` per `(YYYY-MM, region)` key using `defaultdict(Decimal)`.
  - Returns sorted dictionary of totals.
- `main(argv)`:
  - Parses CLI arguments.
  - Collects all orders from provided files.
  - If none, prints error and exits with `1`.
  - Iterates over aggregated totals, prints entries meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – yields orders in specified currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregates revenue.
- `main(argv=None) -> int` – command‑line entry point.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` for missing columns or malformed data.
- `ValueError` propagated from `parse_orders` on invalid rows.
