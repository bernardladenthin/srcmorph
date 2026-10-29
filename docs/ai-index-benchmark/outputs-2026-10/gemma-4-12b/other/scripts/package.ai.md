### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T04:35:37Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> This package provides administrative and reporting utilities for automated data maintenance and financial analysis.

#### Purpose
* Automates system maintenance tasks like directory backups.
* Processes raw CSV data to generate aggregated financial reports.

#### Responsibilities
* **Backup Management**: Handles directory archiving, timestamped file generation, and retention policy enforcement via shell scripts.
* **Sales Analytics**: Parses order data, filters by currency, and aggregates revenue by month and region using Python.

#### Key units
* `rotate_backups.sh`: Manages `.tar.gz` archive creation and pruning of old backups based on a `KEEP` count.
* `sales_report.py`: Provides a CLI to transform CSV order exports into tab-separated revenue summaries.
* `Order` (dataclass): Represents a validated order record with date, region, amount, and currency.

#### Data flow
* **Backup Pipeline**: `SOURCE_DIR` $\rightarrow$ `tar` (partial file) $\rightarrow$ Atomic Move $\rightarrow$ Retention Pruning.
* **Reporting Pipeline**: CSV Files $\rightarrow$ `parse_orders` (Generator) $\rightarrow$ `revenue_by_month_and_region` (Aggregation) $\rightarrow$ CLI Output.

#### Dependencies
* **System Utilities**: `tar`, `date`, `du`, `ls`, `mkdir`.
* **Python Standard Library**: `argparse`, `csv`, `decimal`, `datetime`, `collections.defaultdict`.

#### Cross-cutting
* **Error Handling**: Strict exit codes (e.g., status 2 for script errors, status 1 for empty reports) and `set -euo pipefail` for shell robustness.
* **Data Validation**: Consistent validation of input formats (ISO dates, positive integers, required CSV headers).
