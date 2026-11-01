### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:07:41Z
- G: 
- A: 0.0.0
- X: file
---
> Generates monthly sales revenue per region from CSV order exports.

#### Purpose
- Compute revenue totals by month and region.
- Filter by currency and minimum revenue.

#### Type
- Python script (`.py`).

#### Input
- CSV files via positional arguments (`csv_files`).
- Optional `--currency` (default `EUR`).
- Optional `--min-revenue` (default `0`).

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue currency`.
- Exits with code `1` if no orders found; otherwise `0`.

#### Core logic
- `parse_orders(path, currency)`:
  - Open CSV, validate required columns.
  - Skip rows with non‑matching currency.
  - Convert `order_date` to `date`, `amount` to `Decimal`.
  - Yield `Order` dataclass instances.
- `revenue_by_month_and_region(orders)`:
  - Aggregate amounts into `defaultdict(Decimal)` keyed by `(YYYY-MM, region)`.
  - Return sorted dictionary.
- `main(argv)`:
  - Parse CLI args.
  - Collect all orders from provided files.
  - If none, error message and exit `1`.
  - Iterate aggregated totals; print those ≥ `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]`  
  Yields orders in specified currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]`  
  Aggregates revenue by month and region.
- `main(argv=None) -> int`  
  CLI entry point; processes files and prints report.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` on missing columns or parsing errors.
- `ValueError` propagated from `parse_orders` with file and line context.
