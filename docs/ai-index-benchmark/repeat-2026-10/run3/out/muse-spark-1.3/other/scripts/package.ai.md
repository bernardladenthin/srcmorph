### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:25:29Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Operational utility scripts for dated backup rotation and monthly regional revenue reporting.
#### Purpose
- Standalone ops/reporting helpers: filesystem backup archiving with retention, CSV order revenue aggregation.

#### Responsibilities
- Backup rotation: timestamped `tar -czf` archives per `SOURCE_DIR` basename, `KEEP` newest-N pruning.
- Sales reporting: CSV order ingestion, currency filtering, month-region revenue totals with thresholded STDOUT report.

#### Key units
- `rotate_backups.sh(source_dir, backup_dir, [keep])` — creates `$BACKUP_DIR/<name>_YYYYMMDDTHHMMSSZ.tar.gz`, prunes excess.
- `Order(order_date, region, amount)` — frozen dataclass immutable sales order record.
- `parse_orders(path, currency)` — yields matching-currency `Order` from `order_date, region, amount, currency` CSV.
- `revenue_by_month_and_region(orders)` — `dict[(month, region)] -> Decimal` grouped by `%Y-%m`.
- `main(argv)` — parses `--currency`, `--min-revenue`, prints `YYYY-MM region revenue currency` lines.

#### Dependencies
- No internal collaboration; independent CLI utilities.
- Bash `set -euo pipefail`, `tar`, `date -u`, `ls -1t`, `du -h`, `mkdir`, `mv`, `rm`, `basename`, `dirname`, `mapfile`, `trap`.
- Python stdlib: `argparse`, `csv.DictReader`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `sys`.

#### Cross-cutting
- Standalone CLI executables with positional/flag args and defaults (`KEEP` default 7, `--currency` default EUR, `--min-revenue` default 0).
- Fail-fast validation: `KEEP` regex `^[1-9][0-9]*$` + `SOURCE_DIR` existence check exit 2; `REQUIRED_COLUMNS`, `date.fromisoformat`, `Decimal` parsing `ValueError`.
- Partial/failure hygiene: `$archive.partial` + `trap` cleanup and atomic `mv`; `no orders found` STDERR exit 1 on empty.
