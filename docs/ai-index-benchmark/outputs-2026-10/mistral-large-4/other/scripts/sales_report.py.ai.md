### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:46:27Z
- G: 
- A: 0.0.0
- X: file
---
> Reads order CSV exports and prints monthly revenue aggregated by region, filtered by currency and minimum revenue.

#### Purpose
- CLI tool aggregating order CSV files into monthly revenue totals per region
- Filters orders by target currency and minimum revenue threshold before reporting

#### Type
- Python script (module) with `__main__` entry point
- Frozen dataclass `Order` representing a single order row
- Module-level functions `parse_orders`, `revenue_by_month_and_region`, `main`

#### Input
- Positional CLI args: one or more CSV file paths (`csv_files`)
- `--currency` option (default `"EUR"`) to select order currency
- `--min-revenue` option (`Decimal`, default `0`) to filter output rows
- CSV columns: `order_date` (ISO 8601), `region` (string), `amount` (decimal), `currency` (string)

#### Output
- Stdout: tab-separated lines `YYYY-MM\tregion\trevenue.2f currency` for qualifying (month, region) pairs
- Stderr: `no orders found` when currency filter yields empty result
- Process exit code: `0` on success, `1` when no orders found

#### Core logic
- `parse_orders` validates CSV headers against `REQUIRED_COLUMNS`, streams rows via `csv.DictReader`, skips non-matching currencies, parses `order_date` with `date.fromisoformat` and `amount` with `Decimal` into `Order` objects
- `revenue_by_month_and_region` accumulates `Order.amount` into `defaultdict(Decimal)` keyed by `(order_date.strftime("%Y-%m"), region)`, returns sorted dict
- `main` chains `parse_orders` across all input paths, aggregates totals, filters by `min_revenue`, prints formatted revenue lines

#### Public API
- `Order(order_date: date, region: str, amount: Decimal) -> Order` frozen dataclass for one order row
- `parse_orders(path, currency) -> Iterator[Order]` yielding currency-filtered orders from CSV
- `revenue_by_month_and_region(orders) -> dict` aggregating revenue by month and region
- `main(argv=None) -> int` CLI entry point returning exit code

#### Dependencies
- Standard library: `argparse`, `csv`, `sys`
- `collections.defaultdict`
- `dataclasses.dataclass`
- `datetime.date`
- `decimal.Decimal`, `decimal.InvalidOperation`

#### Exceptions / Errors
- `ValueError` raised when CSV file missing required columns (`order_date`, `region`, `amount`, `currency`)
- `ValueError` raised with `path:line_no` context when `date.fromisoformat` or `Decimal` conversion fails
- Exit code `1` (not exception) when no orders match the currency filter
