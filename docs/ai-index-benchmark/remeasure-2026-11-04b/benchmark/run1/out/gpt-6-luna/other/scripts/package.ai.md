### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:50:37Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides command-line utilities for maintaining timestamped backup archives and summarizing order revenue by month and region.

#### Purpose
- Create compressed backups with configurable retention, and aggregate CSV orders into currency-specific monthly regional revenue reports.
- Expose both tasks as standalone scripts with command-line inputs and stdout/stderr output.

#### Responsibilities
- **Backup rotation:** `rotate_backups.sh` validates a source and retention count, creates a timestamped `.tar.gz` archive, and removes older matching archives beyond the configured limit.
- **Sales reporting:** `sales_report.py` parses order CSVs, selects a currency, aggregates revenue by month and region, and filters totals by minimum revenue.

#### Key units
- `rotate_backups.sh` — Bash utility for archive creation, atomic rename from a `.partial` file, and retention pruning.
- `Order` — immutable sales record with order date, region, and decimal amount.
- `parse_orders(path, currency)` — validates CSV columns and yields matching-currency orders.
- `revenue_by_month_and_region(orders)` — returns sorted monthly regional revenue totals.
- `main(argv=None)` — parses report options, aggregates input files, and prints qualifying totals.

#### Data flow
- Backup: source directory and retention count → compressed UTC-timestamped archive → newest-first archive listing → deletion of entries beyond `KEEP`.
- Sales: CSV files → validated, currency-filtered `Order` records → monthly and regional totals → minimum-revenue filter → tab-separated report.

#### Dependencies
- Backup script uses Bash facilities and external commands including `tar`, `date`, `mv`, `du`, `ls`, and `rm`.
- Sales report uses Python standard-library CSV, argument parsing, date, and decimal utilities.

#### Cross-cutting
- Both are standalone command-line tools with positional or option-based inputs and explicit status/output behavior.
- Backup cleanup uses an EXIT trap for unfinished `.partial` archives; the report raises contextual `ValueError`s for missing columns or invalid date and amount values.
