### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:41:53Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Extracts settled payments for a specified business day from Oracle, writes a CSV, creates a control file, and gzips the result.

#### Purpose
- Performs nightly export of settled payments.
- Runs directly, not sourced.

#### Type
- `ksh93`
- `set -e` not used; `set -u` not used; `set -o pipefail` not used.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → `business_day`
  - `-o <dir>` → `export_dir`
- Environment variables:
  - `ORACLE_SID` (default `PAYPRD`)
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`
- No positional arguments.

#### Output
- Writes `payments_<business_day>.csv` to `$export_dir`.
- Creates control file `payments_<business_day>.ctl` with row count and business day.
- Gzips the CSV to `payments_<business_day>.csv.gz`.
- Prints log messages to stderr.
- Exit status: `0` on success, `1` on failure, `2` on usage error, `3` if required tool missing, `4` after exhausting retries.

#### Core logic
- Parse options; set defaults.
- Ensure `export_dir` exists.
- Set `csv` path.
- Retry loop up to `MAX_RETRIES`:
  - Run `run_export` to dump data via `sqlplus`.
  - If successful, write control file, gzip CSV, log success, exit.
  - On failure, log, sleep 60, retry.
- On exhaustion, call `fail` with exit code 4.

#### Functions
- `log` – prints timestamped message to stderr.
- `fail` – logs failure and exits with code.
- `require_tools` – checks for `sqlplus` and `gzip`.
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
- Oracle database via `sqlplus` connection `/ AS SYSDBA`.
- SQL query reads from table `settled_payment`.

#### Errors and exit codes
- `fail` with code 3: missing required tool.
- `fail` with code 2: incorrect usage.
- `fail` with code 4: exceeded retry limit.
- `run_export` failure triggers retry loop; no explicit exit code captured.
