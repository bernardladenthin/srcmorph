### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:32:01Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for a business day from Oracle to a gzipped CSV with a control file.

#### Purpose
- Directly runs nightly export of settled payments.
- Produces CSV, gzips it, writes row‑count control file.

#### Type
- `ksh93` shell.
- Uses `typeset`, `print`, `whence`; no strict mode flags.

#### Input
- Options parsed by `getopts "d:o:"`:
  - `-d` sets `business_day` (`$OPTARG`).
  - `-o` sets `export_dir` (`$OPTARG`).
- Environment variables:
  - `ORACLE_SID` (default `PAYPRD`).
  - `NLS_LANG` set to `AMERICAN_AMERICA.AL32UTF8`.
- Default values:
  - `business_day` from `date +%Y%m%d`.
  - `export_dir` from `EXPORT_DIR_DEFAULT`.

#### Output
- Creates `$export_dir/payments_$business_day.csv`.
- Gzips the CSV to `$export_dir/payments_$business_day.csv.gz`.
- Writes control file `$export_dir/payments_$business_day.ctl` with row count.
- Logs status messages to stderr.
- Exit status: `0` on success, `1–4` on various failures.

#### Core logic
- Parse options, set defaults.
- Call `require_tools` to ensure `sqlplus` and `gzip`.
- Ensure `$export_dir` exists or create it.
- Build `csv` path.
- Loop up to `MAX_RETRIES`:
  - Increment `attempt`.
  - Run `run_export "$csv"`.
  - On success: `write_control`, `gzip -f`, log, exit `0`.
  - On failure: log, sleep 60, retry.
- After retries exhausted: call `fail` with exit code `4`.

#### Functions
- `log` – prints timestamped message to stderr.
- `fail` – logs failure, exits with code.
- `require_tools` – verifies `sqlplus` and `gzip`.
- `run_export` – runs `sqlplus` to export data to file.
- `write_control` – writes row count and business day to control file.

#### Constants and configuration
- `ORACLE_SID_DEFAULT=PAYPRD`
- `EXPORT_DIR_DEFAULT=/data/export/payments`
- `MAX_RETRIES=3`
- `attempt=0`
- `business_day=$(date +%Y%m%d)`
- `export_dir=$EXPORT_DIR_DEFAULT`
- `ORACLE_SID=${ORACLE_SID:-$ORACLE_SID_DEFAULT}`
- `NLS_LANG=AMERICAN_AMERICA.AL32UTF8`

#### Dependencies
- External commands: `sqlplus`, `gzip`.
- Oracle connection via `sqlplus` using `/ AS SYSDBA`.

#### Errors and exit codes
- `fail` exit `1`: generic failure.
- `fail` exit `2`: incorrect usage.
- `fail` exit `3`: missing required tool.
- `fail` exit `4`: exceeded maximum retries.
