### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:18:18Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates monthly sales revenue per region from order CSV exports.

#### Purpose
- Reads order CSV exports and reports revenue per month and region
- Filters orders by currency and minimum revenue threshold

#### Type
- Python script/module with frozen dataclass `Order`, functions `parse_orders`, `revenue_by_month_and_region`, `main`

#### Input
- CLI `csv_files` paths, `--currency` default EUR, `--min-revenue` Decimal default 0
- CSV columns `order_date`, `region`, `amount`, `currency` via `csv.DictReader`
- `Order` fields `order_date` date, `region` str, `amount` Decimal

#### Output
- TSV stdout lines `YYYY-MM region revenue currency` filtered by `min-revenue`
- `dict` keyed `(month, region)` to Decimal revenue from `revenue_by_month_and_region`
- `Order` generator from `parse_orders`, stderr `no orders found`, exit code 0/1

#### Core logic
- `parse_orders` validates `REQUIRED_COLUMNS`, skips non-matching `currency`, strips `region`
- `parse_orders` parses `order_date` via `date.fromisoformat`, `amount` via `Decimal`
- `revenue_by_month_and_region` groups by `strftime("%Y-%m")` and `region` using `defaultdict(Decimal)`
- `revenue_by_month_and_region` sorts totals items into dict
- `main` aggregates orders across `csv_files`, exits 1 if empty, prints revenues >= `min-revenue` formatted `.2f`

#### Public API
- `parse_orders(path, currency) -> Iterator[Order]` - filters currency, validates rows
- `revenue_by_month_and_region(orders) -> dict` - groups revenue by month-region
- `main(argv) -> int` - CLI orchestration, prints report

#### Dependencies
- argparse, csv, sys, collections.defaultdict, dataclasses.dataclass, datetime.date, decimal.Decimal, decimal.InvalidOperation

#### Exceptions / Errors
- `ValueError` on missing columns with path and column names
- `ValueError` on invalid `order_date`/`amount` with path:line_no cause
