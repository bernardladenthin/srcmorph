### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:11:57Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> This package provides backup rotation and monthly revenue aggregation tools for directory and CSV order data.

#### Purpose
- Rotate and prune dated backups of specified directories
- Aggregate monthly revenue per region from CSV order exports with currency and threshold filtering

#### Responsibilities
- Backup rotation: creates dated tar.gz archives and retains only the newest KEEP copies
- Revenue reporting: parses CSV order data and aggregates revenue by month and region

#### Key units
- Order dataclass — represents frozen order data with order_date, region, and amount
- parse_orders generator — yields orders filtered by currency from CSV paths
- revenue_by_month_and_region aggregator — totals revenue by (month, region) from order sequences
- rotate_backups.sh script — creates dated backups and prunes archives beyond KEEP count

#### Data flow
- Backup rotation: SOURCE_DIR → tar.gz archive → BACKUP_DIR → prune oldest archives → stdout status
- Revenue reporting: CSV files → parse_orders (currency filter) → Order dataclasses → revenue_by_month_and_region accumulation → sorted output to stdout/stderr

#### Dependencies
- rotate_backups.sh: tar, ls, mapfile, du, basename, dirname, date, mv, rm, set -euo pipefail
- sales_report.py: argparse, csv, sys, collections, dataclasses, datetime, decimal

#### Cross-cutting
- Both modules enforce input validation and use exit codes/status streams for error reporting
- rotate_backups.sh uses trap for partial archive cleanup on EXIT; sales_report.py raises ValueError with file path and line number context
- Common pattern of strict error handling and status output to stdout/stderr
