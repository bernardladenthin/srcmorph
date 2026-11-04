### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:59:45Z
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
- `ksh93` shell.
- Uses `set -e`? none; uses `typeset -i` and `until` loop.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → sets `business_day`.
  - `-o <dir>` → sets `export_dir`.
- Environment variables:
  - `ORACLE_SID` (default `PAYPRD`).
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`.
- No positional arguments.

#### Output
- Creates `$export_dir/payments_$business_day.csv`.
- Generates control file `$export_dir/payments_$business_day.ctl` with row count.
- Gzips the CSV to `$export_dir/payments_$business_day.csv.gz`.
- Prints log messages to stderr.
- Exit status: `0` on success, `1` on failure, `3` if required tool missing, `4` if retries exhausted.

#### Core logic
- Parse options; set defaults.
- Ensure export directory exists.
- Attempt export up to `MAX_RETRIES` times:
  - Run `run_export` to spool SQL query output to CSV.
  - If successful, write control file, gzip CSV, log success, exit.
  - On failure, log, sleep 60, retry.
- On exhausting retries, call `fail`.

#### Functions
- `log` – prints timestamped message to stderr.
- `fail` – logs failure and exits with code.
- `require_tools` – checks for `sqlplus` and `gzip`.
- `run_export` – runs SQL query via `sqlplus` and writes to file.
- `write_control` – counts rows in CSV and writes control file.

#### Constants and configuration
- ``ORACLE_SID_DEFAULT=PAYPRD``
- ``EXPORT_DIR_DEFAULT=/data/export/payments``
- ``MAX_RETRIES=3``
- ``attempt=0``
- ``business_day=$(date +%Y%m%d)``
- ``export_dir=$EXPORT_DIR_DEFAULT``

#### Dependencies
- External commands: `sqlplus`, `gzip`.
- Oracle database connection via `/ AS SYSDBA`.
- SQL query selects from `settled_payment` table.

#### Errors and exit codes
- `fail` with code `3` if `sqlplus` or `gzip` missing.
- `fail` with code `4` after exceeding `MAX_RETRIES`.
- `fail` with code `2` on usage error.
- `exit 0` on successful export.
