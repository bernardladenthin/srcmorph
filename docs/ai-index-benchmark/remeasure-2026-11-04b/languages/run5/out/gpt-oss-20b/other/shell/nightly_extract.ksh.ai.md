### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:44:39Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Extracts settled payments for a specified business day from Oracle, writes a CSV, creates a control file, and gzips the result.

#### Purpose
- Runs nightly to export settled payments.
- Executed directly; not sourced.

#### Type
- `ksh93` (shebang `#!/bin/ksh93`).
- Uses `set -e`? none; relies on `whence -q` and explicit exits.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → variable `business_day`.
  - `-o <dir>` → variable `export_dir`.
- Environment variables:
  - `ORACLE_SID` (default `PAYPRD`).
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`.
- No positional arguments.

#### Output
- Creates `payments_<business_day>.csv` in `export_dir`.
- Generates `<csv>.ctl` control file with row count and business day.
- Gzips the CSV to `payments_<business_day>.csv.gz`.
- Prints log messages to stderr.
- Exits with status 0 on success, 1–4 on various failures.

#### Core logic
- Parse options; set defaults.
- Ensure `export_dir` exists.
- Attempt export up to `MAX_RETRIES` (3):
  - Run `run_export` to query Oracle via `sqlplus` and spool CSV.
  - On success: write control file, gzip CSV, log success, exit 0.
  - On failure: log, sleep 60, retry.
- If all attempts fail, call `fail` with exit code 4.

#### Functions
- `log` – prints timestamped message to stderr.
- `fail` – logs failure and exits with given code.
- `require_tools` – checks for `sqlplus` and `gzip`; exits if missing.
- `run_export` – runs Oracle query via `sqlplus`, writes CSV.
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
- Oracle connection: `CONNECT / AS SYSDBA` (no credentials in script).
- SQL query reads `settled_payment` table, filters by `business_day`.

#### Errors and exit codes
- `fail "missing $tool" 3` – missing tool.
- `fail "cannot create $export_dir"` – directory creation failure.
- `fail "giving up after $MAX_RETRIES attempts" 4` – all retries exhausted.
- `fail "usage: nightly_extract.ksh ..." 2` – invalid options.
