### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:11:02Z
- G: 
- A: 0.0.0
- X: file
---
> Exports one business day of settled Oracle payments to a semicolon-delimited, gzipped CSV with a row-count control file.

#### Purpose
- Create a nightly payment extract for a selected business day.
- Run directly as a command-line script.

#### Type
- `ksh93` via `#!/bin/ksh93`; no strict-mode settings.

#### Input
- `-d YYYYMMDD` parsed by `getopts`: sets `business_day`.
- `-o dir` parsed by `getopts`: sets `export_dir`.
- `ORACLE_SID`: read from the environment; defaults to `ORACLE_SID_DEFAULT`.
- Reads no input files or directories.

#### Output
- Writes `payments_<business_day>.csv` under `export_dir`, then replaces it with `payments_<business_day>.csv.gz`.
- Writes a sibling `payments_<business_day>.ctl` containing the CSV row count and business day.
- Creates `export_dir` if missing.
- Logs timestamped status messages to stderr.
- Exits `0` on success; failure statuses are listed below.

#### Core logic
- Set the default business day from `date +%Y%m%d` and the default export directory.
- Set Oracle environment values and verify `sqlplus` and `gzip` are available.
- Parse `-d` and `-o`; create the export directory if needed.
- Try the export up to `MAX_RETRIES` times: run a SQL query for the selected day, write the control file, gzip the CSV, log success, and exit.
- On export failure, log the attempt and wait 60 seconds before retrying; fail after the retry limit.

#### Functions
- `log`: Timestamp and print a message to stderr.
- `fail`: Log failure and exit with a status.
- `require_tools`: Check required commands are available.
- `run_export`: Query settled payments and write the CSV.
- `write_control`: Count CSV rows and write the control file.

#### Constants and configuration
- `ORACLE_SID_DEFAULT=PAYPRD`
- `EXPORT_DIR_DEFAULT=/data/export/payments`
- `MAX_RETRIES=3`
- `attempt=0`
- `business_day=$(date +%Y%m%d)`
- `export_dir=$EXPORT_DIR_DEFAULT`
- `ORACLE_SID=${ORACLE_SID:-$ORACLE_SID_DEFAULT}`
- `NLS_LANG=AMERICAN_AMERICA.AL32UTF8`
- `csv=$export_dir/payments_$business_day.csv`

#### Dependencies
- Commands: `date`, `sqlplus`, `gzip`, `wc`, `mkdir`, `whence`, `sleep`.
- Oracle connection: `CONNECT / AS SYSDBA`.
- Oracle table: `settled_payment`; selects `payment_id`, `account_no`, `amount`, and `currency`, filtered by `business_day`.
- SQL is sent to `sqlplus` through a heredoc.

#### Errors and exit codes
- `2`: Invalid or unsupported option; prints usage.
- `3`: Required command (`sqlplus` or `gzip`) is unavailable.
- `1`: Export directory cannot be created, or `fail` is called without an explicit status.
- `4`: Export fails on all three attempts.
- `0`: Export, control-file creation, and compression succeed.
- Retries failed exports up to `MAX_RETRIES`, sleeping 60 seconds between attempts; no traps are defined.
