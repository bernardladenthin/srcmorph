### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:16:38Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Exports one business day of settled Oracle payments as a gzipped CSV with a neighboring row-count control file.

#### Purpose
- Produces a daily settled-payments export for a selected business day.
- Runs directly as a nightly command-line script.

#### Type
- `ksh93` (`#!/bin/ksh93`); no strict-mode settings.

#### Input
- `-d YYYYMMDD` via `getopts`: sets `business_day`.
- `-o dir` via `getopts`: sets `export_dir`.
- `ORACLE_SID`: read from the environment; defaults to `PAYPRD`.
- Reads the `settled_payment` table in Oracle.
- Reads the current date via `date +%Y%m%d` when initializing `business_day`.

#### Output
- Creates `export_dir` if absent.
- Writes `payments_<business_day>.csv`, then compresses it to `.csv.gz`.
- Writes a sibling `.ctl` file containing `rows=<count> business_day=<day>`.
- Logs timestamped messages to standard error.
- Exits `0` on success; `1` by default for `fail`; `2` for usage errors; `3` for missing tools; `4` after export retries are exhausted.

#### Core logic
- Parses `-d` and `-o`; shifts parsed arguments. Remaining arguments are not validated.
- Checks that `sqlplus` and `gzip` are available.
- Creates the export directory if needed; fails if creation fails.
- Builds the CSV path as `payments_<business_day>.csv` under `export_dir`.
- Tries up to `MAX_RETRIES` times to run a silent `sqlplus` session, connecting as SYSDBA and selecting payment ID, account number, amount, and currency from `settled_payment` for the requested `business_day`.
- On success, counts CSV lines with `wc -l`, writes the control file, compresses the CSV with `gzip -f`, logs success, and exits.
- On each failed export, logs a retry message and sleeps 60 seconds; after the retry limit, fails with status `4`.

#### Functions
- `log`: Timestamped stderr logging.
- `fail`: Logs failure and exits with a supplied or default status.
- `require_tools`: Checks required commands.
- `run_export`: Runs the Oracle query into the CSV.
- `write_control`: Writes the row-count control file.

#### Constants and configuration
- `ORACLE_SID_DEFAULT=PAYPRD`
- `EXPORT_DIR_DEFAULT=/data/export/payments`
- `MAX_RETRIES=3`
- `attempt=0`
- `business_day=$(date +%Y%m%d)`
- `export_dir=$EXPORT_DIR_DEFAULT`
- `ORACLE_SID=${ORACLE_SID:-$ORACLE_SID_DEFAULT}`
- `NLS_LANG=AMERICAN_AMERICA.AL32UTF8`
- `opt` is set by `getopts` for each parsed option.
- `csv=$export_dir/payments_$business_day.csv`

#### Dependencies
- External commands: `date`, `sqlplus`, `gzip`, `wc`, `mkdir`, `sleep`.
- Oracle SYSDBA connection; query reads `settled_payment`.

#### Errors and exit codes
- `2`: unsupported option; logs the usage string.
- `3`: `sqlplus` or `gzip` is unavailable.
- `1`: directory creation failure, or any other `fail` call without an explicit status.
- `4`: all export attempts fail.
- Retries failed exports up to three times, sleeping 60 seconds after each failed attempt, including the last.
- No traps are defined.
