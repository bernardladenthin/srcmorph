### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:05:15Z
- G: 
- A: 0.0.0
- X: file
---
> Generates monthly sales revenue reports by aggregating order amounts from CSV files, filtered by currency and minimum revenue threshold.

#### Purpose
*   Reads order CSV exports and filters by specified currency.
*   Aggregates revenue by month and region, applying minimum revenue filters.

#### Type
Script (Python)

#### Input
*   List of CSV file paths.
*   Currency filter (default: EUR).
*   Minimum revenue threshold (default: 0).
*   CSV columns: `order_date`, `region`, `amount`, `currency`.

#### Output
*   Prints matching month, region, and revenue to stdout.
*   Returns exit code 0 on success, 1 if no orders found.

#### Core logic
*   Validates CSV headers against required columns (`order_date`, `region`, `amount`, `currency`).
*   Iterates through CSV rows, skipping non-matching currencies.
*   Parses date, region, and amount into `Order` dataclass.
*   Aggregates amounts into a dictionary keyed by `(YYYY-MM, region)`.
*   Filters aggregated results against the minimum revenue threshold.
*   Prints formatted output for valid entries.

#### Public API
`main(argv) -> int` - Entry point parsing CLI args and generating report.

#### Dependencies
*   `argparse` - CLI argument parsing.
*   `csv` - CSV file reading.
*   `sys` - Standard output and exit codes.
*   `collections.defaultdict` - Aggregation logic.
*   `dataclasses` - Data structure definition.
*   `datetime` - Date parsing and formatting.
*   `decimal` - Precise monetary calculations.

#### Exceptions / Errors
*   `ValueError` - Raised for missing CSV columns or invalid date/amount formats.
*   `InvalidOperation` - Handled during decimal conversion.
