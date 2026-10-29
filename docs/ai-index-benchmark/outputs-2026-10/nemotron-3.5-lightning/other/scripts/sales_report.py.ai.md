### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:10:10Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates monthly revenue per region from CSV order exports

#### Purpose
- Reads CSV order exports and aggregates revenue per month and region
- Filters orders by currency and minimum revenue threshold

#### Type
Python module; defines Order dataclass, parse_orders generator, revenue aggregation, and main CLI entry; runs as script

#### Input
- csv_files: paths to one or more CSV order exports
- --currency: target currency filter (default EUR)
- --min-revenue: minimum revenue threshold (default 0)
- Required columns: order_date, region, amount, currency

#### Output
- Formatted text: month, region, revenue amount, and currency for rows meeting min-revenue threshold
- Exit code 0 on success; exit code 1 if no orders found
- Prints to stderr if no orders match criteria

#### Core logic
- Parse each CSV via parse_orders, skipping rows with mismatched currency
- Validate required columns exist; raise ValueError if missing
- Yield Order dataclasses with parsed date, region, and Decimal amount
- Accumulate totals in a defaultdict keyed by (month_str, region)
- Sort and output only (month, region) pairs where revenue >= min_revenue

#### Public API
- parse_orders(path, currency) -> Order — yields orders by currency
- revenue_by_month_and_region(orders) -> dict — aggregates revenue by month region
- main(argv) -> int — runs CLI report generation
- Order(order_date, region, amount) — represents frozen order data

#### Dependencies
- argparse, csv, sys, collections, dataclasses, datetime, decimal

#### Exceptions / Errors
- ValueError if CSV missing required columns
- ValueError on invalid date format or amount, annotated with file path and line number
- Printed to stderr if no orders match criteria
