### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:47:44Z
- G: 
- A: 0.0.0
- X: file
---
> Monthly sales reporting from CSV exports to total revenue grouped by month and region in a selected currency.

#### Purpose
- Reads one or more order CSV files and aggregates revenue by month and region.
- Filters by target currency and optionally skips totals below a minimum revenue threshold.

#### Type
- Python module with a frozen dataclass `Order`.
- Public functions: `parse_orders`, `revenue_by_month_and_region`, `main`.
- Key type: `Order(order_date: date, region: str, amount: Decimal)`.

#### Input
- CSV file paths from `argparse` positional `csv_files`.
- `--currency` default `EUR`; `--min-revenue` default `0` as `Decimal`.
- Each CSV row must contain columns: `order_date`, `region`, `amount`, `currency`.
- Reads UTF-8 CSV files via `csv.DictReader`; expects `order_date` ISO format and numeric `amount`.

#### Output
- Prints lines formatted as `YYYY-MM\tregion\tamount currency` for qualifying totals.
- Returns `0` on successful processing, `1` when no orders are found.
- Side effect: writes to stdout; writes "no orders found" to stderr when empty.

#### Core logic
- Defines `REQUIRED_COLUMNS` and validates each input file's header before processing.
- `parse_orders(path, currency)` opens the CSV, checks required columns, and yields `Order` objects for rows matching the target currency.
- Converts `order_date` with `date.fromisoformat`, trims `region`, and parses `amount` via `Decimal`.
- Raises `ValueError` on missing columns or malformed CSV row data.
- `revenue_by_month_and_region(orders)` groups amounts by `(YYYY-MM, region)` using `defaultdict(Decimal)`.
- Sorts the aggregated totals by key and returns a dict.
- `main()` loads all orders from all provided CSV files, exits early with an error if no orders exist, then prints each qualifying month/region total.

#### Public API
- `Order(order_date: date, region: str, amount: Decimal) -> Order` + dataclass for parsed order rows.
- `parse_orders(path, currency) -> Iterator[Order]` + yields valid orders from one CSV file.
- `revenue_by_month_and_region(orders) -> dict` + groups revenue by month and region.
- `main(argv=None) -> int` + CLI entry point for CSV processing and reporting.

#### Dependencies
- `argparse`
- `csv`
- `sys`
- `defaultdict`
- `dataclass`
- `date`
- `Decimal`, `InvalidOperation`

#### Exceptions / Errors
- `ValueError` when a CSV file is missing required columns.
- `ValueError` when an order row has invalid `order_date`, `amount`, or conversion issue; raised with file path and line number.
- CLI exits with status `1` if no orders are found after filtering.
