### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:54:20Z
- G: 
- A: 0.0.0
- X: file
---
> Monthly sales report: reads order CSV exports and prints revenue per month and region.

#### Purpose
- Reads one or more CSV order exports and aggregates revenue by month and region
- Supports currency filtering and a minimum revenue threshold

#### Type
Python script (module); executable entry point; uses `argparse`, `csv`, `dataclasses`, `decimal`

#### Input
- `csv_files`: one or more file paths to CSV exports
- Each CSV must contain columns: `order_date`, `region`, `amount`, `currency`
- `--currency`: target currency filter (default: "EUR")
- `--min-revenue`: minimum revenue threshold (default: 0)

#### Output
- Printed to stdout: `month\tregion\trevenue currency` for each group meeting the threshold
- Returns exit code 0 on success, 1 if no orders found

#### Core logic
- Parse each CSV file, skipping rows whose currency does not match the `--currency` filter
- Validate required columns exist; raise `ValueError` if missing
- Parse `order_date` via `date.fromisoformat`, `amount` via `Decimal`
- Accumulate `Decimal` totals keyed by `(YYYY-MM, region)` using `defaultdict`
- Sort results lexicographically by month then region
- Print each `(month, region)` pair where revenue >= `--min-revenue`

#### Public API
- `parse_orders(path, currency)` -> yields `Order` objects from one CSV file matching the currency
- `revenue_by_month_and_region(orders)` -> `dict` of `(month, region) -> total revenue`, sorted
- `main(argv=None)` -> entry point; processes CLI args, drives parsing and printing

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal` / `InvalidOperation`

#### Exceptions / Errors
- `ValueError` raised if a CSV is missing required columns or contains unparseable `order_date` / `amount` values
