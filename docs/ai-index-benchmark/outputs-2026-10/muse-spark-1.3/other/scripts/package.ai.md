### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:18:27Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Standalone operational scripts for filesystem backup rotation and monthly regional sales aggregation.
#### Purpose
- Miscellaneous CLI utilities with no shared pipeline: dated backup archiving and CSV sales reporting
#### Responsibilities
- Backup rotation: timestamped tar.gz archiving of SOURCE_DIR into BACKUP_DIR with KEEP-count retention, atomic publish via .partial temp
- Sales reporting: monthly revenue aggregation per region from order CSV exports filtered by currency and min-revenue
#### Key units
- rotate_backups.sh: Bash executable creating BACKUP_DIR/<basename>_YYYYMMDDTHHMMSSZ.tar.gz, enforcing newest-first ls -1t retention
- sales_report.py: Python CLI module for revenue_by_month_and_region reporting
- Order(order_date, region, amount): frozen dataclass immutable sales order record
- parse_orders(path, currency): yields filtered currency Orders via csv.DictReader
- revenue_by_month_and_region(orders): sums Order.amount per YYYY-MM month-region via defaultdict Decimal
- main(argv): parses csv_files, --currency, --min-revenue, prints tab-separated report
#### Dependencies
- No internal collaboration between scripts
- Shell tools: bash, tar, date, mkdir, mv, ls, rm, du, cut, basename, dirname, trap
- Python stdlib: argparse, csv, sys, collections.defaultdict, dataclasses.dataclass, datetime.date, decimal.Decimal
#### Cross-cutting
- CLI-first pattern: positional/flag args, stdout reporting, non-zero exits on error
- Validation and error handling: KEEP regex ^[1-9][0-9]*$, SOURCE_DIR directory check, REQUIRED_COLUMNS check, ValueError path:line_no wrapping
- Safe I/O: set -euo pipefail + EXIT trap cleanup, UTF-8 CSV parsing, Decimal monetary arithmetic
