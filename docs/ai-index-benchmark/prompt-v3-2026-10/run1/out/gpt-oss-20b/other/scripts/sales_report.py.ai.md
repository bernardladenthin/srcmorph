### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:55:17Z
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
- Positional arguments: list of CSV file paths (`csv_files`).
- Optional flags: `--currency` (default `EUR`), `--min-revenue` (default `0`).
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
  - Parse arguments.
  - Collect orders from all files.
  - If none, print error and exit 1.
  - Iterate sorted totals, filter by `min_revenue`, print formatted line.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – yields orders in given currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregates totals.
- `main(argv=None) -> int` – command‑line entry point.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` if CSV missing required columns or row parsing fails.
- `ValueError` propagated from `parse_orders` for invalid data.
