### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:43:44Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Extracts settled payments for a specified business day from Oracle, writes a CSV, creates a control file, and gzips the output with retry logic.

#### Purpose
- Performs nightly export of settled payments.
- Runs as a standalone script.

#### Type
- `ksh93` with `typeset` and `function` syntax.
- No strict mode flags set.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → sets `business_day`.
  - `-o <dir>` → sets `export_dir`.
- Environment variables read:
  - `ORACLE_SID` (default `PAYPRD`).
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`.
- No positional arguments.

#### Output
- Creates `payments_<business_day>.csv` in `export_dir`.
- Generates `<csv>.ctl` with row count and business day.
- Gzips the CSV to `<csv>.gz`.
- Prints log messages to stderr.
- Exits with status 0 on success, 1–4 on various failures.

#### Core logic
- Parse options; set defaults.
- Ensure `export_dir` exists.
- Set `csv` path.
- Retry loop (max 3 attempts):
  - Run `run_export` to dump data via `sqlplus`.
  - If successful:
    - Write control file.
    - Gzip CSV.
    - Log success and exit.
  - On failure: log, sleep 60, retry.
- On exhausting retries: log failure and exit with code 4.

#### Functions
- `log` – prints timestamped messages to stderr.
- `fail` – logs failure and exits with given code.
- `require_tools` – checks for `sqlplus` and `gzip`.
- `run_export` – runs SQL*Plus to dump settled payments to a file.
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
- Oracle database connection via `sqlplus` (SYSDBA, `/`).
- SQL query reads `settled_payment` table for `business_day`.

#### Errors and exit codes
- `fail` exits with code 1 by default, or specified code.
- Missing tools → exit 3.
- Cannot create export directory → exit 1.
- Exhausted retries → exit 4.
