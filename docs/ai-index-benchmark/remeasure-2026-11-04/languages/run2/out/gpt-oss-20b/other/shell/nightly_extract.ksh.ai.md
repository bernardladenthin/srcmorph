### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:02:51Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Extracts settled payments for a specified business day from Oracle, writes a CSV, gzips it, and creates a control file with row count.

#### Purpose
- Run nightly to export settled payments.
- Executed directly; not sourced.

#### Type
- `ksh93` (shebang `#!/bin/ksh93`).
- Uses `set -e`? none; uses `typeset` and `whence`.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → sets `business_day`.
  - `-o <dir>` → sets `export_dir`.
- Environment variables:
  - `ORACLE_SID` (default `PAYPRD`).
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`.
- No other external input files.

#### Output
- Creates `payments_<business_day>.csv` in `$export_dir`.
- Gzips the CSV to `payments_<business_day>.csv.gz`.
- Writes control file `payments_<business_day>.ctl` with `rows=` and `business_day=`.
- Prints log messages to stderr.
- Exits with status 0 on success, 1–4 on various failures.

#### Core logic
- Parse options; set defaults.
- Ensure `$export_dir` exists.
- Build `csv` path.
- Retry loop (max 3 attempts):
  - Run `run_export` to spool SQL query output to `$csv`.
  - If successful:
    - `write_control` counts rows and writes control file.
    - `gzip -f "$csv"`.
    - Log success and exit.
  - On failure: log, sleep 60, retry.
- On exhausting retries: call `fail` with exit code 4.

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
- Oracle database connection via `sqlplus / AS SYSDBA`.
- SQL query accesses table `settled_payment`.

#### Errors and exit codes
- `fail` exit codes:
  - 1: generic failure (e.g., missing tool).
  - 2: usage error.
  - 3: missing required tool.
  - 4: exceeded retry limit.
- Traps: none defined.
