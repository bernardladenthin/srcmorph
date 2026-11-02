### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:03:46Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A utility package that provides a shell script for rotating directory backups and a Python tool for summarizing monthly sales revenue from CSV exports.

#### Purpose
- Automate backup rotation for arbitrary directories.
- Generate revenue reports per month and region from order CSVs.

#### Responsibilities
- **Backup Management** – create timestamped tarballs, enforce retention policy, clean old archives.
- **Sales Reporting** – parse CSV orders, filter by currency, aggregate revenue, output tab‑separated summaries.

#### Key units
- `rotate_backups.sh` – Bash script; creates `*.tar.gz` archives, deletes older ones beyond a keep count.
- `sales_report.py` – Python module; exposes `parse_orders`, `revenue_by_month_and_region`, and `main` for CLI usage.

#### Data flow
1. **Backup**: `rotate_backups.sh` receives source and backup directories, optional keep count → validates inputs → creates a compressed archive → moves to backup dir → lists existing archives → removes those beyond the keep threshold.
2. **Reporting**: `sales_report.py` reads one or more CSV files → `parse_orders` yields `Order` objects filtered by currency → `revenue_by_month_and_region` aggregates amounts by `(YYYY-MM, region)` → `main` prints rows meeting `min_revenue`.

#### Dependencies
- **Shell**: `tar`, `date`, `du`, `ls`, `rm`, `mv`, `mkdir`, `basename`, `dirname`.
- **Python**: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- Strict error handling: shell script uses `set -euo pipefail` and traps; Python raises `ValueError` for malformed data.
- Both utilities operate via command line, accept positional arguments, and exit with meaningful status codes.
