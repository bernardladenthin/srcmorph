### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:32:39Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Generates a monthly sales report from CSV order exports, aggregating revenue by month and region in a specified currency.

#### Purpose
- Produce revenue totals per month and region from order CSV files.
- Filter by currency and minimum revenue threshold.

#### Type
- Python script (`.py`).

#### Input
- Positional arguments: list of CSV file paths (`csv_files`).
- Optional arguments: `--currency` (default `"EUR"`), `--min-revenue` (default `Decimal("0")`).
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue<TAB>currency`.
- Exits with status `0` on success, `1` if no orders found.

#### Core logic
- `parse_orders(path, currency)`:
  - Open CSV, validate required columns.
  - Skip rows with non‑matching currency.
  - Convert `order_date` to `date`, `amount` to `Decimal`.
  - Yield `Order` dataclass instances.
- `revenue_by_month_and_region(orders)`:
  - Accumulate totals in `defaultdict(Decimal)` keyed by `(YYYY-MM, region)`.
  - Return sorted dictionary of totals.
- `main(argv)`:
  - Parse CLI arguments.
  - Collect orders from all files.
  - If none, print error and exit with `1`.
  - Iterate sorted totals; print rows meeting `min_revenue` threshold.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]`  
  Yields orders from a CSV file in the specified currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]`  
  Aggregates revenue per month and region.
- `main(argv=None) -> int`  
  Entry point for command‑line execution.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` if CSV missing required columns or contains invalid data.
- `ValueError` propagated from `parse_orders` for malformed rows.
