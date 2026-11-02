### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:37:49Z
- G: 
- A: 0.0.0
- X: file
---
> Exports one business day of settled Oracle payments to a gzipped CSV with a row-count control file.

#### Purpose
- Creates a daily settled-payment export for the requested business day.
- Runs directly as a nightly job; it is not a sourced library.

#### Type
- `ksh93` (`#!/bin/ksh93`); no strict-mode settings (`set -e`, `set -u`, or `pipefail`).

#### Input
- `getopts` parses:
  - `-d YYYYMMDD` → `business_day`
  - `-o dir` → `export_dir`
- Environment:
  - `ORACLE_SID` → Oracle SID; defaults to `PAYPRD`.
- Reads Oracle table `settled_payment`; reads the system date to determine the default business day.

#### Output
- Writes `payments_<business_day>.csv` in `export_dir`, then gzips it to `.csv.gz`.
- Writes a sibling `.ctl` file containing `rows=<count> business_day=<business_day>`.
- Logs timestamped status messages to standard error.
- Exit statuses: `0` on successful export; `1` for `fail` without an explicit status; `2` for invalid usage; `3` if a required tool is missing; `4` after all export attempts fail.

#### Core logic
- Sets the default business day from `date +%Y%m%d`; defaults the export directory and Oracle SID.
- Sets `NLS_LANG=AMERICAN_AMERICA.AL32UTF8`.
- Parses `-d` and `-o`; shifts past parsed arguments.
- Requires `sqlplus` and `gzip`; creates `export_dir` if it does not exist.
- Builds the CSV path as `payments_<business_day>.csv`.
- Tries the export up to `MAX_RETRIES` times:
  - Runs `sqlplus -s /nolog`, connects as SYSDBA, and selects payment ID, account number, amount, and currency from `settled_payment` for the business day, separated by semicolons.
  - On success, counts CSV lines, writes the control file, gzips the CSV, logs success, and exits `0`.
  - On failure, logs the attempt and sleeps 60 seconds before retrying.
- Calls `fail` with status `4` after exhausting retries.

#### Functions
- `log` — Timestamp and print a message to standard error.
- `fail` — Log failure and exit with a status.
- `require_tools` — Check for required commands.
- `run_export` — Query Oracle and write the CSV.
- `write_control` — Count CSV rows and write the control file.

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
- Shell command lookup: `whence`; shell builtins: `print`, `getopts`.
- Oracle connection: `/ AS SYSDBA`.
- Oracle table: `settled_payment`.

#### Errors and exit codes
- `2` — Invalid option or usage failure.
- `3` — `sqlplus` or `gzip` is unavailable.
- `1` — Export directory creation fails; also the default status for other `fail` calls without an explicit status.
- `4` — `sqlplus` export fails on all `MAX_RETRIES` attempts.
- Retries export up to `MAX_RETRIES`; waits 60 seconds between failed attempts.
- No traps are defined.
