### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:30:56Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for a business day from Oracle to a gzipped CSV with a row‑count control file.

#### Purpose
- Generate daily payment export.
- Produce gzipped CSV and control file.

#### Type
- Shell script (ksh93).

#### Input
- `-d YYYYMMDD` (business day, defaults to today).
- `-o <dir>` (export directory, defaults to `/data/export/payments`).
- Environment: `ORACLE_SID`, `NLS_LANG`.

#### Output
- `payments_<YYYYMMDD>.csv.gz` in export directory.
- `<YYYYMMDD>.ctl` control file with row count and business day.
- Log messages to stderr.

#### Core logic
- Validate required tools (`sqlplus`, `gzip`).
- Create export directory if missing.
- Build CSV via `sqlplus` query on `settled_payment`.
- Count rows with `wc -l` and write control file.
- Compress CSV with `gzip -f`.
- Retry up to `MAX_RETRIES` on failure, sleeping 60 s between attempts.

#### Public API
- `log(msg)` – prints timestamped log to stderr.
- `fail(msg, exit_code)` – logs failure and exits.
- `require_tools()` – ensures `sqlplus` and `gzip` are available.
- `run_export(outfile)` – runs SQL query, writes to `outfile`.
- `write_control(csv)` – writes control file with row count.
- Command‑line options: `-d YYYYMMDD`, `-o <dir>`.

#### Constants and configuration
- `ORACLE_SID_DEFAULT=PAYPRD`
- `EXPORT_DIR_DEFAULT=/data/export/payments`
- `MAX_RETRIES=3`

#### Dependencies
- `sqlplus` (Oracle client).
- `gzip`.
- `wc`, `print`, `date`, `mkdir`, `sleep`.

#### Exceptions / Errors
- Missing tools → exit 3.
- Export directory creation failure → exit 1.
- Exceeding max retries → exit 4.
