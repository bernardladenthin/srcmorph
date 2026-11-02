### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:35:55Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for a business day from Oracle to a gzipped CSV with a row‑count control file.

#### Purpose
- Run nightly to export settled payments.
- Executed directly as a script.

#### Type
- `ksh93`
- `set -e` not used; script relies on explicit checks and `fail`.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → `business_day`
  - `-o <dir>` → `export_dir`
- Environment variables:
  - `ORACLE_SID` (default `PAYPRD`)
  - `NLS_LANG=AMERICAN_AMERICA.AL32UTF8`
- Files read:
  - Oracle database via `sqlplus`.

#### Output
- Creates `$export_dir/payments_$business_day.csv` (CSV).
- Creates `$export_dir/payments_$business_day.ctl` (control file with row count).
- Gzips the CSV to `$export_dir/payments_$business_day.csv.gz`.
- Prints log messages to stderr.
- Exit status: `0` on success, `1` on failure, `2` on usage error, `3` on missing tool, `4` on retry exhaustion.

#### Core logic
- Parse options; set defaults.
- Ensure `export_dir` exists.
- Attempt up to `MAX_RETRIES` times:
  - Run `run_export` to spool SQL query output to CSV.
  - If successful, write control file, gzip CSV, log success, exit.
  - On failure, log, sleep 60, retry.
- On exhausting retries, call `fail`.

#### Functions
- `log` – prefix timestamped message to stderr.
- `fail` – log failure and exit with code.
- `require_tools` – ensure `sqlplus` and `gzip` are available.
- `run_export` – execute SQL query via `sqlplus`, spool to file.
- `write_control` – count rows in CSV, write `rows=` and `business_day=` to `.ctl`.

#### Constants and configuration
- ``ORACLE_SID_DEFAULT=PAYPRD``
- ``EXPORT_DIR_DEFAULT=/data/export/payments``
- ``MAX_RETRIES=3``
- ``attempt=0``
- ``business_day=$(date +%Y%m%d)``
- ``export_dir=$EXPORT_DIR_DEFAULT``

#### Dependencies
- External commands: `sqlplus`, `gzip`.
- Oracle database connection via `sqlplus` (SYSDBA).
- SQL query reads `settled_payment` table for `business_day`.

#### Errors and exit codes
- `fail` with code `3` if `sqlplus` or `gzip` missing.
- `fail` with code `2` on usage error.
- `fail` with code `4` after exceeding `MAX_RETRIES`.
- Traps not defined; cleanup limited to removing incomplete CSV via `gzip -f` only if export succeeded.
