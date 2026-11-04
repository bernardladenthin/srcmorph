### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:45:45Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Extracts settled payments for a specified business day from Oracle, writes a CSV, creates a control file, and gzips the result with retry logic.

#### Purpose
- Run nightly to export settled payments.
- Executed directly; not sourced.

#### Type
- `ksh93` shell.
- Uses `set -e`? none; relies on `until` loop and explicit exits.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → `business_day`
  - `-o <dir>` → `export_dir`
- Environment variables:
  - `ORACLE_SID` (default `PAYPRD`)
  - `NLS_LANG=AMERICAN_AMERICA.AL32UTF8`
- No positional arguments.

#### Output
- CSV file: `$export_dir/payments_$business_day.csv`
- Control file: `$export_dir/payments_$business_day.ctl` containing `rows=` and `business_day=`.
- Gzipped CSV: `$export_dir/payments_$business_day.csv.gz`
- Logs to stderr with timestamps.
- Exit status:
  - `0` on success.
  - `1` on generic failure.
  - `2` on usage error.
  - `3` if required tool missing.
  - `4` after exhausting retries.

#### Core logic
- Parse options; set defaults.
- Ensure `export_dir` exists.
- Set `csv` path.
- Retry loop (max 3 attempts):
  - Run `run_export` to spool SQL query output to `$csv`.
  - On success:
    - `write_control` counts rows, writes control file.
    - `gzip -f "$csv"`.
    - Log success and exit.
  - On failure:
    - Log attempt, sleep 60, retry.
- After retries, call `fail` with exit code 4.

#### Functions
- `log` – prepend timestamp, write to stderr.
- `fail` – log failure, exit with code.
- `require_tools` – ensure `sqlplus` and `gzip` are available.
- `run_export` – execute SQL query via `sqlplus`, spool to file.
- `write_control` – count rows, write control file.

#### Constants and configuration
- ``ORACLE_SID_DEFAULT=PAYPRD``
- ``EXPORT_DIR_DEFAULT=/data/export/payments``
- ``MAX_RETRIES=3``
- ``attempt=0``
- ``business_day=$(date +%Y%m%d)``
- ``export_dir=$EXPORT_DIR_DEFAULT``

#### Dependencies
- External commands: `sqlplus`, `gzip`.
- Oracle connection: `CONNECT / AS SYSDBA` (no credentials in script).
- SQL query reads `settled_payment` table, filters by `business_day`.

#### Errors and exit codes
- `fail "missing $tool" 3` – missing tool.
- `fail "cannot create $export_dir"` – directory creation failure.
- `fail "usage: nightly_extract.ksh ..." 2` – invalid options.
- `fail "giving up after $MAX_RETRIES attempts" 4` – retries exhausted.
