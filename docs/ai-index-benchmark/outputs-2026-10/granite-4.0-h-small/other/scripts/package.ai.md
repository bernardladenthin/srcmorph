### other/scripts
- H: 1.0
- C: B929B81D
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T05:18:14Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> A Python package for generating sales reports and managing backups.

#### Purpose
- Provides tools for generating monthly sales reports from CSV order data and managing backups of directories.

#### Responsibilities
- **Sales Reporting**: Reads CSV files containing order data, calculates total revenue per month and region, and outputs the results in a tabular format.
- **Backup Management**: Compresses a specified source directory into a dated archive file, maintains a configurable number of the most recent backups, and deletes older backups beyond the specified retention count.

#### Key units
- `sales_report.py`: Generates a monthly sales report from CSV order exports, showing revenue per month and region.
- `rotate_backups.sh`: Creates compressed, dated backups of a directory while retaining only the newest N archives.

#### Data flow
- **Sales Reporting**:
  1. CSV files are read and parsed to extract order data.
  2. Orders are filtered by currency.
  3. Revenue is aggregated by month and region using `defaultdict`.
  4. The aggregated data is sorted and filtered based on the minimum revenue threshold.
  5. The results are printed in a tabular format.
- **Backup Management**:
  1. Input parameters are validated and directories are checked for existence.
  2. A timestamped archive filename is constructed.
  3. The source directory is compressed into a temporary file.
  4. The temporary file is renamed to the final archive name.
  5. Archives in the backup directory are listed and sorted by newest first.
  6. Older archives beyond the specified retention count are deleted.

#### Dependencies
- **Sales Reporting**:
  - `argparse`: Parses command-line arguments.
  - `csv`: Reads CSV files.
  - `sys`: Handles standard input/output.
  - `collections.defaultdict`: Aggregates revenue data.
  - `dataclasses.dataclass`: Defines the `Order` data structure.
  - `datetime.date`: Parses and formats dates.
  - `decimal.Decimal`: Handles monetary values accurately.
- **Backup Management**:
  - `tar`: For creating compressed archive files.
  - `date`: For generating timestamped filenames.
  - `du`: For reporting archive sizes.
  - Standard shell utilities: `basename`, `mkdir`, `ls`, `mv`, `rm`

#### Cross-cutting
- **Error Handling**: Both components handle errors and exceptions gracefully, providing informative error messages and exiting with appropriate status codes.
- **Configuration**: Both components allow for configuration through command-line arguments or script parameters, such as specifying directories, retention counts, and currency filters.
