### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:24:02Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Exports settled payments for a business day from Oracle to a gzipped CSV with a row-count control file.

#### Purpose
- Produces a daily settled-payment export and adjacent row-count control file.
- Runs directly as a nightly job; not a sourced library.

#### Type
- `ksh93` (`#!/bin/ksh93`); no strict-mode settings.

#### Input
- `-d YYYYMMDD` sets `business_day`; parsed with `getopts`.
- `-o dir` sets `export_dir`; parsed with `getopts`.
- `ORACLE_SID` is read with default `PAYPRD`.
- Reads no input files or directories.

#### Output
- Writes `payments_$business_day.csv` in `export_dir`, then compresses it to `.csv.gz`.
- Writes a sibling `.ctl` file containing `rows=<count> business_day=<date>`.
- Creates `export_dir` if absent.
- Prints timestamped log messages to standard error.

#### Core logic
- Defaults `business_day` to the current date in `YYYYMMDD` format and `export_dir` to the configured export directory.
- Sets `ORACLE_SID` from its existing environment value or default, and exports `NLS_LANG=AMERICAN_AMERICA.AL32UTF8`.
- Parses `-d` and `-o`; shifts parsed arguments.
- Checks for `sqlplus` and `gzip`; creates the export directory if needed.
- Builds the CSV path and attempts the SQL export up to three times.
- Runs Oracle SQL to select payment ID, account number, amount, and currency for the requested business day, separated by semicolons.
- On success, counts CSV lines, writes the control file, gzips the CSV, logs success, and exits.
- On failure, logs the attempt, sleeps 60 seconds, and retries; after exhaustion, reports failure.

#### Functions
- `log` — timestamped stderr logging.
- `fail` — logs failure and exits with a status.
- `require_tools` — verifies required commands are available.
- `run_export` — queries Oracle into the CSV file.
- `write_control` — writes CSV row count and business day.

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
- Commands: `date`, `sqlplus`, `gzip`, `whence`, `mkdir`, `wc`, `sleep`.
- Oracle connection: `CONNECT / AS SYSDBA`.
- Oracle table: `settled_payment`.

#### Errors and exit codes
- `0` — export, control-file creation, and compression complete.
- `1` — default status from `fail` when no explicit status is supplied.
- `2` — invalid option or usage error.
- `3` — required `sqlplus` or `gzip` is missing.
- `4` — export fails for all three attempts.
- Directory creation failure calls `fail` with its default status `1`.
- Retries failed exports up to three times, sleeping 60 seconds after each failed attempt.
- No traps are defined.
