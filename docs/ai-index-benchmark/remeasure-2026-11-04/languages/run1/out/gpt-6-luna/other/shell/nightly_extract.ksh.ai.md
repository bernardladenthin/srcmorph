### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:58:11Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Exports settled payments for one business day from Oracle into a gzipped CSV with a row-count control file.

#### Purpose
- Creates a daily settled-payment export and companion row-count file.
- Runs directly as a nightly job; not a sourced library.

#### Type
- `ksh93` (`#!/bin/ksh93`); no strict-mode settings (`set -e`, `set -u`, or `pipefail`).

#### Input
- `-d` sets `business_day` to the requested `YYYYMMDD`; parsed with `getopts`.
- `-o` sets `export_dir`; parsed with `getopts`.
- Reads `ORACLE_SID`, defaulting to `ORACLE_SID_DEFAULT` (`PAYPRD`).
- Reads no input files.

#### Output
- Creates `export_dir` if needed.
- Writes `payments_$business_day.csv`, then compresses it to `.csv.gz`; writes the row count and business day to the corresponding `.ctl` file.
- Logs timestamped status messages to stderr.
- Exits `0` on successful export; exits `2` for invalid options, `3` when a required tool is missing, `4` after retries are exhausted, or `1` if `fail` is called without an explicit status.

#### Core logic
- Defaults `business_day` to `date +%Y%m%d` and `export_dir` to `/data/export/payments`; sets the Oracle environment.
- Parses `-d` and `-o`, then checks that `sqlplus` and `gzip` are available.
- Creates the export directory if it does not exist; targets `payments_$business_day.csv`.
- Tries the export up to three times: connects through `sqlplus` as SYSDBA and selects payment ID, account number, amount, and currency from `settled_payment` for the requested business day.
- On success, counts CSV lines, writes the `.ctl` file, compresses the CSV with `gzip -f`, logs success, and exits `0`.
- On failure, logs the attempt and sleeps 60 seconds; after exhausting retries, exits `4`.

#### Functions
- `log` — timestamped stderr logging.
- `fail` — logs failure and exits.
- `require_tools` — checks required commands.
- `run_export` — runs the Oracle payment query into the CSV.
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
- External commands: `date`, `sqlplus`, `gzip`, `wc`, `sleep`.
- Shell commands/builtins: `whence`, `mkdir`, `print`.
- Oracle connection: `/ AS SYSDBA`.
- Table read: `settled_payment` (`payment_id`, `account_no`, `amount`, `currency`, `business_day`).

#### Errors and exit codes
- `2` — unrecognized option; prints usage.
- `3` — `sqlplus` or `gzip` is unavailable.
- `1` — export directory creation fails.
- Export retries up to `MAX_RETRIES=3`, sleeping 60 seconds after each failed attempt; `4` if all attempts fail.
- `0` — export, control-file write, and compression succeed.
- No traps.
