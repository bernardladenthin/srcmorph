### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:40:30Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for a business day from Oracle to a gzipped CSV with a control file.

#### Purpose
- Run directly as a standalone script.
- Generates a CSV of settled payments for a specified or current business day, gzips it, and writes a control file.

#### Type
- `ksh93`
- Strict mode: none explicitly set; uses `typeset` and `set -e` not present.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → `business_day`
  - `-o <dir>` → `export_dir`
- Environment variables:
  - `ORACLE_SID` (default `PAYPRD`)
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`
- No positional arguments.

#### Output
- Creates directory `$export_dir` if missing.
- Writes `$export_dir/payments_$business_day.csv` (uncompressed), then gzips it to `$export_dir/payments_$business_day.csv.gz`.
- Creates control file `$export_dir/payments_$business_day.ctl` containing `rows=<count> business_day=<date>`.
- Prints log messages to stderr.
- Exit status:
  - `0` on success.
  - `1` on generic failure.
  - `2` on usage error.
  - `3` if required tools missing.
  - `4` after exhausting retries.

#### Core logic
- Parse options; set defaults.
- Ensure required tools (`sqlplus`, `gzip`) are available.
- Create export directory if needed.
- Build CSV path.
- Retry loop (max 3 attempts):
  - Run `run_export` to dump data via `sqlplus` into CSV.
  - On success: write control file, gzip CSV, log success, exit.
  - On failure: log, sleep 60, retry.
- After retries exhausted, fail with exit code 4.

#### Functions
- `log` – prints timestamped message to stderr.
- `fail` – logs failure and exits with given code.
- `require_tools` – checks presence of `sqlplus` and `gzip`.
- `run_export` – runs `sqlplus` to export settled payments into a CSV file.
- `write_control` – writes a control file with row count and business day.

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
- SQL query reads `settled_payment` table for `business_day`.

#### Errors and exit codes
- `fail "missing $tool" 3` – exit 3 if `sqlplus` or `gzip` missing.
- `fail "cannot create $export_dir"` – exit 1 if directory creation fails.
- `fail "usage: nightly_extract.ksh ..." 2` – exit 2 on bad options.
- `fail "giving up after $MAX_RETRIES attempts" 4` – exit 4 after retries exhausted.
