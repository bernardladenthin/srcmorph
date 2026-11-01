### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:38:56Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for a business day from Oracle to a gzipped CSV with a row-count control file.

#### Purpose
- Produce a semicolon-delimited payment extract for one business day.
- Retry failed exports and record the CSV row count in a neighboring control file.

#### Type
- KornShell 93 (`ksh93`) command-line script.

#### Input
- `-d YYYYMMDD`: business day; defaults to the current date formatted as `YYYYMMDD`.
- `-o dir`: export directory; defaults to `/data/export/payments`.
- Environment variable `ORACLE_SID`: Oracle instance; defaults to `PAYPRD`.
- Reads Oracle `settled_payment` fields `payment_id`, `account_no`, `amount`, `currency`, and filters on `business_day`.

#### Output
- Writes `payments_<business_day>.csv`, then compresses it to `.csv.gz`.
- Writes `payments_<business_day>.ctl` containing `rows=<count> business_day=<business_day>`.
- Logs timestamped messages to standard error; exits with status `0` on success.

#### Core logic
- Parses `-d` and `-o`; sets Oracle session language to `AMERICAN_AMERICA.AL32UTF8`.
- Verifies `sqlplus` and `gzip` are available; creates the export directory if needed.
- Runs `sqlplus -s /nolog`, connects as SYSDBA, and selects payment fields joined by semicolons for the requested business day.
- On successful export, counts CSV lines, writes the control file, gzips the CSV, and exits successfully.
- Retries failed exports up to three times, sleeping 60 seconds between attempts; fails after the retry limit.

#### Public API
- `log(message...)` — timestamped stderr logging.
- `fail(message, status=1)` — logs failure and exits.
- `require_tools()` — checks required executables.
- `run_export(outfile)` — writes the Oracle query result to a file.
- `write_control(csv)` — writes row count and business day to a control file.
- `-d YYYYMMDD` — selects the business day.
- `-o dir` — selects the export directory.
- Script entry point — parses options and runs the export/retry workflow.

#### Constants and configuration
- `ORACLE_SID_DEFAULT=PAYPRD` — default Oracle SID.
- `EXPORT_DIR_DEFAULT=/data/export/payments` — default output directory.
- `MAX_RETRIES=3` — maximum export attempts.
- `attempt=0` — initial attempt counter.
- `business_day=$(date +%Y%m%d)` — default business day.
- `export_dir=$EXPORT_DIR_DEFAULT` — default export directory.
- `ORACLE_SID=${ORACLE_SID:-$ORACLE_SID_DEFAULT}` — Oracle SID environment setting.
- `NLS_LANG=AMERICAN_AMERICA.AL32UTF8` — Oracle client language and encoding.
- Retry delay: `60` seconds.
- SQL settings: `HEADING OFF`, `FEEDBACK OFF`, `PAGESIZE 0`, `LINESIZE 400`, `TRIMSPOOL ON`.

#### Dependencies
- Shell builtins/utilities: `date`, `print`, `whence`, `getopts`, `shift`, `mkdir`, `wc`, `gzip`, `sleep`.
- Oracle client: `sqlplus`.
- Oracle table: `settled_payment`; columns `payment_id`, `account_no`, `amount`, `currency`, `business_day`.

#### Exceptions / Errors
- Missing `sqlplus` or `gzip`: logs failure and exits `3`.
- Invalid option or usage: logs usage and exits `2`.
- Unable to create export directory: logs failure and exits `1`.
- Export failures: logs each failed attempt; after three attempts exits `4`.
