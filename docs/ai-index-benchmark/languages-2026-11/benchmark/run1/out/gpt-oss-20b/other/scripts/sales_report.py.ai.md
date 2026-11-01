### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:20:53Z
- G: 
- A: 0.0.0
- X: file
---
> Generates monthly sales revenue per region from CSV order exports.

#### Purpose
- Produce revenue summaries by month and region.
- Filter by currency and minimum revenue.

#### Type
- Python script (`.py`).

#### Input
- CSV files (`csv_files` positional arguments).
- `--currency` (default `EUR`).
- `--min-revenue` (Decimal, default `0`).
- CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue currency`.
- Exits with status `0` on success, `1` if no orders found.

#### Core logic
- `parse_orders`: read CSV, validate columns, filter by currency, convert `order_date` to `date`, `amount` to `Decimal`, yield `Order` objects.
- `revenue_by_month_and_region`: aggregate amounts into `defaultdict(Decimal)` keyed by `(YYYY-MM, region)`, return sorted dict.
- `main`: parse args, collect orders from all files, compute totals, print rows meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – read and filter orders.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – aggregate totals.
- `main(argv=None) -> int` – command‑line entry point.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` if CSV missing required columns or contains invalid data.
- `ValueError` propagated from `parse_orders` for malformed rows.
