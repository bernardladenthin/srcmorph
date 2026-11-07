### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:46:36Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Extracts settled payments for a specified business day from Oracle, writes a CSV, creates a control file, and gzips the result.

#### Purpose
- Run nightly to export settled payments.
- Executed directly; not sourced.

#### Type
- `ksh93` (shebang `#!/bin/ksh93`).
- Uses `set -e`? none; uses `typeset` and `whence`.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → variable `business_day`.
  - `-o <dir>` → variable `export_dir`.
- Environment variables read:
  - `ORACLE_SID` (default `PAYPRD`).
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`.
- Files read:
  - Oracle database via `sqlplus`.

#### Output
- Writes `payments_<business_day>.csv` in `export_dir`.
- Creates control file `payments_<business_day>.ctl` with row count and business day.
- Gzips the CSV to `payments_<business_day>.csv.gz`.
- Prints log messages to stderr.
- Exit status: `0` on success, `1` on failure, `3` if tools missing, `4` if retries exhausted.

#### Core logic
- Parse options; set defaults.
- Ensure `export_dir` exists.
- Set `csv` path.
- Retry loop up to `MAX_RETRIES`:
  - Run `run_export` to generate CSV via `sqlplus`.
  - If successful:
    - `write_control` to create `.ctl`.
    - `gzip -f` the CSV.
    - Log success and exit.
  - On failure: log, sleep 60, retry.
- On exhausting retries: call `fail`.

#### Functions
- `log` – prints timestamped message to stderr.
- `fail` – logs failure and exits with code.
- `require_tools` – checks `sqlplus` and `gzip` availability.
- `run_export` – runs SQL query via `sqlplus`, writes CSV.
- `write_control` – counts rows, writes control file.

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
- SQL query selects from `settled_payment` table.

#### Errors and exit codes
- `fail` with code `3` if `sqlplus` or `gzip` missing.
- `fail` with code `4` after exceeding `MAX_RETRIES`.
- `fail` with code `2` on usage error.
- Traps: none defined.
