### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:49:04Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Generates monthly sales revenue per region from CSV order exports, filtering by currency and minimum revenue.

#### Purpose
- Produce revenue report per month and region.
- Filter orders by currency and minimum revenue.

#### Type
- Python script (`sales_report.py`).

#### Input
- `csv_files`: list of CSV file paths.
- `--currency`: target currency (default `EUR`).
- `--min-revenue`: minimum revenue threshold (default `0`).
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints lines: `month<TAB>region<TAB>revenue currency` for each month-region pair meeting threshold.
- Exits with status `1` if no orders found; otherwise `0`.

#### Core logic
- `parse_orders(path, currency)`:
  - Open CSV, validate required columns.
  - Skip rows with non-matching currency.
  - Convert `order_date` to `date`, `amount` to `Decimal`.
  - Yield `Order` dataclass instances.
- `revenue_by_month_and_region(orders)`:
  - Aggregate amounts into `defaultdict(Decimal)` keyed by `(YYYY-MM, region)`.
  - Return sorted dictionary of totals.
- `main(argv)`:
  - Parse arguments.
  - Collect orders from all files.
  - If none, error message and exit 1.
  - Iterate aggregated totals, print those ≥ `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – read and filter orders.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregate revenue.
- `main(argv=None) -> int` – command‑line entry point.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` for missing columns or invalid data rows.
- `ValueError` propagated from `parse_orders` for malformed dates or amounts.
