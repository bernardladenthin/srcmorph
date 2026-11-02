### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:03:11Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Generates monthly sales revenue per region from CSV order exports, filtering by currency and minimum revenue.

#### Purpose
- Produce revenue summaries per month and region.
- Filter orders by currency and minimum revenue.

#### Type
- Python script (`.py`).

#### Input
- Positional arguments: list of CSV file paths (`csv_files`).
- Optional arguments: `--currency` (default `"EUR"`), `--min-revenue` (default `Decimal("0")`).
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue currency`.
- Exits with status 0 on success, 1 if no orders found.

#### Core logic
- `parse_orders(path, currency)`:
  - Open CSV, validate required columns.
  - Skip rows with non‑matching currency.
  - Convert `order_date` to `date`, `amount` to `Decimal`.
  - Yield `Order` dataclass instances.
- `revenue_by_month_and_region(orders)`:
  - Aggregate amounts into `defaultdict(Decimal)` keyed by `(YYYY-MM, region)`.
  - Return sorted dictionary of totals.
- `main(argv)`:
  - Parse CLI args.
  - Collect orders from all files.
  - If none, error message and exit 1.
  - Iterate sorted totals; print entries meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – read and filter orders.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregate totals.
- `main(argv=None) -> int` – command‑line entry point.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` if CSV missing required columns or data conversion fails.
- `ValueError` propagated from `parse_orders` for malformed rows.
