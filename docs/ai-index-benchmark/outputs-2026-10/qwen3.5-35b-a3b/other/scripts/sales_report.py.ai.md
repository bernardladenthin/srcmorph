### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:38:36Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates monthly revenue by region from CSV order exports filtered by currency.

#### Purpose
*   CLI tool to aggregate sales data from CSV order exports.
*   Filters orders by currency and minimum revenue threshold.

#### Type
Python script (module / executable).

#### Input
*   Command line arguments: `csv_files` (list), `--currency` (string), `--min-revenue` (Decimal).
*   CSV files containing columns: `order_date`, `region`, `amount`, `currency`.

#### Output
*   Standard output: Tab-separated lines (month, region, revenue).
*   Standard error: "no orders found" message.
*   Exit code: 0 on success, 1 on failure.

#### Core logic
*   Validate CSV headers against `REQUIRED_COLUMNS`.
*   Filter rows where `currency` matches argument.
*   Parse `order_date` to `date` and `amount` to `Decimal`.
*   Aggregate amounts into `defaultdict` keyed by `(YYYY-MM, region)`.
*   Sort aggregated results by key.
*   Filter aggregated totals by `min-revenue`.
*   Print formatted revenue lines.

#### Public API
*   `parse_orders(path, currency) -> Generator[Order]`
*   `revenue_by_month_and_region(orders) -> Dict[(str, str), Decimal]`
*   `main(argv) -> int`

#### Dependencies
*   `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Exceptions / Errors
*   `ValueError`: Missing CSV columns, invalid date/amount format.
*   `InvalidOperation`: Decimal parsing failure.
*   Exit code 1: No orders found.
