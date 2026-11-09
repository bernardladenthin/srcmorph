### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:34:14Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Generates monthly revenue reports from CSV order exports, filtering by currency and minimum revenue.

#### Purpose
- Produce revenue per month and region from order CSVs.
- Filter by currency and minimum revenue threshold.

#### Type
- Python script (`.py`).

#### Input
- `csv_files`: list of CSV file paths (positional arguments).
- `--currency`: target currency code (default `EUR`).
- `--min-revenue`: minimum revenue to display (default `0`).
- CSV columns: `order_date`, `region`, `amount`, `currency`.

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
  - Aggregate amounts by `(YYYY-MM, region)` using `defaultdict(Decimal)`.
  - Return sorted dictionary of totals.
- `main(argv)`:
  - Parse CLI arguments.
  - Collect all orders from provided files.
  - If none, print error and exit `1`.
  - Iterate aggregated totals; print those meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]`  
  Parses CSV, yields orders in specified currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]`  
  Aggregates revenue per month and region.
- `main(argv=None) -> int`  
  CLI entry point; processes arguments, outputs report.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` if CSV missing required columns or contains invalid data.
- `ValueError` propagated from `parse_orders` for malformed rows.
