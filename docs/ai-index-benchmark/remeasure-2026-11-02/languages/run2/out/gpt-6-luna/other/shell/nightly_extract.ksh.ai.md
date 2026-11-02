### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:40:28Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Exports settled payments for a business day from Oracle to a gzipped CSV with a row-count control file.

#### Purpose
- Produces a daily settled-payment export for downstream processing.
- Runs directly as a nightly command-line script.

#### Type
- `ksh93` (`#!/bin/ksh93`); no strict-mode settings (`set -e`, `set -u`, or `pipefail`).

#### Input
- `-d` via `getopts`: sets `business_day` to the requested `YYYYMMDD`; defaults to the current date.
- `-o` via `getopts`: sets `export_dir`; defaults to `/data/export/payments`.
- `ORACLE_SID`: read from the environment; defaults to `PAYPRD`.
- Reads no input files or directories.

#### Output
- Writes `payments_<business_day>.csv` in `export_dir`, then compresses it to `.csv.gz`.
- Writes `<export_dir>/payments_<business_day>.ctl` with the row count and business day.
- Logs timestamped messages to standard error.
- Creates `export_dir` if it does not exist.
- Exit statuses: `0` on reported success; `1` for unspecified `fail` calls; `2` for usage errors; `3` for missing tools; `4` after exhausting retries.

#### Core logic
- Parses `-d` and `-o` with `getopts`; shifts parsed arguments.
- Requires `sqlplus` and `gzip`; creates `export_dir` if needed.
- Builds the CSV path as `payments_<business_day>.csv`.
- Up to three times, runs Oracle SQL through `sqlplus -s /nolog`, selecting `payment_id`, `account_no`, `amount`, and `currency` from `settled_payment` for the requested business day, with semicolon-separated fields.
- On a successful export, counts CSV lines with `wc -l`, writes the `.ctl` file, gzips the CSV, logs success, and exits `0`.
- On export failure, logs the attempt and sleeps 60 seconds before retrying; after three failures, exits through `fail`.

#### Functions
- `log`: Timestamped stderr logging.
- `fail`: Logs failure and exits with a status.
- `require_tools`: Checks required commands are available.
- `run_export`: Runs the Oracle query and writes CSV output.
- `write_control`: Counts CSV rows and writes the control file.

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
- Shell command lookup: `whence`.
- Oracle connection: `CONNECT / AS SYSDBA`.
- Oracle table: `settled_payment`.

#### Errors and exit codes
- `fail` defaults to exit status `1` if no status is supplied.
- Missing `sqlplus` or `gzip`: status `3`.
- Invalid option handling: status `2`.
- Failure to create `export_dir`: calls `fail` with its default status `1`.
- Failed export attempts retry up to `MAX_RETRIES`, sleeping 60 seconds between attempts; exhaustion exits with status `4`.
- No traps are defined.
