### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:34:43Z
- G: 
- A: 0.0.0
- X: file
---
> Exports settled payments for a business day from Oracle to a gzipped CSV with a row‑count control file.

#### Purpose
- Generate daily payment export for downstream processing.
- Provide a control file indicating row count and business day.

#### Type
- Shell script (ksh93) – executable.

#### Input
- Optional `-d YYYYMMDD` to override default business day (`date +%Y%m%d`).
- Optional `-o <dir>` to override default export directory (`/data/export/payments`).
- Environment variables: `ORACLE_SID`, `NLS_LANG` (set to `AMERICAN_AMERICA.AL32UTF8`).

#### Output
- Gzipped CSV file: `<export_dir>/payments_<business_day>.csv.gz`.
- Control file: `<export_dir>/payments_<business_day>.ctl` containing `rows=` and `business_day=`.

#### Core logic
- Parse options; set `business_day` and `export_dir`.
- Ensure required tools (`sqlplus`, `gzip`) are available.
- Create export directory if missing.
- Attempt up to `MAX_RETRIES` (3) to:
  1. Run `sqlplus` to spool query results to a CSV.
  2. Count rows with `wc -l` and write control file.
  3. Gzip the CSV.
  4. Log success and exit.
- On failure, log attempt, sleep 60s, retry.
- After retries exhausted, exit with error.

#### Public API
- `log(message)` – prints timestamped log to stderr.
- `fail(message, exit_code)` – logs failure and exits.
- `require_tools()` – verifies `sqlplus` and `gzip`.
- `run_export(outfile)` – runs SQL query and writes raw CSV.
- `write_control(csv)` – writes control file with row count.

#### Constants and configuration
- `ORACLE_SID_DEFAULT=PAYPRD`
- `EXPORT_DIR_DEFAULT=/data/export/payments`
- `MAX_RETRIES=3`

#### Dependencies
- `sqlplus` (Oracle client)
- `gzip`
- `wc`
- `date`
- `print` (ksh built‑in)

#### Exceptions / Errors
- Missing tools → `fail "missing $tool"`.
- Directory creation failure → `fail "cannot create $export_dir"`.
- Exceeded retries → `fail "giving up after $MAX_RETRIES attempts"`.
