### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:28:14Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for a business day from Oracle to a gzipped CSV with a row-count control file.

#### Purpose
- Create a daily payment export and accompanying row-count control file.
- Retry failed exports up to a configured limit.

#### Type
- KornShell 93 (`ksh93`) command-line script.

#### Input
- `-d YYYYMMDD`: optional business day; defaults to the current date in `YYYYMMDD` format.
- `-o <dir>`: optional export directory; defaults to `/data/export/payments`.
- `ORACLE_SID`: optional environment variable; defaults to `PAYPRD`.
- Oracle `settled_payment` rows for the selected `business_day`.

#### Output
- Writes `$export_dir/payments_$business_day.csv`, then compresses it to `.csv.gz`.
- Writes `$export_dir/payments_$business_day.ctl` containing `rows=<count> business_day=<date>`.
- Logs timestamped status messages to standard error; exits with status 0 on success.

#### Core logic
- Sets Oracle environment (`ORACLE_SID`, `NLS_LANG`) and initializes the business day and export directory.
- Parses `-d` and `-o`; checks for `sqlplus` and `gzip`, then creates the export directory if needed.
- Runs SQL*Plus as SYSDBA to select `payment_id`, `account_no`, `amount`, and `currency` from `settled_payment`, joined into semicolon-separated rows.
- On a successful export, counts CSV lines, writes the control file, gzips the CSV, logs success, and exits.
- On export failure, logs the attempt and waits 60 seconds; gives up after the retry limit.

#### Public API
- `nightly_extract.ksh([-d YYYYMMDD] [-o dir])` → process exit status — exports one business day of settled payments.
- `log(message)` → none — writes timestamped log message to standard error.
- `fail(message, status)` → exits — logs failure and exits with supplied status or 1.
- `require_tools()` → none — checks required executables.
- `run_export(outfile)` → command status — runs the Oracle query into the CSV file.
- `write_control(csv)` → none — writes row count and business day to the control file.

#### Constants and configuration
- `ORACLE_SID_DEFAULT=PAYPRD` — default Oracle SID.
- `EXPORT_DIR_DEFAULT=/data/export/payments` — default export directory.
- `MAX_RETRIES=3` — maximum export attempts.
- `attempt=0` — initial retry counter.
- `business_day=$(date +%Y%m%d)` — default export date.
- `export_dir=$EXPORT_DIR_DEFAULT` — default output directory.
- `ORACLE_SID=${ORACLE_SID:-$ORACLE_SID_DEFAULT}` — environment override with default.
- `NLS_LANG=AMERICAN_AMERICA.AL32UTF8` — Oracle client character set configuration.
- Retry delay: `60` seconds.

#### Dependencies
- Commands: `date`, `whence`, `sqlplus`, `wc`, `gzip`, `mkdir`, `sleep`.
- Oracle table: `settled_payment`; columns: `payment_id`, `account_no`, `amount`, `currency`, `business_day`.
- SQL*Plus commands: `CONNECT`, `SET`, `SELECT`, `TO_DATE`, `EXIT`.

#### Exceptions / Errors
- Missing `sqlplus` or `gzip`: logs failure and exits with status 3.
- Invalid option or usage: logs failure and exits with status 2.
- Export directory creation failure: logs failure and exits with default status 1.
- Exhausted export retries: logs failure and exits with status 4.
