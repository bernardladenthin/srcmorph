### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:56:22Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Generates monthly revenue reports from CSV order exports, filtering by currency and minimum revenue.

#### Purpose
- Compute revenue per month and region from order CSV files.
- Output tab‑separated lines: month, region, revenue.

#### Type
- Python script (`.py`).

#### Input
- Positional arguments: list of CSV file paths (`csv_files`).
- Optional arguments:
  - `--currency` (default `"EUR"`): filter orders by this currency.
  - `--min-revenue` (Decimal, default `0`): minimum revenue threshold for output.
- CSV columns required: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints to stdout lines: `YYYY-MM<TAB>region<TAB>revenue<TAB>currency`.
- Returns exit code `0` on success, `1` if no orders found.

#### Core logic
- `parse_orders(path, currency)`:
  - Open CSV, validate required columns.
  - Iterate rows; skip rows with different currency.
  - Convert `order_date` to `date`, `amount` to `Decimal`.
  - Yield `Order` dataclass instances.
- `revenue_by_month_and_region(orders)`:
  - Aggregate amounts into `defaultdict(Decimal)` keyed by `(month, region)`.
  - Return sorted dictionary of totals.
- `main(argv)`:
  - Parse CLI arguments.
  - Collect all orders from provided files.
  - If none, print error and exit with code `1`.
  - For each `(month, region)` with revenue ≥ `min_revenue`, print formatted line.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]`  
  Yields orders from a CSV file in the specified currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]`  
  Aggregates revenue by month and region.
- `main(argv=None) -> int`  
  Entry point; processes CLI, outputs report.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` if CSV missing required columns or contains invalid data.
- `ValueError` propagated from `parse_orders` for malformed rows.
