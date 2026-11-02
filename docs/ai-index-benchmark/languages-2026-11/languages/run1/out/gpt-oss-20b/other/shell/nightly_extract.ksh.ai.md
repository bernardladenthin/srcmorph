### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:12:33Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for a business day from Oracle to a gzipped CSV with a control file.

#### Purpose
- Run nightly to export settled payments.
- Executed directly; not sourced.

#### Type
- `ksh93`
- `set -e` not used; relies on `until` loop and explicit checks.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → `business_day`
  - `-o <dir>` → `export_dir`
- Environment variables read:
  - `ORACLE_SID` (default `PAYPRD`)
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`
- No positional arguments.

#### Output
- Creates `$export_dir/payments_$business_day.csv` (uncompressed) then gzips it.
- Generates `$export_dir/payments_$business_day.ctl` with row count and business day.
- Prints log messages to stderr.
- Exit status: `0` on success, `1` on failure, `2` on usage error, `3` if required tool missing, `4` after max retries.

#### Core logic
- Parse options; set defaults.
- Ensure `$export_dir` exists.
- `run_export` spawns `sqlplus` to query `settled_payment` for the day, spooling to CSV.
- `write_control` counts rows with `wc -l` and writes control file.
- Retry loop up to `MAX_RETRIES` (3):
  - Run export; on success write control, gzip, log, exit.
  - On failure log, sleep 60, retry.
- On exhaustion, fail with code 4.

#### Functions
- `log` – timestamped message to stderr.
- `fail` – log failure and exit with code.
- `require_tools` – ensure `sqlplus` and `gzip` available.
- `run_export` – execute SQL*Plus query, output to file.
- `write_control` – count rows, write control file.

#### Constants and configuration
- `ORACLE_SID_DEFAULT=PAYPRD`
- `EXPORT_DIR_DEFAULT=/data/export/payments`
- `MAX_RETRIES=3`
- `attempt=0`
- `business_day=$(date +%Y%m%d)`
- `export_dir=$EXPORT_DIR_DEFAULT`

#### Dependencies
- External commands: `sqlplus`, `gzip`, `wc`, `print`, `mkdir`.
- Oracle database via `sqlplus` connection `/ AS SYSDBA`.

#### Errors and exit codes
- `fail` with code 3: missing tool.
- `fail` with code 2: usage error.
- `fail` with code 4: exceeded retries.
- `run_export` failure triggers retry loop; no explicit exit code captured.
