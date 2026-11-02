### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:42:40Z
- G: 
- A: 0.0.0
- X: file
---
> Generates monthly sales revenue per region from CSV order exports.

#### Purpose
- Compute revenue totals by month and region.
- Filter by currency and minimum revenue threshold.

#### Type
- Python script (`.py`).

#### Input
- CSV files specified as positional arguments.
- `--currency` option (default `EUR`).
- `--min-revenue` option (default `0`).
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints lines: `month<TAB>region<TAB>revenue<TAB>currency`.
- Exits with code `1` if no orders found; otherwise `0`.

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
  - Parse arguments.
  - Collect all orders from all files.
  - If none, print error and exit 1.
  - Iterate over aggregated totals, print those meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – yields orders in specified currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregates revenue.
- `main(argv=None) -> int` – command‑line entry point.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` if CSV missing required columns or contains invalid data.
- `ValueError` propagated from `parse_orders` for malformed rows.
