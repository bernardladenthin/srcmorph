### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:55:06Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Generates a monthly sales report from CSV order exports, aggregating revenue by month and region for a specified currency.

#### Purpose
- Aggregate order amounts by month and region.
- Output revenue per month/region in a specified currency.

#### Type
- Python script (`.py`).

#### Input
- `csv_files`: list of CSV file paths (positional arguments).
- `--currency`: target currency code (default `EUR`).
- `--min-revenue`: minimum revenue threshold (default `0`).
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue currency`.
- Exits with status `0` on success, `1` if no orders found.

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
  - Parse CLI arguments.
  - Collect orders from all files.
  - If none, error message and exit `1`.
  - Iterate sorted totals; print rows meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]`  
  Parses CSV, filters by currency, yields orders.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]`  
  Aggregates revenue per month/region.
- `main(argv=None) -> int`  
  CLI entry point; processes arguments, outputs report.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` on missing columns or invalid data rows.
- `ValueError` propagated from `parse_orders` for malformed dates or amounts.
