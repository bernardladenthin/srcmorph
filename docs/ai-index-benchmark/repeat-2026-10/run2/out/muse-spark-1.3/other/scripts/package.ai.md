### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:19:16Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Standalone operational utilities for dated backup rotation and monthly regional sales revenue reporting.
#### Purpose
- Collection of independent admin/reporting scripts with no shared runtime
- Backup maintenance and CSV sales aggregation via CLI invocation
#### Responsibilities
- Backup rotation: timestamped `tar.gz` archiving of `SOURCE_DIR` into `BACKUP_DIR` with newest-N `KEEP` retention
- Sales reporting: CSV order ingestion and `YYYY-MM` x `region` revenue aggregation with `currency` and `min-revenue` filtering
#### Key units
- `rotate_backups.sh(source_dir, backup_dir, [keep])` — creates `{basename}_YYYYMMDDTHHMMSSZ.tar.gz`, enforces retention
- `Order` frozen dataclass — `order_date`, `region`, `amount` typed order record
- `parse_orders(path, currency)` — `csv.DictReader` iterator validating `REQUIRED_COLUMNS`, `order_date`, `amount`
- `revenue_by_month_and_region(orders)` — `defaultdict(Decimal)` grouping by `strftime("%Y-%m")` and `region`
- `main(argv)` — aggregates `csv_files`, prints TSV `YYYY-MM region revenue currency`
#### Dependencies
- No internal cross-file collaboration; independently executable
- Bash side: `bash set -euo pipefail`, `tar -czf`, `mapfile`, `trap`, GNU coreutils `basename`, `dirname`, `date -u`, `ls -1t`, `du -h`, `mkdir -p`, `mv`, `rm`
- Python side: stdlib only `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`
#### Cross-cutting
- CLI-driven scripts with positional args / `argparse` defaults (`KEEP` 7, `--currency` EUR, `--min-revenue` 0)
- Strict input validation: `KEEP` regex `^[1-9][0-9]*$`, `SOURCE_DIR` existence, `REQUIRED_COLUMNS`, `date.fromisoformat`, `Decimal`
- Exit-code error signaling: exit 2 invalid keep/missing directory, exit 1 `no orders found`, `ValueError` with path:line_no
- Safe output handling: `.partial` + `mv` atomic publish with `trap EXIT` cleanup, TSV stdout + stderr status lines
