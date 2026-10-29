### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T05:13:47Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a monthly sales report from CSV order exports, showing revenue per month and region.

#### Purpose
- Reads CSV files containing order data.
- Calculates total revenue per month and region.
- Outputs the results in a tabular format.

#### Type
- Python script (`.py`).
- No classes or inheritance.
- Uses `dataclass` for `Order` and `defaultdict` for aggregation.

#### Input
- CSV files specified as command-line arguments (`csv_files`).
- Optional currency filter (`--currency`, default: "EUR").
- Minimum revenue threshold (`--min-revenue`, default: 0).

#### Output
- Prints revenue per month and region to standard output.
- Format: `YYYY-MM	region	revenue currency`.
- Skips regions with revenue below the specified minimum.

#### Core logic
- Parses CSV files to extract orders, filtering by currency.
- Aggregates revenue by month and region using `defaultdict`.
- Sorts and filters the aggregated data before printing.

#### Public API
- `main(argv=None) -> int`: Entry point of the script, processes arguments and generates the report.

#### Dependencies
- `argparse`: Parses command-line arguments.
- `csv`: Reads CSV files.
- `sys`: Handles standard input/output.
- `collections.defaultdict`: Aggregates revenue data.
- `dataclasses.dataclass`: Defines the `Order` data structure.
- `datetime.date`: Parses and formats dates.
- `decimal.Decimal`: Handles monetary values accurately.

#### Exceptions / Errors
- `ValueError`: Raised for missing columns, invalid dates, or non-decimal amounts in CSV files.
