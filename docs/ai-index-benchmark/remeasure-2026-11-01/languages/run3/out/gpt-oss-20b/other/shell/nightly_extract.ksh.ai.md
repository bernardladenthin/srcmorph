### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:57:41Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for a business day from Oracle to a gzipped CSV with a row‑count control file.

#### Purpose
- Generates a daily payment export.
- Runs as a standalone script.

#### Type
- `ksh93`
- `set -e` not used; script relies on explicit checks.

#### Input
- Options parsed by `getopts`:
  - `-d YYYYMMDD` → `business_day`
  - `-o <dir>` → `export_dir`
- Environment variables:
  - `ORACLE_SID` (default `PAYPRD`)
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`
- No positional arguments.

#### Output
- File: `$export_dir/payments_$business_day.csv.gz`
- Control file: `$export_dir/payments_$business_day.ctl` containing `rows=<count> business_day=<date>`
- Console logs to stderr.
- Exit status: `0` on success, `1` on generic failure, `2` on usage error, `3` on missing tool, `4` on retry exhaustion.

#### Core logic
- Parse options; set defaults.
- Ensure `export_dir` exists.
- Attempt up to `MAX_RETRIES` times:
  - Run `run_export` to spool SQL query to CSV.
  - If successful, write control file, gzip CSV, log success, exit.
  - On failure, log, sleep 60, retry.
- On exhausting retries, call `fail`.

#### Functions
- `log` – prints timestamped message to stderr.
- `fail` – logs failure and exits with code.
- `require_tools` – verifies `sqlplus` and `gzip` are available.
- `run_export` – runs SQL query via `sqlplus` and writes to given file.
- `write_control` – counts rows in CSV and writes control file.

#### Constants and configuration
- ``ORACLE_SID_DEFAULT=PAYPRD``
- ``EXPORT_DIR_DEFAULT=/data/export/payments``
- ``MAX_RETRIES=3``
- ``attempt=0``
- ``business_day=$(date +%Y%m%d)``
- ``export_dir=$EXPORT_DIR_DEFAULT``

#### Dependencies
- External commands: `sqlplus`, `gzip`.
- Oracle connection: `CONNECT / AS SYSDBA` within `sqlplus`.
- SQL query reads table `settled_payment`.

#### Errors and exit codes
- `3` – missing required tool (`sqlplus` or `gzip`).
- `2` – incorrect usage.
- `4` – exceeded maximum retries.
- `1` – generic failure (e.g., cannot create export directory).
