### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:21:30Z
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
- `ksh93` (shebang `#!/bin/ksh93`).
- Uses `set -e`? none; relies on `whence -q` and explicit exits.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → variable `business_day`.
  - `-o <dir>` → variable `export_dir`.
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
- Build `csv` path.
- Retry loop (max 3 attempts):
  - Run `run_export` to dump data via `sqlplus`.
  - On success:
    - Write control file.
    - Gzip CSV.
    - Log success and exit.
  - On failure:
    - Log attempt, sleep 60, retry.
- On exhausting retries, call `fail` with exit code 4.

#### Functions
- `log` – prefix timestamped message to stderr.
- `fail` – log failure and exit with code.
- `require_tools` – ensure `sqlplus` and `gzip` are available.
- `run_export` – execute SQL to produce CSV.
- `write_control` – write row count and business day to `.ctl`.

#### Constants and configuration
- `ORACLE_SID_DEFAULT=PAYPRD`
- `EXPORT_DIR_DEFAULT=/data/export/payments`
- `MAX_RETRIES=3`
- `attempt=0`
- `business_day=$(date +%Y%m%d)`
- `export_dir=$EXPORT_DIR_DEFAULT`

#### Dependencies
- External commands: `sqlplus`, `gzip`.
- Oracle database connection via `/ AS SYSDBA`.
- SQL query reads `settled_payment` table for `business_day`.

#### Errors and exit codes
- `fail` exit codes:
  - 1: generic failure (e.g., missing tool).
  - 2: usage error.
  - 3: missing required tool.
  - 4: exceeded retry limit.
