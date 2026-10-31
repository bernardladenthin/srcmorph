### sales_report.py
- H: 1.0
- C: 34684FDC
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T11:00:13Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
> <one-sentence domain essence>

> Calculates monthly revenue per region from CSV order exports, filtering by currency and minimum revenue threshold.

#### Purpose
- Reads one or more CSV files containing order data.
- Filters rows by specified currency and optionally by minimum revenue.
- Aggregates amount by month (YYYY-MM) and region.
- Prints formatted results to stdout.

#### Type
Python script (header + implementation); uses dataclass `Order`, functions, no classes with inheritance.

#### Input
- `csv_files`: list of file paths (positional args).
- `currency`: string (default `"EUR"`), used to filter rows.
- `min_revenue`: Decimal (default `0`), rows with revenue below this are excluded.
- CSV content: columns `order_date`, `region`, `amount`, `currency` (required); other columns ignored.

#### Output
- Prints each matching `(month, region)` pair with revenue formatted as `YYYY-MM\tregion\tamount:two-decimal {currency}` to stdout.
- Returns exit code `0` on success, `1` if no orders are found.

#### Core logic
- Validates CSV has required columns; raises `ValueError` if missing.
- Parses each row: skips rows with currency mismatch; converts `order_date` to `date`, `amount` to `Decimal`; strips `region`; yields `Order` instance or raises `ValueError` on parse error.
- Computes totals using `defaultdict(Decimal)` keyed by `(month_string, region)`.
- Filters results by `min_revenue` before printing.
- Prints sorted results (by key due to `sorted`).

#### Public API
- `parse_orders(path: str, currency: str) -> Iterator[Order]` – yields `Order` objects for rows matching currency; raises `ValueError` on missing columns or parse errors.
- `revenue_by_month_and_region(orders: Iterable[Order]) -> Dict[Tuple[str, str], Decimal]` – returns dict mapping `(month, region)` to summed `amount`.
- `main(argv: Optional[List[str]]) -> int` – parses CLI args, processes files, prints results, returns exit code.

#### Dependencies
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- No external modules beyond Python standard library.

#### Exceptions / Errors
- `ValueError` raised by `parse_orders` for missing required columns or invalid `order_date`/`amount` parsing; includes file path and line number.
- `main` prints `"no orders found"` to stderr and returns `1` when no orders match filters.
- No other exceptions are caught or handled; errors propagate to CLI exit.
