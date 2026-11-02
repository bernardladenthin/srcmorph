### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:27:27Z
- G: 
- A: 0.0.0
- X: file
---
> Exports one business day of settled Oracle payments to a gzipped CSV with a neighboring row-count control file.

#### Purpose
- Creates a nightly extract of settled payments for a selected business day.
- Runs directly; it is not a sourced library.

#### Type
- `ksh93` via `#!/bin/ksh93`; no strict-mode settings.

#### Input
- `-d YYYYMMDD` sets `business_day`; parsed with `getopts`.
- `-o dir` sets `export_dir`; parsed with `getopts`.
- Environment variable `ORACLE_SID` is read, defaulting to `ORACLE_SID_DEFAULT` (`PAYPRD`).
- Reads Oracle table `settled_payment`, filtered by `business_day`.
- Reads no local input files.

#### Output
- Writes `payments_$business_day.csv` under `export_dir`, then compresses it to `.csv.gz`.
- Writes a sibling `.ctl` file containing `rows=<count> business_day=<date>`.
- Logs timestamped status and failure messages to standard error.
- Exits `0` on success; failure statuses are listed below.

#### Core logic
- Sets the default business day from `date +%Y%m%d` and the default export directory.
- Sets `ORACLE_SID` to its existing value or the default, and exports `NLS_LANG=AMERICAN_AMERICA.AL32UTF8`.
- Parses `-d` and `-o`; shifts parsed arguments.
- Requires `sqlplus` and `gzip`; creates `export_dir` if it does not exist.
- Builds the CSV path as `payments_$business_day.csv`.
- Tries the export up to `MAX_RETRIES` times:
  - Runs `sqlplus -s /nolog`, connects as SYSDBA, and selects payment ID, account number, amount, and currency from `settled_payment` for the business day, delimited by semicolons.
  - On success, counts CSV lines, writes the control file, gzips the CSV, logs success, and exits.
  - On failure, logs the attempt and sleeps 60 seconds before retrying.
- Fails after exhausting retries.

#### Functions
- `log` — timestamp and write messages to standard error.
- `fail` — log failure and exit with a supplied or default status.
- `require_tools` — verify required commands are available.
- `run_export` — run the Oracle query and write CSV output.
- `write_control` — count CSV rows and write the control file.

#### Constants and configuration
- `ORACLE_SID_DEFAULT=PAYPRD`
- `EXPORT_DIR_DEFAULT=/data/export/payments`
- `MAX_RETRIES=3`
- `attempt=0`
- `business_day=$(date +%Y%m%d)`
- `export_dir=$EXPORT_DIR_DEFAULT`
- `ORACLE_SID=${ORACLE_SID:-$ORACLE_SID_DEFAULT}`
- `NLS_LANG=AMERICAN_AMERICA.AL32UTF8`
- `opt` is assigned by `getopts` for each parsed option.
- `csv=$export_dir/payments_$business_day.csv`

#### Dependencies
- External commands: `date`, `sqlplus`, `gzip`, `wc`, `mkdir`, `sleep`.
- Shell commands and built-ins: `whence`, `print`, `getopts`.
- Oracle connection: `/ AS SYSDBA`; query reads `settled_payment`.

#### Errors and exit codes
- `2` — invalid or unsupported option; logs the usage string.
- `3` — `sqlplus` or `gzip` is unavailable.
- `4` — all export attempts fail.
- `1` — default status used by `fail` when no status is supplied.
- Export directory creation failure calls `fail` with its default status (`1`).
- Retries failed exports up to 3 times, sleeping 60 seconds between failures; no traps are defined.
