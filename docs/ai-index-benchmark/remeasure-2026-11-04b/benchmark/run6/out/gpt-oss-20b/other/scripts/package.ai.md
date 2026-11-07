### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:58:15Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A small utility bundle that backs up directories and aggregates sales data from CSV exports.

#### Purpose
- Provide a command‑line backup script that keeps a rolling set of compressed archives.
- Offer a Python tool to generate monthly sales reports by region, filtered by currency and revenue threshold.

#### Responsibilities
- **Backup rotation**: `rotate_backups.sh` creates timestamped tarballs, prunes old ones, and reports actions.
- **Sales reporting**: `sales_report.py` parses CSV orders, aggregates revenue per month/region, and outputs a tab‑separated report.

#### Key units
- **`rotate_backups.sh`** – Bash script; core logic: validate inputs, create `tar.gz` archive, delete excess archives.
- **`sales_report.py`** – Python module; public API: `parse_orders`, `revenue_by_month_and_region`, `main`.
- **`Order` dataclass** – represents a single order record (date, region, amount).

#### Data flow
1. **Backup**: User supplies source and target directories; script validates, archives, and cleans old backups.
2. **Reporting**: User supplies CSV files and optional filters; script parses rows, aggregates totals, and prints results.

#### Dependencies
- **Shell**: `tar`, `date`, `du`, `ls`, `rm`, `mkdir`.
- **Python**: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- Strict error handling: both tools exit with non‑zero status on failure and provide clear messages.
- Input validation: `rotate_backups.sh` checks numeric `KEEP`; `sales_report.py` validates CSV columns and currency.
- Output formatting: tab‑separated lines for the report; human‑readable logs for backup actions.
