### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:18:11Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Extracts settled payments for a specified business day from Oracle, writes a CSV, creates a row‑count control file, and gzips the result.

#### Purpose
- Run nightly to export settled payments.
- Executed directly; not sourced.

#### Type
- `ksh93` (shebang `#!/bin/ksh93`).
- Uses `set -e`? none; relies on `whence -q` and explicit exits.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → variable `business_day`.
  - `-o <dir>` → variable `export_dir`.
- Environment:
  - `ORACLE_SID` (defaults to `PAYPRD`).
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`.
- No other arguments.

#### Output
- CSV file: `${export_dir}/payments_${business_day}.csv`.
- Control file: `${export_dir}/payments_${business_day}.ctl` containing `rows=` and `business_day=`.
- Gzipped CSV: `${export_dir}/payments_${business_day}.csv.gz`.
- Logs to stderr via `log`.
- Exit status: `0` on success, `1` on failure, `3` if tools missing, `4` if retries exhausted.

#### Core logic
- Parse options; set defaults.
- Ensure export directory exists.
- Attempt up to `MAX_RETRIES` times:
  - Run `run_export` to spool SQL query output to CSV.
  - If successful, write control file, gzip CSV, log success, exit.
  - On failure, log, sleep 60, retry.
- On exhausting retries, call `fail`.

#### Functions
- `log` – prepend timestamp to messages, write to stderr.
- `fail` – log failure, exit with code.
- `require_tools` – ensure `sqlplus` and `gzip` are available.
- `run_export` – execute SQL query via `sqlplus`, spool to file.
- `write_control` – count rows in CSV, write control file.

#### Constants and configuration
- ``ORACLE_SID_DEFAULT='PAYPRD'``
- ``EXPORT_DIR_DEFAULT='/data/export/payments'``
- ``MAX_RETRIES=3``
- ``attempt=0``
- ``business_day=$(date +%Y%m%d)``
- ``export_dir=$EXPORT_DIR_DEFAULT``

#### Dependencies
- External commands: `sqlplus`, `gzip`.
- Oracle connection: `CONNECT / AS SYSDBA` (no credentials in script).
- SQL query reads table `settled_payment`.

#### Errors and exit codes
- `fail` with code `3` if `sqlplus` or `gzip` missing.
- `fail` with code `4` after exceeding `MAX_RETRIES`.
- `fail` with code `2` for usage error.
- Traps: none defined; cleanup handled by `gzip -f` and exit.
