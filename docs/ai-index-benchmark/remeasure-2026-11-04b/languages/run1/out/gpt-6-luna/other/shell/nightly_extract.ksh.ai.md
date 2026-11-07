### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:13:11Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Exports one business day of settled Oracle payments as a gzipped CSV with a companion row-count control file.

#### Purpose
- Produce a daily settled-payments export for downstream processing.
- Run directly as a nightly job; not a sourced library.

#### Type
- ksh93 (`#!/bin/ksh93`); no strict-mode settings.

#### Input
- `-d YYYYMMDD` parsed by `getopts`: sets `business_day`; defaults to the current date in `YYYYMMDD` format.
- `-o dir` parsed by `getopts`: sets `export_dir`; defaults to `/data/export/payments`.
- Environment variable `ORACLE_SID`: used if set; otherwise defaults to `PAYPRD`.
- Reads Oracle table `settled_payment`; no local input files.

#### Output
- Writes `payments_<business_day>.csv` in `export_dir`, then compresses it to `.csv.gz`.
- Writes a sibling `.ctl` file containing the CSV row count and business day.
- Logs timestamped status and failure messages to standard error.
- Exits `0` on success; `2` for invalid usage, `3` for missing required tools, `4` after exhausting export retries, or the default `1` for other `fail` calls.

#### Core logic
- Sets defaults, derives the current business day, and exports `ORACLE_SID` and `NLS_LANG`.
- Parses `-d` and `-o`; shifts parsed arguments.
- Checks that `sqlplus` and `gzip` are available; creates `export_dir` if needed.
- Attempts the Oracle export up to three times, sleeping 60 seconds after each failed attempt.
- Runs a SQL*Plus query for the selected `business_day`, writing semicolon-separated payment ID, account number, amount, and currency fields to the CSV.
- On success, counts CSV lines, writes the `.ctl` file, gzips the CSV, logs success, and exits.
- After all attempts fail, logs a final failure and exits `4`.

#### Functions
- `log`: Write timestamped messages to standard error.
- `fail`: Log failure and exit with a status.
- `require_tools`: Check required commands are available.
- `run_export`: Query settled payments into a CSV.
- `write_control`: Write row count and business day to a control file.

#### Constants and configuration
- `ORACLE_SID_DEFAULT=PAYPRD`
- `EXPORT_DIR_DEFAULT=/data/export/payments`
- `MAX_RETRIES=3`
- `attempt=0`
- `business_day=$(date +%Y%m%d)`
- `export_dir=$EXPORT_DIR_DEFAULT`
- `ORACLE_SID=${ORACLE_SID:-$ORACLE_SID_DEFAULT}` (exported)
- `NLS_LANG=AMERICAN_AMERICA.AL32UTF8` (exported)

#### Dependencies
- Commands: `date`, `whence`, `sqlplus`, `wc`, `gzip`, `mkdir`, `sleep`.
- Oracle connection: `/ AS SYSDBA`; query reads `settled_payment` and filters `business_day` using `TO_DATE(..., 'YYYYMMDD')`.

#### Errors and exit codes
- Exit `2`: unsupported option or usage error.
- Exit `3`: `sqlplus` or `gzip` is unavailable.
- Exit `4`: all three export attempts fail; waits 60 seconds between failed attempts.
- Exit `1` by default for `fail` calls without an explicit status; also used if output directory creation fails.
- No traps or cleanup handlers.
