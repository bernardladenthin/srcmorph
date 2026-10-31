### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:41:15Z
- G: 
- A: 0.0.0
- X: file
---
> Generates monthly sales revenue per region from CSV order exports.

#### Purpose
- Aggregate sales amounts by month and region.
- Filter by currency and minimum revenue threshold.

#### Type
- Python script (`.py`).

#### Input
- CSV files (`csv_files` positional arguments).
- Optional `--currency` (default `EUR`).
- Optional `--min-revenue` (default `0`).

#### Output
- Prints tab‑separated lines: `month<TAB>region<TAB>revenue currency`.
- Exits with status `0` on success, `1` if no orders found.

#### Core logic
- `parse_orders`: read CSV, validate required columns, skip rows with non‑matching currency, convert `order_date` to `date`, `amount` to `Decimal`, yield `Order` objects.
- `revenue_by_month_and_region`: accumulate totals in `defaultdict(Decimal)` keyed by `(YYYY-MM, region)`, return sorted dict.
- `main`: parse args, collect orders from all files, compute totals, print rows meeting `min_revenue`.

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` – yields orders in specified currency.
- `revenue_by_month_and_region(orders) -> Dict[Tuple[str, str], Decimal]` – totals per month/region.
- `main(argv=None) -> int` – command‑line entry point.

#### Constants and configuration
- `REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")`.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
- `ValueError` for missing columns or invalid data rows.
- `ValueError` propagated from `parse_orders` for malformed dates or amounts.
