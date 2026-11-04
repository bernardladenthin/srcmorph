### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:48:28Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Extracts settled payments for a specified business day from Oracle, writes a CSV, creates a control file, and gzips the result.

#### Purpose
- Performs nightly export of settled payments.
- Runs as a standalone script.

#### Type
- `ksh93`
- Uses `set -e`? none; uses `typeset` and `whence`.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → sets `business_day`.
  - `-o <dir>` → sets `export_dir`.
- Environment variables:
  - `ORACLE_SID` (default `PAYPRD`).
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`.
- No positional arguments.

#### Output
- Creates `payments_<business_day>.csv` in `export_dir`.
- Generates control file `payments_<business_day>.ctl` with row count.
- Gzips the CSV to `payments_<business_day>.csv.gz`.
- Prints log messages to stderr.
- Exits with status 0 on success, 1–4 on various failures.

#### Core logic
- Parse options; set defaults.
- Ensure `export_dir` exists.
- Attempt export up to `MAX_RETRIES`:
  - Run `run_export` to query Oracle via `sqlplus`.
  - If successful, write control file, gzip CSV, log success, exit.
  - On failure, log, sleep 60, retry.
- On exhausting retries, call `fail` with exit code 4.

#### Functions
- `log` – prints timestamped message to stderr.
- `fail` – logs failure and exits with code.
- `require_tools` – checks for `sqlplus` and `gzip`.
- `run_export` – runs SQL query, writes CSV.
- `write_control` – writes row count and business day to control file.

#### Constants and configuration
- `ORACLE_SID_DEFAULT=PAYPRD`
- `EXPORT_DIR_DEFAULT=/data/export/payments`
- `MAX_RETRIES=3`
- `attempt=0`
- `business_day=$(date +%Y%m%d)`
- `export_dir=$EXPORT_DIR_DEFAULT`

#### Dependencies
- External commands: `sqlplus`, `gzip`.
- Oracle connection via `/ AS SYSDBA`.
- SQL query selects from `settled_payment` where `business_day = TO_DATE('$business_day', 'YYYYMMDD')`.

#### Errors and exit codes
- `fail` exit codes:
  - 1: generic failure (e.g., missing tool).
  - 2: usage error.
  - 3: missing required tool.
  - 4: exceeded retry limit.
