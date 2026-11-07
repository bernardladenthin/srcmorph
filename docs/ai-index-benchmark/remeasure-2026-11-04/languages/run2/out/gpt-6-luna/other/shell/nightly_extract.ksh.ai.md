### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:01:20Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Exports settled payments for one business day from Oracle into a gzipped CSV with a row-count control file.

#### Purpose
- Produces a daily payment export and adjacent control file for the selected business day.
- Runs directly as a nightly export command; it is not a sourced library.

#### Type
- `ksh93` (`#!/bin/ksh93`); no `set -e`, `set -u`, or `pipefail`.

#### Input
- `-d YYYYMMDD` sets `business_day`; parsed by `getopts`.
- `-o dir` sets `export_dir`; parsed by `getopts`.
- `ORACLE_SID` environment variable; defaults to `PAYPRD`.
- Reads the Oracle `settled_payment` table. Defaults the business day using `date +%Y%m%d`.
- Reads `OPTARG` and `OPTIND` through `getopts`; no input files are read.

#### Output
- Creates `export_dir` if missing.
- Writes `payments_<business_day>.csv`, then gzips it with `gzip -f` and removes the uncompressed CSV on successful gzip.
- Writes `payments_<business_day>.ctl` containing `rows=<count> business_day=<business_day>`.
- Logs timestamped status messages to standard error.
- Explicit exit statuses: `0` after export, `2` for invalid options, `3` for a missing required tool, `4` after retries are exhausted; `fail` otherwise defaults to `1`.

#### Core logic
- Parses optional business-day and output-directory arguments.
- Sets Oracle environment: preserves `ORACLE_SID` if provided, otherwise uses `PAYPRD`; sets `NLS_LANG=AMERICAN_AMERICA.AL32UTF8`.
- Checks that `sqlplus` and `gzip` are available.
- Ensures the output directory exists and sets the CSV path.
- Tries the export up to three times: runs a SQL query for payments matching `business_day`, writes the control file from the CSV line count, gzips the CSV, logs success, and exits.
- On export failure, logs the attempt and sleeps 60 seconds before retrying; after all attempts, exits with status `4`.

#### Functions
- `log` — writes timestamped messages to standard error.
- `fail` — logs failure and exits with a supplied or default status.
- `require_tools` — checks for required commands.
- `run_export` — queries Oracle and writes CSV output.
- `write_control` — writes the row count and business day.

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
- Uses `whence` to check command availability and `print` for logging and control-file output.
- Oracle connection: `CONNECT / AS SYSDBA`; query reads `settled_payment` columns `payment_id`, `account_no`, `amount`, `currency`, filtered by `business_day`.
- No sourced files.

#### Errors and exit codes
- Exit `2`: unrecognized option; prints usage through `fail`.
- Exit `3`: `sqlplus` or `gzip` is unavailable.
- Exit `4`: all three export attempts fail.
- Exit `1` by default for other calls to `fail`, including output-directory creation failure.
- Retries failed `run_export` calls three times, sleeping 60 seconds between failed attempts.
- No traps.
