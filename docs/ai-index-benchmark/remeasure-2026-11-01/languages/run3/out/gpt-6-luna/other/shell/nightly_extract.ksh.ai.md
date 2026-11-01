### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:55:58Z
- G: 
- A: 0.0.0
- X: file
---
> Exports one business day of settled Oracle payments to a gzipped CSV with a row-count control file.

#### Purpose
- Nightly payment export for a selected business day.
- Runs directly; accepts a date and output-directory override.

#### Type
- `ksh93` (`#!/bin/ksh93`); no strict-mode settings.

#### Input
- `-d YYYYMMDD` sets `business_day`; parsed with `getopts`.
- `-o dir` sets `export_dir`; parsed with `getopts`.
- `ORACLE_SID` environment variable; defaults to `PAYPRD`.
- Reads settled payment data from Oracle table `settled_payment`.
- No input files or directories.

#### Output
- Writes `payments_$business_day.csv`, then compresses it to `.csv.gz`.
- Writes `${csv%.csv}.ctl` with the row count and business day.
- Logs timestamped status messages to standard error.
- Exits `0` on success; failure statuses are listed below.

#### Core logic
- Sets the default business day from `date +%Y%m%d`; initializes the export directory.
- Sets `ORACLE_SID` if unset and exports it; exports `NLS_LANG=AMERICAN_AMERICA.AL32UTF8`.
- Parses `-d` and `-o`; shifts parsed arguments.
- Checks for `sqlplus` and `gzip`; creates the export directory if needed.
- Attempts the Oracle export up to three times: connects as SYSDBA and selects `payment_id`, `account_no`, `amount`, and `currency` from `settled_payment` for the business day, separated by semicolons.
- On success, counts CSV lines, writes the control file, gzips the CSV, logs success, and exits.
- On export failure, logs the attempt and sleeps 60 seconds before retrying; fails after the retry limit.

#### Functions
- `log`: Timestamped stderr logging.
- `fail`: Logs failure and exits with a status.
- `require_tools`: Checks required commands are available.
- `run_export`: Runs the Oracle query into the CSV.
- `write_control`: Writes the CSV row count and business day.

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
- External commands: `date`, `sqlplus`, `gzip`, `wc`, `mkdir`, `sleep`.
- Shell commands/builtins: `whence`, `print`, `getopts`.
- Oracle connection: `CONNECT / AS SYSDBA`.
- Oracle table: `settled_payment`.

#### Errors and exit codes
- `2`: Invalid or incomplete option arguments trigger the usage failure.
- `3`: `sqlplus` or `gzip` is unavailable.
- `1`: Export directory creation fails; also the default status for `fail`.
- `4`: All three export attempts fail.
- `0`: Export and compression succeed.
- Retries failed exports up to three times, sleeping 60 seconds between attempts.
- No traps.
