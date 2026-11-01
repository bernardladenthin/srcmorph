### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:55:57Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for a specified business day from Oracle to a gzipped CSV with a row‑count control file.

#### Purpose
- Run nightly to export settled payments.
- Executed directly (`./nightly_extract.ksh`).

#### Type
- `ksh93` (shebang `#!/bin/ksh93`).
- Uses `set -e`? none; relies on `whence -q` and explicit exits.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → sets `business_day`.
  - `-o <dir>` → sets `export_dir`.
- Environment variables read:
  - `ORACLE_SID` (defaults to `PAYPRD`).
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`.
- No positional arguments.

#### Output
- Creates `$export_dir/payments_$business_day.csv` (plain text).
- Creates `$export_dir/payments_$business_day.csv.ctl` with row count.
- Gzips the CSV to `$export_dir/payments_$business_day.csv.gz`.
- Prints log messages to stderr.
- Exits with status 0 on success, 1–4 on various failures.

#### Core logic
- Parse options; set defaults.
- Ensure `sqlplus` and `gzip` are available (`require_tools`).
- Create export directory if missing.
- Build CSV path.
- Retry loop (max 3 attempts):
  - Run `run_export` to spool SQL query output to CSV.
  - On success: `write_control`, gzip CSV, log success, exit 0.
  - On failure: log, sleep 60, retry.
- After retries exhausted, `fail` with exit 4.

#### Functions
- `log` – prints timestamped message to stderr.
- `fail` – logs failure and exits with given code.
- `require_tools` – checks `sqlplus` and `gzip` availability.
- `run_export` – runs SQL query via `sqlplus`, writes to given file.
- `write_control` – writes row count and business day to `.ctl` file.

#### Constants and configuration
- ``ORACLE_SID_DEFAULT=PAYPRD``
- ``EXPORT_DIR_DEFAULT=/data/export/payments``
- ``MAX_RETRIES=3``
- ``attempt=0``
- ``business_day=$(date +%Y%m%d)``
- ``export_dir=$EXPORT_DIR_DEFAULT``

#### Dependencies
- External commands: `sqlplus`, `gzip`.
- Oracle database connection via `sqlplus / AS SYSDBA`.
- SQL query reads from table `settled_payment`.

#### Errors and exit codes
- `fail` exit codes:
  - 1: generic failure (e.g., missing tool).
  - 2: usage error.
  - 3: missing required tool.
  - 4: exceeded retry limit.
- Traps: none defined.
