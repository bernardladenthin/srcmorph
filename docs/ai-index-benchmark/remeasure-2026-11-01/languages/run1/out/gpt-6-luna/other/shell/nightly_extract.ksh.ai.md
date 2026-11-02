### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:15:57Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for one business day from Oracle to a gzipped CSV with a row-count control file.

#### Purpose
- Produce a daily settled-payment export for the selected business day.
- Run directly as a nightly job; not a sourced library.

#### Type
- `ksh93` (`#!/bin/ksh93`); no strict-mode settings (`set -e`, `set -u`, or `pipefail`).

#### Input
- `-d YYYYMMDD` via `getopts`: sets `business_day`; defaults to the current date in `YYYYMMDD` format.
- `-o dir` via `getopts`: sets `export_dir`; defaults to `/data/export/payments`.
- Environment: reads `ORACLE_SID`, defaulting to `PAYPRD`.
- Files/directories read: none explicitly.

#### Output
- Writes `payments_<business_day>.csv` in `export_dir`, then compresses it to `.csv.gz`.
- Writes a sibling `.ctl` file containing `rows=<count> business_day=<business_day>`.
- Prints timestamped status and failure messages to standard error.
- Exit status: `0` on export success; `1` for default failures such as directory creation or final failure; `2` for usage errors; `3` if a required tool is missing; `4` after retries are exhausted.

#### Core logic
- Set default business day and export directory; use `ORACLE_SID` from the environment or default to `PAYPRD`, and export `NLS_LANG=AMERICAN_AMERICA.AL32UTF8`.
- Parse `-d` and `-o`; shift parsed arguments away.
- Require `sqlplus` and `gzip`; create `export_dir` if it does not exist.
- Set the output path to `payments_<business_day>.csv`.
- Retry the export up to three times: connect through `sqlplus` as SYSDBA and select payment ID, account number, amount, and currency from `settled_payment` for the requested business day.
- On successful export, count CSV lines, write the control file, gzip the CSV, log success, and exit `0`.
- On failed export, log the attempt and sleep 60 seconds; after three failures, exit with status `4`.

#### Functions
- `log`: Write timestamped messages to standard error.
- `fail`: Log failure and exit with a supplied or default status.
- `require_tools`: Check for `sqlplus` and `gzip`.
- `run_export`: Query Oracle and write the CSV.
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
- `opt` is assigned by `getopts` for option parsing.
- `csv=$export_dir/payments_$business_day.csv`

#### Dependencies
- External commands: `date`, `whence`, `mkdir`, `sqlplus`, `wc`, `gzip`, `sleep`.
- Shell built-ins: `getopts`, `shift`, `print`, `exit`.
- Oracle connection: `CONNECT / AS SYSDBA`.
- Oracle table: `settled_payment`; selected columns: `payment_id`, `account_no`, `amount`, `currency`; filtered by `business_day`.

#### Errors and exit codes
- `2`: option parsing reaches the usage-error branch.
- `3`: `sqlplus` or `gzip` is unavailable.
- `1`: `fail` without an explicit status, including failure to create `export_dir`.
- `4`: all three export attempts fail.
- `0`: export succeeds and the script completes its success path.
- Retries: three maximum; 60-second sleep after each failed export attempt.
- No traps are defined.
