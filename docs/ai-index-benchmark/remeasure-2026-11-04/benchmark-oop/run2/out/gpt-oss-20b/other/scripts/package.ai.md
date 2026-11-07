### other/scripts
- H: 1.0
- C: 88852564
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:42:20Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A utility package that provides a shell script for rotating directory backups and a Python tool for aggregating monthly sales data from CSV exports.

#### Purpose
- Automate backup creation and retention for a source directory.
- Generate revenue reports by month and region from order CSV files.

#### Responsibilities
- **Backup rotation**: timestamped archive generation, cleanup of old archives.
- **Sales reporting**: CSV parsing, currency filtering, monthly‑region aggregation, threshold filtering.

#### Key units
- `rotate_backups.sh` – Bash script that creates a `.tar.gz` backup and prunes older ones.
- `sales_report.py` – Python script exposing `parse_orders`, `revenue_by_month_and_region`, and `main` for CLI usage.

#### Data flow
1. **Backup**: `SOURCE_DIR` → `tar` → temporary archive → final archive in `BACKUP_DIR`; old archives listed and removed beyond `KEEP`.
2. **Sales report**: CSV files → `parse_orders` → `Order` objects → `revenue_by_month_and_region` → aggregated totals → printed lines filtered by `--min-revenue`.

#### Dependencies
- Bash: `tar`, `date`, `du`, `ls`, `rm`, `mv`, `mapfile`.
- Python: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- Strict error handling (`set -euo pipefail` in Bash; `ValueError` for CSV issues in Python).
- Use of UTC timestamps for backup naming.
- Consistent output formatting: tab‑separated lines for reports, clear console messages for backup status.
