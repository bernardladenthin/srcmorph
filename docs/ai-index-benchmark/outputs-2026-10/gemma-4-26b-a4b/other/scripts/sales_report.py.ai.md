### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:00:01Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates monthly revenue per region from CSV order exports for a specific currency.

#### Purpose
* Generates a summary report of sales performance.
* Filters and groups transaction data by time period and geographic area.

#### Type
Python script (CLI application)

#### Input
* `csv_files`: Paths to CSV files containing order data.
* `--currency`: Target currency string (default: "EUR").
* `--min-revenue`: Minimum revenue threshold for reporting (default: 0).
* CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
* Printed report to stdout: `YYYY-MM`, `region`, and `revenue` formatted to two decimal places.
* Exit code 0 on success, 1 if no orders are found.

#### Core logic
* `parse_orders`: Validates CSV headers, filters rows by the specified `currency`, and converts raw strings into `Order` dataclass instances.
* `revenue_by_month_and_region`: Uses a `defaultdict` to accumulate `Decimal` amounts, grouping them by a composite key of `(YYYY-MM, region)`.
* `main`: Orchestrates file reading, aggregation, threshold filtering, and formatted output.

#### Public API
* `parse_orders(path, currency) -> Generator[Order]` yields validated orders matching the currency.
* `revenue_by_month_and_region(orders) -> dict` returns sorted revenue totals by month and region.
* `main(argv=None) -> int` executes the CLI logic and returns exit status.

#### Dependencies
* `argparse`
* `csv`
* `collections.defaultdict`
* `dataclasses.dataclass`
* `datetime.date`
* `decimal.Decimal`

#### Exceptions / Errors
* `ValueError`: Raised if required columns are missing from CSV or if date/amount parsing fails.
