### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:38:57Z
- G: 
- A: 0.0.0
- X: file
---
> Exports one business day of settled Oracle payments as a gzipped CSV with a row-count control file.

#### Purpose
- Produces a daily settled-payment export for the selected business day.
- Runs directly as a nightly job; it is not a sourced library.

#### Type
- ksh93 (`#!/bin/ksh93`); no strict-mode settings (`set -e`, `set -u`, or `pipefail`).

#### Input
- `-d YYYYMMDD` sets `business_day`; parsed by `getopts`.
- `-o dir` sets `export_dir`; parsed by `getopts`.
- Environment variable `ORACLE_SID` is read, defaulting to `PAYPRD`.
- Reads the Oracle `settled_payment` table; uses the system date to choose the default business day.
- Reads no input files.

#### Output
- Writes `payments_<business_day>.csv` in `export_dir`, then compresses it to `.csv.gz`.
- Writes a neighboring `.ctl` file containing the CSV row count and business day.
- Logs timestamped status and failure messages to stderr.
- Uses the Oracle database through SQL*Plus.
- Exit status `0` on successful export; explicit failure statuses are listed under Errors and exit codes.

#### Core logic
- Defaults `business_day` to the current date in `YYYYMMDD` format and `export_dir` to `/data/export/payments`.
- Parses `-d` and `-o`; shifts parsed arguments away.
- Checks for `sqlplus` and `gzip`; creates `export_dir` if it does not exist.
- Sets the output path to `payments_<business_day>.csv`.
- Retries the SQL export up to three times, sleeping 60 seconds after each failed attempt.
- Runs SQL*Plus as SYSDBA; selects payment ID, account number, amount, and currency from `settled_payment` for the chosen business day, separated by semicolons.
- On successful SQL execution, counts CSV lines, writes the control file, gzips the CSV, logs success, and exits.
- After all attempts fail, reports failure and exits with status `4`.

#### Functions
- `log` — print timestamped messages to stderr.
- `fail` — log failure and exit with the supplied status or `1`.
- `require_tools` — verify required commands are available.
- `run_export` — query Oracle and write the CSV.
- `write_control` — write row count and business day to the control file.

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
- External commands: `date`, `whence`, `mkdir`, `sqlplus`, `wc`, `gzip`, `sleep`.
- Oracle table: `settled_payment`.
- Oracle connection: `CONNECT / AS SYSDBA`.

#### Errors and exit codes
- `1` — `fail` called without an explicit status.
- `2` — option parsing or usage failure.
- `3` — required `sqlplus` or `gzip` command is missing.
- `4` — all three export attempts fail.
- Retries failed exports up to three times, with a 60-second sleep after each failure; no traps are defined.
