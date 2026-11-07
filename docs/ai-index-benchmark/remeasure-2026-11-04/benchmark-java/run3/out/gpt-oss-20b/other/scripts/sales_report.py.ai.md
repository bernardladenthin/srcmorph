### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:49:21Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Generates a monthly sales report from CSV order exports, aggregating revenue by month and region.

#### Purpose
- Aggregates order amounts per month and region.
- Filters by currency and minimum revenue threshold.

#### Type
- Python script (module) with `main` entry point.

#### Input
- `csv_files`: list of CSV file paths.
- `--currency`: target currency code (default `EUR`).
- `--min-revenue`: minimum revenue to display (default `0`).
- CSV rows must contain columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints lines: `month<TAB>region<TAB>revenue currency` for each bucket meeting `min-revenue`.
- Exits with code `0` on success, `1` if no orders found.

#### Core logic
- `parse_orders(path, currency)`:
  - Opens CSV, validates required columns.
  - Skips rows with non‑matching currency.
  - Yields `Order(order_date, region, amount)`; errors raise `ValueError`.
- `revenue_by_month_and_region(orders)`:
  - Sums `amount` per `(YYYY-MM, region)` key using `defaultdict(Decimal)`.
  - Returns sorted dictionary of totals.
- `main(argv)`:
  - Parses arguments.
  - Collects all orders from provided files.
  - If none, prints error and exits with `1`.
  - Iterates over totals, prints those ≥ `min-revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]`  
  Parses CSV, yields orders in specified currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]`  
  Aggregates revenue per month and region.
- `main(argv=None) -> int`  
  CLI entry point; processes files, prints report.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` for missing columns or invalid data rows.
