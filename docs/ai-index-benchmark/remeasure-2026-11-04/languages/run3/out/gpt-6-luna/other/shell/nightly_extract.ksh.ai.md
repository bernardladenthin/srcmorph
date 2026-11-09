### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:04:18Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Exports one business day of settled Oracle payments as a gzipped CSV with a row-count control file.

#### Purpose
- Produces a dated payment extract and row-count control file for nightly settlement reporting.
- Runs directly as a command-line script.

#### Type
- `ksh93` (`#!/bin/ksh93`); no strict-mode settings.

#### Input
- `getopts` options: `-d` sets `business_day` (`YYYYMMDD`); `-o` sets `export_dir`.
- Environment: `ORACLE_SID` defaults to `PAYPRD`; `NLS_LANG` is set to `AMERICAN_AMERICA.AL32UTF8`.
- Reads Oracle table `settled_payment`; no local input files.

#### Output
- Creates `export_dir` if absent.
- Writes `payments_<business_day>.csv`, then replaces it with `payments_<business_day>.csv.gz`.
- Writes `payments_<business_day>.ctl` containing row count and business day.
- Logs timestamped status messages to standard error.
- Exit statuses: `0` success; `1` default `fail` status; `2` invalid usage; `3` missing required tool; `4` retries exhausted.

#### Core logic
- Parses `-d` and `-o`; rejects other options with usage error.
- Requires `sqlplus` and `gzip`; creates the export directory if needed.
- Runs `sqlplus -s /nolog`, connects as SYSDBA, and selects payment ID, account number, amount, and currency from `settled_payment` for `business_day`.
- Retries failed exports up to `MAX_RETRIES`, sleeping 60 seconds after each failed attempt.
- On success, counts CSV lines, writes the `.ctl` file, gzips the CSV, logs success, and exits `0`.
- After retries are exhausted, reports failure and exits `4`.

#### Functions
- `log` — Timestamp and print messages to standard error.
- `fail` — Log failure and exit with a status.
- `require_tools` — Check for required commands.
- `run_export` — Query Oracle and write CSV output.
- `write_control` — Write row count and business day.

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
- Shell commands and built-ins: `whence`, `getopts`, `print`.
- Oracle connection: `/ AS SYSDBA`.
- Oracle table: `settled_payment`; columns read: `payment_id`, `account_no`, `amount`, `currency`, `business_day`.

#### Errors and exit codes
- `2` — Unsupported option; prints usage through `fail`.
- `3` — `sqlplus` or `gzip` unavailable.
- `1` — Default status for `fail`, including export-directory creation failure.
- `4` — Export fails through all three attempts.
- Retries wait 60 seconds after each failed attempt, including the final failed attempt.
- No traps.
