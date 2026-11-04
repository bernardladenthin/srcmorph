### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:11:18Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Generates a monthly sales report from CSV order exports, aggregating revenue by month and region in a specified currency.

#### Purpose
- Produce revenue totals per month and region from order CSV files.
- Filter by currency and minimum revenue threshold.

#### Type
- Python script (`.py`).

#### Input
- Positional arguments: list of CSV file paths (`csv_files`).
- Optional arguments:
  - `--currency`: target currency code (default `EUR`).
  - `--min-revenue`: minimum revenue to display (default `0`).
- CSV columns required: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue currency`.
- Exits with code `0` on success, `1` if no orders found.

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
  - Collect all orders from provided files.
  - If none, report error.
  - Iterate sorted totals, print those meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]`  
  Parses CSV, yields orders in specified currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]`  
  Aggregates revenue by month and region.
- `main(argv=None) -> int`  
  CLI entry point; processes arguments, outputs report.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` if CSV missing required columns or contains invalid data.
- `ValueError` propagated from `parse_orders` for malformed rows.
