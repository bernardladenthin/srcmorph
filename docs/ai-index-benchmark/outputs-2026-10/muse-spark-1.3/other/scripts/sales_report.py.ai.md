### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:17:34Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates monthly sales revenue per region from order CSV exports.
#### Purpose
- Monthly sales report CLI filtering orders by currency and minimum revenue
- Groups Order revenue by YYYY-MM month and region for reporting
#### Type
- Python script/module with frozen dataclass Order
#### Input
- CLI csv_files paths, --currency default EUR, --min-revenue Decimal default 0
- CSV columns order_date, region, amount, currency via csv.DictReader
- Order fields order_date date, region str, amount Decimal
#### Output
- stdout lines month, region, revenue formatted 2 decimals + currency
- revenue_by_month_and_region returns dict[(month,region)] -> Decimal sorted
- exit code 0 success, 1 when no orders found; stderr no orders found
#### Core logic
- parse_orders opens UTF-8 CSV, validates REQUIRED_COLUMNS, skips non-matching currency
- parse_orders parses order_date via date.fromisoformat, region stripped, amount via Decimal
- revenue_by_month_and_region buckets Order.amount by strftime %Y-%m + region using defaultdict Decimal
- main expands all csv_files via parse_orders, aggregates, filters revenue >= min-revenue, prints tab-separated report
#### Public API
- Order(order_date, region, amount) -> Order : immutable sales order record
- parse_orders(path, currency) -> Iterator[Order] : yields filtered currency orders only
- revenue_by_month_and_region(orders) -> dict : sums revenue per month-region
- main(argv) -> int : parses CLI, prints revenue report
#### Dependencies
- argparse, csv, sys, collections.defaultdict, dataclasses.dataclass, datetime.date, decimal.Decimal, decimal.InvalidOperation
#### Exceptions / Errors
- ValueError missing columns listing path + column names
- ValueError path:line_no wrapping ValueError, InvalidOperation on bad date/amount
