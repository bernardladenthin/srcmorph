### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:08:10Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides operational tools for rotating dated backups and producing currency-specific monthly revenue reports from order exports.

#### Purpose
- Create compressed directory backups with configurable retention.
- Aggregate order CSV data into monthly regional revenue totals for a selected currency.

#### Responsibilities
- **Backup rotation:** Validate source and retention settings, create a timestamped gzip archive, and prune older archives for that source.
- **Sales reporting:** Validate CSV headers, select orders by currency, aggregate revenue by month and region, and apply an optional minimum-revenue threshold.

#### Key units
- `rotate_backups.sh` — creates a `.tar.gz` archive using a temporary `.partial` path, then retains the newest configured number of matching archives.
- `Order` — immutable sales record containing `order_date`, `region`, and `amount`.
- `parse_orders(path, currency)` — yields valid orders matching the selected currency from a CSV file.
- `revenue_by_month_and_region(orders)` — returns sorted totals grouped by `YYYY-MM` and region.
- `main(argv=None)` — parses report options, gathers orders, and prints qualifying totals.

#### Data flow
- Backup: source directory and backup destination → validated settings → UTC-dated archive → rename from partial path → prune older matching archives.
- Reporting: CSV paths and currency option → header and row validation → matching `Order` records → monthly regional totals → minimum-revenue filtering → tab-separated output.

#### Dependencies
- Backup script uses shell utilities including `tar`, `date`, `mkdir`, `mv`, `du`, `ls`, and `rm`; reads source contents and matching archives in the destination.
- Sales report uses Python standard-library CSV, date, decimal, argument-parsing, and aggregation modules.

#### Cross-cutting
- Both scripts are command-line tools with explicit inputs and status-based failure behavior.
- Backup failures use strict Bash mode and an `EXIT` trap to remove the temporary archive.
- Sales data uses `Decimal` for amounts; invalid matching-row dates or amounts raise file- and line-specific `ValueError`s.
