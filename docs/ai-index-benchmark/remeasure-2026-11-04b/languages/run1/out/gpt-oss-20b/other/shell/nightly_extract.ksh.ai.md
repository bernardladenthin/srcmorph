### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:15:01Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Extracts settled payments for a specified business day from Oracle, writes a CSV, creates a row‑count control file, and gzips the result.

#### Purpose
- Runs nightly to export settled payments.
- Executed directly (not sourced).

#### Type
- `ksh93` (shebang `#!/bin/ksh93`).
- Uses `set -e`? none; relies on `whence -q` and explicit exits.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → variable `business_day`.
  - `-o <dir>` → variable `export_dir`.
- Environment variables read:
  - `ORACLE_SID` (default `PAYPRD`).
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`.
- No other files read.

#### Output
- Creates `payments_<business_day>.csv` in `export_dir`.
- Creates control file `payments_<business_day>.ctl` with row count.
- Gzips the CSV to `payments_<business_day>.csv.gz`.
- Prints log messages to stderr.
- Exits with status 0 on success, 1–4 on various failures.

#### Core logic
- Parse options; set defaults.
- Ensure `export_dir` exists.
- Build CSV path.
- Retry loop (max 3 attempts):
  - Run `run_export` to spool SQL query output to CSV.
  - If successful, write control file, gzip CSV, log success, exit.
  - On failure, log, sleep 60, retry.
- On exhausting retries, call `fail` with exit code 4.

#### Functions
- `log` – prints timestamped message to stderr.
- `fail` – logs failure and exits with given code.
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
- Oracle connection: `CONNECT / AS SYSDBA` (no credentials in script).
- SQL query reads table `settled_payment`.

#### Errors and exit codes
- `fail` with code 1: generic failure.
- `fail` with code 2: usage error.
- `fail` with code 3: missing required tool.
- `fail` with code 4: exceeded retry limit.
