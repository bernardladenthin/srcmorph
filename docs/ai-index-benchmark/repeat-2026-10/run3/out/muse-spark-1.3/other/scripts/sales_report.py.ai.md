### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:24:34Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates monthly revenue per region from order CSV exports.
#### Purpose
- Generates monthly sales report filtered by currency and minimum revenue
- Reads order CSV exports, prints revenue per month and region
#### Type
- Python script / module, dataclass Order frozen
#### Input
- CSV files `csv_files` with columns order_date, region, amount, currency
- CLI `--currency` default EUR, `--min-revenue` Decimal default 0
- Order fields order_date ISO date, region string, amount Decimal
#### Output
- STDOUT lines `YYYY-MM region revenue currency` filtered by min-revenue
- STDERR `no orders found` with exit 1 when empty, exit 0 on success
- `revenue_by_month_and_region` returns dict[(month, region)] -> Decimal
#### Core logic
- `parse_orders` validates REQUIRED_COLUMNS, skips non-matching currency rows
- Parses order_date via date.fromisoformat, region stripped, amount via Decimal
- `revenue_by_month_and_region` groups Order by strftime %Y-%m and region via defaultdict
- Sorts totals items, `main` flattens orders across csv_files and prints thresholded revenues
#### Public API
- `Order(order_date, region, amount) -> Order` - immutable sales order record
- `parse_orders(path, currency) -> Iterator[Order]` - yields matching-currency orders only
- `revenue_by_month_and_region(orders) -> dict` - sums revenue by month-region
- `main(argv) -> int` - parses CLI, prints report
#### Dependencies
- argparse, csv.DictReader, sys, collections.defaultdict, dataclasses.dataclass, datetime.date, decimal.Decimal
#### Exceptions / Errors
- ValueError on missing columns with path and column list
- ValueError on bad order_date / amount with path:line_no
