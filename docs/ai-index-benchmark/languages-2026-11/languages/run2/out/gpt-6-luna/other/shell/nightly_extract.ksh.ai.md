### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:33:09Z
- G: 
- A: 0.0.0
- X: file
---
> Exports one business day of settled Oracle payments to a gzipped CSV with a row-count control file.

#### Purpose
- Nightly extract of settled payments for a selected business day.
- Runs directly; accepts an optional date and export directory.

#### Type
- `ksh93` (`#!/bin/ksh93`); no strict-mode settings.

#### Input
- `-d YYYYMMDD` parsed by `getopts`; sets `business_day`.
- `-o dir` parsed by `getopts`; sets `export_dir`.
- `ORACLE_SID`; defaults to `ORACLE_SID_DEFAULT` when unset or empty.
- Reads the current date via `date +%Y%m%d`.
- Reads Oracle table `settled_payment` through `sqlplus`.

#### Output
- Writes `$export_dir/payments_$business_day.csv`, then compresses it to `.csv.gz`; `gzip -f` replaces an existing gzip file.
- Writes `$export_dir/payments_$business_day.ctl` containing `rows=<count> business_day=<date>`.
- Logs timestamped status messages to standard error.
- Exits `0` on reported success; `1` for unspecified `fail` errors, `2` for usage errors, `3` for missing tools, and `4` after retries are exhausted.

#### Core logic
- Sets the default business day to today and the export directory to `/data/export/payments`; exports Oracle session settings.
- Parses `-d` and `-o`; shifts parsed arguments and ignores any remaining positional arguments.
- Checks for `sqlplus` and `gzip`; creates the export directory if needed.
- Attempts the Oracle query up to three times, with a 60-second sleep after each failed attempt.
- On successful `sqlplus` execution, counts CSV lines, writes the control file, gzips the CSV, logs success, and exits `0`.
- Logs each failed attempt; after the retry limit, exits through `fail`.

#### Functions
- `log` — timestamped stderr logging.
- `fail` — logs failure and exits with a supplied or default status.
- `require_tools` — checks required commands.
- `run_export` — queries Oracle for the selected day’s settled payments.
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
- `opt` receives parsed option letters.
- `csv=$export_dir/payments_$business_day.csv`

#### Dependencies
- External commands: `date`, `sqlplus`, `wc`, `gzip`, `mkdir`, `sleep`.
- Shell commands/builtins: `whence`, `print`.
- Oracle connection: `CONNECT / AS SYSDBA`.
- Oracle table: `settled_payment`; query filters `business_day` and selects `payment_id`, `account_no`, `amount`, and `currency`.

#### Errors and exit codes
- Exit `1`: `fail` called without an explicit status.
- Exit `2`: unrecognized or malformed option triggers usage failure.
- Exit `3`: `sqlplus` or `gzip` is unavailable.
- Exit `4`: all three export attempts fail.
- Directory creation failure calls `fail` with its default status (`1`).
- Retries failed exports up to three times, sleeping 60 seconds between attempts; no trap is defined.
