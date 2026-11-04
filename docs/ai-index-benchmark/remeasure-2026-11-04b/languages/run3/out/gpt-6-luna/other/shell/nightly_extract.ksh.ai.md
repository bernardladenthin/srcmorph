### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:20:02Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Exports a business day’s settled Oracle payments as a gzipped CSV with a row-count control file.

#### Purpose
- Produces a nightly export of settled payments for one business day.
- Runs directly as a command-line script.

#### Type
- `ksh93` (`#!/bin/ksh93`); no strict-mode settings (`set -e`, `set -u`, or `pipefail`).

#### Input
- `-d` sets `business_day` to the requested `YYYYMMDD`; parsed with `getopts`.
- `-o` sets `export_dir`; parsed with `getopts`.
- `ORACLE_SID` environment variable; defaults to `PAYPRD`.
- Reads the `settled_payment` table in Oracle, filtering `business_day`.

#### Output
- Creates `export_dir` if it does not exist.
- Writes `payments_$business_day.csv`, then compresses it with `gzip -f` to `.csv.gz`.
- Writes a sibling `.ctl` file containing the CSV row count and business day.
- Logs timestamped messages to standard error.
- Exits `0` after a successful export; failure statuses are listed below.

#### Core logic
- Defaults `business_day` to the current date in `YYYYMMDD` format and `export_dir` to the configured export directory.
- Sets Oracle client environment variables and parses `-d` and `-o`.
- Checks that `sqlplus` and `gzip` are available; creates the export directory if needed.
- Retries the SQL export up to three times, sleeping 60 seconds after each failed attempt.
- Runs `sqlplus -s /nolog`, connects as SYSDBA, and selects payment ID, account number, amount, and currency as semicolon-separated fields from `settled_payment`.
- Counts CSV lines, writes the control file, gzips the CSV, logs success, and exits; after all retries fail, reports failure.

#### Functions
- `log` — Timestamp and print messages to standard error.
- `fail` — Log failure and exit with a status.
- `require_tools` — Check required commands are available.
- `run_export` — Query Oracle and write the CSV.
- `write_control` — Write row count and business day to the control file.

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
- Uses `whence` to check command availability.
- Oracle connection: `CONNECT / AS SYSDBA`.
- Oracle table: `settled_payment`.

#### Errors and exit codes
- `2` — Unsupported or invalid option; logs usage.
- `3` — `sqlplus` or `gzip` is unavailable.
- `4` — All three export attempts fail.
- `1` by default — `fail` calls without an explicit status, including failure to create the export directory.
- `0` — Export and control-file generation complete; `gzip` status is not checked before success is reported.
- Retries failed exports up to three times with a 60-second sleep between attempts; no traps.
