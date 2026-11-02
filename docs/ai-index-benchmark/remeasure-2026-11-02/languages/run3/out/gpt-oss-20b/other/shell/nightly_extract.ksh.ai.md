### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:56:21Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Extracts settled payments for a specified business day from Oracle, writes a CSV, creates a control file, and gzips the result.

#### Purpose
- Performs nightly export of settled payments.
- Runs as a standalone script.

#### Type
- `ksh93` shell.
- Uses `set -e`? none; uses `typeset -i` and `until` loop.

#### Input
- Options parsed with `getopts`:
  - `-d YYYYMMDD` → sets `business_day`.
  - `-o <dir>` → sets `export_dir`.
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
- Build `csv` path.
- Retry loop (max 3 attempts):
  - Run `run_export` to dump data via `sqlplus`.
  - If successful:
    - Write control file.
    - Gzip CSV.
    - Log success and exit.
  - On failure: log, sleep 60, retry.
- On exhausting retries: fail with exit 4.

#### Functions
- `log` – prints timestamped message to stderr.
- `fail` – logs failure and exits with given code.
- `require_tools` – checks `sqlplus` and `gzip` are available.
- `run_export` – runs SQL*Plus to export settled payments to a CSV.
- `write_control` – writes row count and business day to a `.ctl` file.

#### Constants and configuration
- ``ORACLE_SID_DEFAULT=PAYPRD``
- ``EXPORT_DIR_DEFAULT=/data/export/payments``
- ``MAX_RETRIES=3``
- ``attempt=0``
- ``business_day=$(date +%Y%m%d)``
- ``export_dir=$EXPORT_DIR_DEFAULT``

#### Dependencies
- External commands: `sqlplus`, `gzip`.
- Oracle database accessed via `sqlplus` with `/ AS SYSDBA`.
- SQL query reads `settled_payment` table for the specified `business_day`.

#### Errors and exit codes
- `fail` with code 1: generic failure.
- `fail` with code 2: usage error.
- `fail` with code 3: missing required tool.
- `fail` with code 4: exceeded retry limit.
