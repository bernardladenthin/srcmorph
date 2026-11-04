### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:55:30Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Provides utilities for backing up directories and generating monthly sales reports from CSV order data.

#### Purpose
- Automate creation and rotation of compressed backups.
- Produce aggregated revenue reports by month and region.

#### Responsibilities
- **Backup Management** – `rotate_backups.sh` handles directory snapshotting, timestamped archiving, and retention policy.
- **Sales Reporting** – `sales_report.py` parses order exports, filters by currency, aggregates revenue, and outputs tab‑separated summaries.

#### Key units
- **`rotate_backups.sh`** – Bash script that creates `tar.gz` archives, names them with UTC timestamps, and prunes older files beyond a configurable keep count.
- **`sales_report.py`** – Python module exposing:
  - `parse_orders(path, currency)` – yields `Order` dataclass instances from CSV.
  - `revenue_by_month_and_region(orders)` – returns a sorted dict of `(YYYY‑MM, region) → Decimal` totals.
  - `main(argv=None)` – CLI entry point that orchestrates parsing, aggregation, and output.

#### Data flow
1. **Backup**: User supplies source and backup directories; script validates, archives, and cleans old backups.
2. **Reporting**: User supplies CSV files and options; script parses each file, filters by currency, aggregates revenue per month/region, then prints results.

#### Dependencies
- **Shell**: `tar`, `date`, `du`, `ls`, `mkdir`, `rm`, `mv`, `echo`.
- **Python**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- Both utilities enforce strict input validation and provide clear exit codes (`0` success, `1/2` errors).
- Error handling uses standard exceptions (`ValueError`, `InvalidOperation`) and shell traps to clean temporary files.
- Configuration is minimal and explicit: backup keep count defaults to 7; report currency defaults to EUR.
