### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:34:15Z
- G: 
- A: 0.0.0
- X: file
---
> Exports one business day of settled Oracle payments to a gzipped CSV with a companion row-count control file.

#### Purpose
- Creates a nightly settled-payment export for a selected business day.
- Runs directly; not a sourced library.

#### Type
- `ksh93` via `#!/bin/ksh93`; no strict-mode settings.

#### Input
- `-d YYYYMMDD` via `getopts`: sets `business_day`; defaults to the date from `date +%Y%m%d`.
- `-o dir` via `getopts`: sets `export_dir`; defaults to `/data/export/payments`.
- Environment: reads `ORACLE_SID`, defaulting to `PAYPRD`; sets `NLS_LANG=AMERICAN_AMERICA.AL32UTF8`.
- Reads Oracle table `settled_payment`; no local input files.

#### Output
- Writes `$export_dir/payments_$business_day.csv`, then compresses it to `.csv.gz`.
- Writes `$export_dir/payments_$business_day.ctl` containing `rows=<count> business_day=<date>`.
- Logs timestamped status and failure messages to standard error.
- Exits `0` on success; failure statuses are listed below.

#### Core logic
- Parses `-d` and `-o`; shifts parsed options.
- Requires `sqlplus` and `gzip`; creates `export_dir` if absent.
- Sets the CSV path and attempts the export up to three times.
- Runs an Oracle query for the selected `business_day`, outputting semicolon-delimited payment ID, account number, amount, and currency.
- On success, counts CSV lines, writes the control file, gzips the CSV, logs success, and exits.
- On failure, logs the attempt and waits 60 seconds before retrying.

#### Functions
- `log` — timestamped stderr logging
- `fail` — logs failure and exits
- `require_tools` — checks required commands
- `run_export` — queries Oracle into CSV
- `write_control` — writes row-count control file

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
- Shell built-ins/utilities: `getopts`, `whence`, `print`.
- Oracle connection: `CONNECT / AS SYSDBA`.
- Oracle table: `settled_payment`; columns queried: `payment_id`, `account_no`, `amount`, `currency`, filtered by `business_day`.

#### Errors and exit codes
- `0`: export, control file, and gzip complete.
- `1`: default `fail` status, including inability to create the export directory.
- `2`: invalid option or usage error.
- `3`: missing `sqlplus` or `gzip`.
- `4`: all three export attempts fail.
- Retries failed exports up to three times, sleeping 60 seconds between attempts; no traps.
