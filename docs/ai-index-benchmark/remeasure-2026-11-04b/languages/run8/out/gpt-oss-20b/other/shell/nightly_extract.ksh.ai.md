### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:47:32Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Extracts settled payments for a specified business day from Oracle, writes a CSV, creates a control file, and gzips the output, retrying up to three times.

#### Purpose
- Performs nightly export of settled payments.
- Runs directly as a script.

#### Type
- `ksh93`
- Uses `set -e`? none; uses `typeset` and `print`.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → sets `business_day`.
  - `-o dir` → sets `export_dir`.
- Environment variables:
  - `ORACLE_SID` (default `PAYPRD`).
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`.
- No positional arguments.

#### Output
- Creates `payments_<business_day>.csv` in `export_dir`.
- Creates control file `payments_<business_day>.ctl` with row count and business day.
- Gzips the CSV to `payments_<business_day>.csv.gz`.
- Prints log messages to stderr.
- Exit status: `0` on success, `1` on failure, `2` on usage error, `3` if required tool missing, `4` after exhausting retries.

#### Core logic
- Parse options; set defaults.
- Ensure `export_dir` exists.
- Set `csv` path.
- Retry loop (max 3 attempts):
  - Run `run_export` to dump data via `sqlplus` into `csv`.
  - If successful:
    - Write control file via `write_control`.
    - Gzip the CSV.
    - Log success and exit.
  - On failure: log, sleep 60, retry.
- On exhaustion: call `fail` with exit code 4.

#### Functions
- `log` – prints timestamped message to stderr.
- `fail` – logs failure and exits with code.
- `require_tools` – checks for `sqlplus` and `gzip`; exits if missing.
- `run_export` – runs SQL*Plus to export settled payments to a file.
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
- Oracle connection: `CONNECT / AS SYSDBA` via `sqlplus`.
- SQL query reads from table `settled_payment`.

#### Errors and exit codes
- `fail` with code 1: generic failure.
- `fail` with code 2: usage error.
- `fail` with code 3: missing required tool.
- `fail` with code 4: exceeded retry limit.
