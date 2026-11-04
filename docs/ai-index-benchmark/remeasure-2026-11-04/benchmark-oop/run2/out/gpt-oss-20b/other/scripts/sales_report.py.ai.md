### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:41:30Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
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
- CSV rows must contain columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints lines: `month<TAB>region<TAB>revenue <currency>` for each month/region meeting the threshold.
- Exits with status `1` if no orders found; otherwise `0`.

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
  - Iterate over aggregated totals, filter by `min_revenue`, print formatted output.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – read and filter orders from a CSV file.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregate revenue.
- `main(argv=None) -> int` – command‑line entry point.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` if CSV missing required columns or contains invalid data.
- `ValueError` propagated from `parse_orders` for malformed rows.
