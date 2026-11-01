### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:28:12Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a monthly sales report from CSV order exports, aggregating revenue by month and region in a specified currency.

#### Purpose
- Reads CSV order files.
- Aggregates revenue per month and region.
- Prints results with optional minimum revenue filter.

#### Type
- Python script (module) with `main` entry point.

#### Input
- Command‑line arguments: one or more CSV file paths (`csv_files`).
- Optional flags: `--currency` (default `EUR`), `--min-revenue` (default `0`).
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue currency`.
- Exits with status 0 on success, 1 if no orders found.

#### Core logic
- `parse_orders(path, currency)`:
  - Opens CSV, validates required columns.
  - Skips rows with non‑matching currency.
  - Yields `Order` dataclass instances.
- `revenue_by_month_and_region(orders)`:
  - Sums `amount` per `(YYYY-MM, region)` key.
  - Returns sorted dictionary.
- `main(argv)`:
  - Parses arguments.
  - Collects orders from all files.
  - Filters by `min_revenue`.
  - Prints formatted revenue lines.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – yields orders in specified currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregates revenue.
- `main(argv=None) -> int` – command‑line entry point.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`
- Default currency: `"EUR"`
- Default minimum revenue: `Decimal("0")`

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`

#### Exceptions / Errors
- `ValueError` if CSV missing required columns or contains invalid data.
- `ValueError` propagated from `parse_orders` for malformed rows.
