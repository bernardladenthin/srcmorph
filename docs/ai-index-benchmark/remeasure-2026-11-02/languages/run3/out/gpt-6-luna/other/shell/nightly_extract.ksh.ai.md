### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:54:59Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Exports one business day of settled Oracle payments to a gzipped CSV with a companion row-count control file.

#### Purpose
- Produce a daily settled-payments extract for the requested business date.
- Run directly as a nightly export command; not a sourced library.

#### Type
- `ksh93` (`#!/bin/ksh93`); no `set -e`, `set -u`, or `pipefail`.

#### Input
- `-d YYYYMMDD` via `getopts`: sets `business_day`; defaults to the current date formatted by `date +%Y%m%d`.
- `-o dir` via `getopts`: sets `export_dir`; defaults to `/data/export/payments`.
- Environment: `ORACLE_SID` defaults to `PAYPRD`; `NLS_LANG` is set to `AMERICAN_AMERICA.AL32UTF8`.
- Reads no input files or directories.

#### Output
- Writes `payments_$business_day.csv` under `export_dir`, then compresses it to `.csv.gz`.
- Writes a companion `.ctl` file containing `rows=<count> business_day=<date>`.
- Prints timestamped progress and failure messages to standard error.
- Exit status `0` on reported export success; failure statuses are listed under Errors and exit codes.

#### Core logic
- Parses `-d` and `-o`; shifts past parsed options.
- Requires `sqlplus` and `gzip`; creates `export_dir` if it does not exist.
- Sets the CSV path to `$export_dir/payments_$business_day.csv`.
- Tries the export up to `MAX_RETRIES` times:
  - Runs SQL*Plus as SYSDBA; selects payment ID, account number, amount, and currency from `settled_payment` for `business_day`.
  - On success, counts CSV lines, writes the control file, gzips the CSV, logs success, and exits `0`.
  - On failure, logs the attempt and sleeps 60 seconds before retrying.
- After all attempts fail, reports failure and exits `4`.

#### Functions
- `log`: Print timestamped messages to standard error.
- `fail`: Log failure and exit with a supplied or default status.
- `require_tools`: Check for required commands.
- `run_export`: Run the Oracle query and write its output to the CSV.
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
- `outfile=$1` (local to `run_export`)
- `csv=$1` (local to `write_control`)
- `rows` (integer local to `write_control`; assigned the CSV line count)
- `csv=$export_dir/payments_$business_day.csv`

#### Dependencies
- External commands: `date`, `sqlplus`, `gzip`, `wc`, `sleep`.
- Uses KornShell built-ins/utilities: `whence`, `getopts`, `mkdir`, `print`.
- Oracle endpoint: local SYSDBA connection via `CONNECT / AS SYSDBA`.
- Oracle table: `settled_payment` (reads `payment_id`, `account_no`, `amount`, `currency`, filtered by `business_day`).
- No sourced files.

#### Errors and exit codes
- `0`: Export command succeeds; control file is written and compression is attempted.
- `1` by default: `fail` called without an explicit status.
- `2`: Invalid option or usage error.
- `3`: Required `sqlplus` or `gzip` command is missing.
- `4`: All `MAX_RETRIES` export attempts fail.
- Directory creation failure calls `fail` with the default status `1`.
- Failed export attempts are retried up to three times, with a 60-second sleep after each failure.
- No traps are defined.
