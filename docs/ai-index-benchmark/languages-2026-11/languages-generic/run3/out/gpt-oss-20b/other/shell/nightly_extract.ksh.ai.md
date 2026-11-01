### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:40:54Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for a business day from Oracle to a gzipped CSV with a control file.

#### Purpose
- Generates a daily CSV of settled payments.
- Creates a row‑count control file.
- Retries export up to a maximum number of attempts.

#### Type
- Shell script (ksh93).

#### Input
- `-d YYYYMMDD`: business day (default current date).
- `-o <dir>`: export directory (default `/data/export/payments`).
- Environment: `ORACLE_SID`, `NLS_LANG`.

#### Output
- `payments_<YYYYMMDD>.csv.gz` in the export directory.
- `<csv>.ctl` control file with `rows=` and `business_day=`.

#### Core logic
- Parse options, set defaults.
- Verify required tools (`sqlplus`, `gzip`).
- Ensure export directory exists.
- Loop up to `MAX_RETRIES`:
  - Run `sqlplus` to spool query results to CSV.
  - If successful, write control file, gzip CSV, log success, exit.
  - On failure, log, wait 60 s, retry.
- On exhausting retries, exit with error.

#### Public API
- `log(msg)` – prints timestamped message to stderr.
- `fail(msg, code)` – logs failure and exits.
- `require_tools()` – checks for `sqlplus` and `gzip`.
- `run_export(outfile)` – runs SQL query and writes CSV.
- `write_control(csv)` – writes control file with row count.

#### Constants and configuration
- `ORACLE_SID_DEFAULT=PAYPRD`
- `EXPORT_DIR_DEFAULT=/data/export/payments`
- `MAX_RETRIES=3`

#### Dependencies
- `sqlplus` (Oracle client).
- `gzip`.
- `date`, `wc`, `print`, `whence`, `mkdir`, `sleep`.

#### Exceptions / Errors
- Missing required tools → exit code 3.
- Cannot create export directory → exit code 1.
- Exceeding retry limit → exit code 4.
