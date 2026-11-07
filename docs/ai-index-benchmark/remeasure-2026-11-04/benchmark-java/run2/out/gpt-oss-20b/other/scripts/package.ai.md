### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:34:42Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides utilities for backing up directories and generating monthly revenue reports from order CSVs.

#### Purpose
- Automate directory backups with retention policy.
- Produce revenue summaries per month and region from CSV order exports.

#### Responsibilities
- **Backup Management** – create timestamped tar.gz archives, prune old backups.
- **Data Processing** – parse CSV orders, filter by currency, aggregate revenue, output reports.

#### Key units
- `rotate_backups.sh` – Bash script that creates a compressed backup of a source directory, keeps only the newest N archives, and cleans up old ones.
- `sales_report.py` – Python module exposing:
  - `parse_orders(path, currency)` – yields `Order` dataclass instances from a CSV.
  - `revenue_by_month_and_region(orders)` – aggregates revenue by `(YYYY‑MM, region)`.
  - `main(argv)` – CLI entry point that parses arguments, collects orders, and prints a tab‑separated report.

#### Data flow
1. **Backup**: `rotate_backups.sh` receives source and backup directories, validates inputs, creates a tarball named with UTC timestamp, moves it to the backup directory, lists existing archives, and deletes those beyond the configured keep count.
2. **Reporting**: `sales_report.py` reads CSV files supplied on the command line, parses each row into an `Order`, filters by the specified currency, aggregates amounts by month and region, then prints rows that meet the minimum revenue threshold.

#### Dependencies
- **Shell**: `tar`, `date`, `du`, `ls`, `mkdir`, `mv`, `rm`.
- **Python**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Cross-cutting
- Both utilities enforce strict input validation and provide clear exit codes (`0` success, non‑zero for errors).
- Error handling is explicit: missing arguments, invalid data, or missing columns raise informative exceptions or exit codes.
- Configuration is minimal and exposed via command‑line arguments (`KEEP`, `--currency`, `--min-revenue`).
- Output is human‑readable: backup script prints creation/removal messages; report script outputs tab‑separated lines.
