### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:05:51Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Extracts settled payments for a specified business day from Oracle, writes a CSV, creates a control file, and gzips the result.

#### Purpose
- Performs nightly export of settled payments.
- Runs as a standalone script.

#### Type
- `ksh93` with `set -e`? none; uses `typeset` and `whence`.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → sets `business_day`.
  - `-o <dir>` → sets `export_dir`.
- Environment variables:
  - `ORACLE_SID` (default `PAYPRD`).
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`.
- No positional arguments.

#### Output
- CSV file: `${export_dir}/payments_${business_day}.csv`.
- Control file: `${export_dir}/payments_${business_day}.ctl` with `rows=` and `business_day=`.
- Gzipped CSV: `${export_dir}/payments_${business_day}.csv.gz`.
- Logs to stderr.

#### Core logic
- Parse options; set defaults.
- Ensure `export_dir` exists.
- Attempt export up to `MAX_RETRIES`:
  - Run `sqlplus` to spool query result to CSV.
  - If successful, write control file, gzip CSV, log success, exit.
  - On failure, log, sleep 60, retry.
- On exhausting retries, exit with code 4.

#### Functions
- `log` – prints timestamped message to stderr.
- `fail` – logs failure and exits with code.
- `require_tools` – checks for `sqlplus` and `gzip`.
- `run_export` – runs SQL query via `sqlplus`, writes to given file.
- `write_control` – counts rows in CSV, writes control file.

#### Constants and configuration
- ``ORACLE_SID_DEFAULT=PAYPRD``
- ``EXPORT_DIR_DEFAULT=/data/export/payments``
- ``MAX_RETRIES=3``
- ``attempt=0``
- ``business_day=$(date +%Y%m%d)``
- ``export_dir=$EXPORT_DIR_DEFAULT``

#### Dependencies
- External commands: `sqlplus`, `gzip`.
- Oracle connection: `CONNECT / AS SYSDBA` via `sqlplus`.

#### Errors and exit codes
- `fail` exits with code 1 by default, or specified.
- Missing tools → exit 3.
- Cannot create export directory → exit 1.
- Exceeding retries → exit 4.
