### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:54:08Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for one business day from Oracle to a gzipped CSV with a row-count control file.

#### Purpose
- Runs a nightly payment export; can be invoked directly with a date and output-directory override.
- Writes the export and its control file under the chosen directory.

#### Type
- `ksh93` (`#!/bin/ksh93`); no strict-mode settings.

#### Input
- `-d YYYYMMDD` via `getopts`: sets `business_day`; defaults to the current date in `YYYYMMDD` format.
- `-o dir` via `getopts`: sets `export_dir`; defaults to `/data/export/payments`.
- `ORACLE_SID`: read from the environment; defaults to `PAYPRD`.
- Reads no input files or directories.

#### Output
- Writes `payments_<business_day>.csv` in `export_dir`, then compresses it to `.csv.gz`.
- Writes a neighboring `.ctl` file containing `rows=<count> business_day=<business_day>`.
- Logs timestamped status messages to standard error.
- Exits `0` after a successful export; failures use statuses `1`, `2`, `3`, or `4` as described below.

#### Core logic
- Parses `-d` and `-o`, then shifts parsed arguments; remaining arguments are unused.
- Requires `sqlplus` and `gzip`; exits if either is unavailable.
- Creates `export_dir` if it does not exist; exits if creation fails.
- Sets the CSV path to `payments_<business_day>.csv` within `export_dir`.
- Tries the export up to `MAX_RETRIES` times:
  - Runs `sqlplus -s /nolog`, connects as SYSDBA, and selects payment ID, account number, amount, and currency from `settled_payment` for `business_day`.
  - On success, counts CSV lines, writes the control file, gzips the CSV, logs success, and exits.
  - On failure, logs the attempt and sleeps 60 seconds before retrying.
- Exits with a failure message after all retries fail.

#### Functions
- `log` — timestamped stderr logging.
- `fail` — logs failure and exits.
- `require_tools` — checks required commands.
- `run_export` — queries Oracle and writes CSV output.
- `write_control` — writes row count and business day.

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
- Commands: `date`, `sqlplus`, `gzip`, `wc`, `mkdir`, `sleep`; checks command availability with `whence`.
- Oracle connection: `/ AS SYSDBA`, using `ORACLE_SID` and `NLS_LANG`.
- Oracle table: `settled_payment`; filters on `business_day` and selects `payment_id`, `account_no`, `amount`, and `currency`.

#### Errors and exit codes
- `1` — `fail` called without an explicit status.
- `2` — invalid option or usage error.
- `3` — `sqlplus` or `gzip` is unavailable.
- `4` — all export attempts fail.
- `0` — export succeeds.
- Failed export attempts are retried up to three times, with a 60-second sleep after each failure, including the last.
- No traps are defined.
