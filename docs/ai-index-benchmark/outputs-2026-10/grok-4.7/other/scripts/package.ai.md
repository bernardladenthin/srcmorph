### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:46:30Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
I will produce a dense structured markdown summary of the package based on the per-file summaries.

The package contains two operational CLI scripts. rotate_backups.sh handles backup rotation. sales_report.py aggregates order CSV exports into monthly regional revenue reports.> Operational CLI scripts that rotate dated directory backups and aggregate order CSV exports into monthly regional revenue totals.

#### Purpose
- Miscellaneous operator scripts for backup retention and sales reporting, not a shared library.
- Each script is a standalone CLI with its own inputs, stdout, and exit status.

#### Responsibilities
- Backup rotation: create one dated compressed archive of a source directory and prune older archives beyond a keep count (`rotate_backups.sh`).
- Sales reporting: parse order CSV exports, sum revenue by calendar month and region, and print a filtered TSV report (`sales_report.py`).

#### Key units
- `rotate_backups.sh` — CLI that writes `$BACKUP_DIR/${basename}_YYYYMMDDTHHMMSSZ.tar.gz` and deletes older matching archives beyond `KEEP`.
- `Order` — frozen dataclass for `order_date`, `region`, and `amount`.
- `parse_orders` — yields matching-currency `Order` rows from a CSV path.
- `revenue_by_month_and_region` — sums amounts keyed by `("%Y-%m", region)`.
- `main` — CLI entry that prints the report and returns the process status.
- `REQUIRED_COLUMNS` — required CSV names `order_date`, `region`, `amount`, `currency`.

#### Data flow
- Backup: `SOURCE_DIR` is archived with `tar -C` into a `.partial` file, `mv` to the final `.tar.gz`, then newest-first `ls -1t` of `${name}_*.tar.gz` drops entries after index `KEEP`.
- Sales: CSV paths go through `parse_orders` (currency filter), totals are grouped in `revenue_by_month_and_region`, groups below `--min-revenue` are skipped, and remaining lines print as `YYYY-MM`, region, two-decimal revenue, and currency.

#### Dependencies
- `rotate_backups.sh`: `bash`, `tar`, `date`, `du`, `cut`, `ls`, `mkdir`, `mv`, `rm`, `basename`, `dirname`, `mapfile`.
- `sales_report.py`: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- No collaboration between the two scripts.

#### Cross-cutting
- Both are CLIs that write progress or report lines to stdout and errors to stderr, and use non-zero exits for bad inputs or empty results.
- Backup script uses `set -euo pipefail` and an `EXIT` trap to remove a partial archive; sales script raises `ValueError` (with path and line) on missing columns or bad date/amount.
