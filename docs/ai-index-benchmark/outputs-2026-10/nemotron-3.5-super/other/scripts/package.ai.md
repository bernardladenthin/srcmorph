### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:56:42Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> The package provides standalone automation utilities for creating and pruning dated directory backups and for generating monthly regional sales revenue reports from CSV order exports.

#### Purpose
- Compress a source directory into a timestamped tar.gz archive and retain only the newest N copies.
- Parse CSV order exports, filter by currency, and print monthly revenue per region above a threshold.

#### Responsibilities
- rotate_backups.sh: archive creation, retention-based pruning, partial-file cleanup on failure.
- sales_report.py: CSV ingestion, order validation, monthly-region aggregation, formatted CLI report.

#### Key units
- rotate_backups.sh: Bash script accepting SOURCE_DIR, BACKUP_DIR, and optional KEEP; exits 0 on success or 2 on invalid input; uses an EXIT trap to remove .partial archives.
- sales_report.py: frozen dataclass Order representing a validated order; parse_orders(path, currency) yields Orders from CSV; revenue_by_month_and_region(orders) returns sorted (year-month, region) to Decimal totals; main(argv=None) drives argument parsing, aggregation, and stdout output.

#### Data flow
- rotate_backups.sh: SOURCE_DIR is tar-compressed to a .partial staging file in BACKUP_DIR, renamed to a dated final archive, then older archives beyond KEEP are deleted via ls -1t ordering.
- sales_report.py: CSV files are read with csv.DictReader into Order objects, accumulated by (YYYY-MM, region) in a defaultdict(Decimal), filtered by --min-revenue, and emitted as tab-separated month/region/revenue rows.

#### Dependencies
- rotate_backups.sh relies on bash, tar, gzip, date, ls, mapfile, basename, dirname, mv, rm, mkdir, and trap.
- sales_report.py relies on argparse, csv, sys, collections.defaultdict, dataclasses.dataclass, datetime.date, decimal.Decimal, and decimal.InvalidOperation.
- The two scripts are independent; no internal collaboration between them.

#### Cross-cutting
- Both are procedural CLI utilities with explicit argument validation and non-zero exit codes on failure.
- rotate_backups.sh uses set -euo pipefail and an EXIT trap; sales_report.py raises ValueError on bad CSV columns or unparseable dates/amounts and returns exit 1 when no orders exist.
- Both write operational messages to stdout and errors to stderr.
