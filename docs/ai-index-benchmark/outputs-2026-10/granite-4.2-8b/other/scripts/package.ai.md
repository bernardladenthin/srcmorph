### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T11:13:21Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Creates timestamped compressed backups of source directories while automatically pruning old archives, and generates monthly regional revenue reports from CSV order data filtered by currency and minimum revenue.

#### Purpose
- Backup automation: compresses a source directory into a dated archive and maintains only the newest N backups.
- Reporting: reads CSV order files, filters by currency and minimum revenue, aggregates revenue by month and region, prints results.

#### Responsibilities
- Backup management: directory validation, archive creation, temporary file handling, cleanup of old archives.
- Report generation: CSV parsing, data validation, aggregation by month/region, formatted output printing.

#### Key units
- `rotate_backups.sh`: shell script that backs up a source directory, creates a timestamped tar.gz archive, and deletes archives older than the keep count.
- `sales_report.py`: Python script with `Order` dataclass, `parse_orders`, `revenue_by_month_and_region`, and `main` CLI entry point that filters by currency/min revenue and prints revenue per month/region.
- `Order` (dataclass in `sales_report.py`): represents a parsed order row with `order_date`, `region`, `amount`, `currency`.

#### Data flow
- `rotate_backups.sh`: receives `source_dir`, `backup_dir`, optional `keep`; validates inputs; creates temporary archive; moves to final name; lists archives; deletes old ones.
- `sales_report.py`: receives list of CSV file paths via `main`; `parse_orders` filters by `currency`; `revenue_by_month_and_region` aggregates `amount` by `(month, region)`; `main` prints results or exits `1` if no orders found.

#### Dependencies
- `rotate_backups.sh`: relies on Unix utilities (`date`, `mkdir`, `tar`, `ls`, `mv`, `rm`, `echo`, `printf`) and Bash built-ins (`set`, `trap`, parameter expansion).
- `sales_report.py`: uses only Python standard library modules (`argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime.date`, `decimal`).

#### Cross-cutting
- Both scripts enforce strict input validation: `rotate_backups.sh` checks `KEEP` is a positive integer regex `^[1-9][0-9]*$` and `SOURCE_DIR` exists; `sales_report.py` validates required CSV columns and raises `ValueError` on parse errors.
- `rotate_backups.sh` uses `set -e`, `set -u`, `pipefail`, and a `trap` to clean up temporary files; `sales_report.py` relies on standard error propagation (no caught exceptions for data issues).
- Output is stdout-only for both: `rotate_backups.sh` prints messages like "created archive size", "removed archive"; `sales_report.py` prints tab-separated `(month, region, amount)` lines or "no orders found" to stderr on failure.
- Timestamp format is consistent UTC `%Y%m%dT%H%M%SZ` for backup archives; month string in reports uses `YYYY-MM` format.

> Note: The placeholder `> <one-sentence domain essence>` in `sales_report.py` summary was omitted from synthesis since it was empty/unfilled in the provided text; the actual domain essence was derived from the script's description: "Calculates monthly revenue per region from CSV order exports, filtering by currency and minimum revenue threshold." This aligns with the synthesized lead.
