### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:18:23Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for a specified business day from Oracle to a gzipped CSV with a row‑count control file.

#### Purpose
- Run directly as a script.
- Generates a CSV of settled payments for a business day, gzips it, and writes a control file.

#### Type
- `ksh93` shell.
- Uses `set -e`? none; uses `typeset` and `whence`.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → variable `business_day`.
  - `-o <dir>` → variable `export_dir`.
- Environment variables read:
  - `ORACLE_SID` (defaults to `PAYPRD`).
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`.
- No positional arguments.

#### Output
- Creates directory `$export_dir` if missing.
- Writes `$export_dir/payments_$business_day.csv`.
- Creates `$export_dir/payments_$business_day.ctl` with row count.
- Gzips the CSV to `$export_dir/payments_$business_day.csv.gz`.
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
- Attempt export up to `MAX_RETRIES` times:
  - Run `run_export` to spool SQL query output to CSV.
  - If successful, write control file, gzip CSV, log success, exit.
  - On failure, log, sleep 60, retry.
- On final failure, call `fail` with exit code 4.

#### Functions
- `log` – prefix timestamped message to stderr.
- `fail` – log failure and exit with code.
- `require_tools` – check presence of `sqlplus` and `gzip`.
- `run_export` – execute SQL query via `sqlplus` and spool to file.
- `write_control` – count rows in CSV and write control file.

#### Constants and configuration
- ``ORACLE_SID_DEFAULT=PAYPRD``
- ``EXPORT_DIR_DEFAULT=/data/export/payments``
- ``MAX_RETRIES=3``
- ``attempt=0``
- ``business_day=$(date +%Y%m%d)``
- ``export_dir=$EXPORT_DIR_DEFAULT``

#### Dependencies
- External commands: `sqlplus`, `gzip`.
- Oracle database connection via `sqlplus` with `/ AS SYSDBA`.
- SQL query selects from `settled_payment` table where `business_day` matches.

#### Errors and exit codes
- `fail "missing $tool" 3` – exit 3 if `sqlplus` or `gzip` missing.
- `fail "cannot create $export_dir"` – exit 1 if directory creation fails.
- `fail "usage: nightly_extract.ksh ..." 2` – exit 2 on bad options.
- `fail "giving up after $MAX_RETRIES attempts" 4` – exit 4 after retries exhausted.
