### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:34:44Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for a specified business day from Oracle to a gzipped CSV with a row‑count control file.

#### Purpose
- Run nightly to export settled payments.
- Executed directly as a script.

#### Type
- `ksh93`
- Strict mode: none (no `set -e`, `set -u`, or `set -o pipefail`).

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → sets `business_day`.
  - `-o <dir>` → sets `export_dir`.
- Environment variables read:
  - `ORACLE_SID` (default `PAYPRD`).
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`.
- Files read:
  - Oracle database via `sqlplus` (no local files).

#### Output
- Creates or overwrites:
  - `$export_dir/payments_$business_day.csv` (CSV).
  - `$export_dir/payments_$business_day.ctl` (control file).
  - `$export_dir/payments_$business_day.csv.gz` (gzipped CSV).
- Prints log messages to stderr.
- Exit status:
  - `0` on success.
  - `1` on generic failure.
  - `2` on usage error.
  - `3` if required tool missing.
  - `4` after exhausting retries.

#### Core logic
- Parse options; set defaults.
- Ensure `export_dir` exists.
- Build `csv` path.
- Retry loop (max 3 attempts):
  - Run `run_export` to dump data via `sqlplus`.
  - On success:
    - Write control file with row count.
    - Gzip the CSV.
    - Log success and exit.
  - On failure:
    - Log attempt, sleep 60s, retry.
- On final failure, call `fail` with exit code 4.

#### Functions
- `log` – prints timestamped message to stderr.
- `fail` – logs failure and exits with code.
- `require_tools` – checks `sqlplus` and `gzip` are available.
- `run_export` – runs SQL*Plus to spool settled payments to a file.
- `write_control` – writes `rows=` and `business_day=` to a `.ctl` file.

#### Constants and configuration
- ``ORACLE_SID_DEFAULT=PAYPRD``
- ``EXPORT_DIR_DEFAULT=/data/export/payments``
- ``MAX_RETRIES=3``
- ``attempt=0``
- ``business_day=$(date +%Y%m%d)``
- ``export_dir=$EXPORT_DIR_DEFAULT``

#### Dependencies
- External commands:
  - `sqlplus` – connects to Oracle as SYSDBA, runs a SELECT.
  - `gzip` – compresses the CSV.
- Oracle database (via `sqlplus` connection string `/ AS SYSDBA`).

#### Errors and exit codes
- `fail "missing $tool" 3` – exit 3 if `sqlplus` or `gzip` missing.
- `fail "cannot create $export_dir"` – exit 1 if directory creation fails.
- `fail "usage: nightly_extract.ksh ..." 2` – exit 2 on bad options.
- `fail "giving up after $MAX_RETRIES attempts" 4` – exit 4 after retries exhausted.
