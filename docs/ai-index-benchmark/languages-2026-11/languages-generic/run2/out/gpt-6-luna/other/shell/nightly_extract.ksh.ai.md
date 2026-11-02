### nightly_extract.ksh
- H: 1.0
- C: 5A355E24
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:32:54Z
- G: 
- A: 0.0.0
- X: file
---
> Exports one business day of settled payment records from Oracle as a gzipped CSV with a row-count control file.

#### Purpose
- Extracts settled payments for a selected business day into `payments_YYYYMMDD.csv.gz`.
- Writes a neighboring `.ctl` file containing the CSV row count and business day.

#### Type
- KornShell 93 (`ksh93`) script; command-line batch export.

#### Input
- `-d YYYYMMDD`: optional business day; defaults to the current date in `YYYYMMDD` format.
- `-o dir`: optional export directory; defaults to `/data/export/payments`.
- `ORACLE_SID`: environment variable; used if set, otherwise defaults to `PAYPRD`.
- Oracle `settled_payment` rows with `business_day` matching the selected date.

#### Output
- CSV rows containing `payment_id;account_no;amount;currency`, written to `$export_dir/payments_$business_day.csv`.
- Gzip-compressed CSV; `gzip -f` removes the uncompressed CSV after compression.
- Control file `${csv%.csv}.ctl` containing `rows=<count> business_day=<date>`.
- Timestamped status messages on standard error; exits with status 0 on success.

#### Core logic
- Parses `-d` and `-o`, then shifts consumed options.
- Requires `sqlplus` and `gzip`; creates the export directory if absent.
- Runs SQL*Plus as SYSDBA and selects semicolon-delimited fields from `settled_payment` for the requested business day.
- Counts CSV lines with `wc -l` and writes the count and date to the control file.
- Retries export up to three times, sleeping 60 seconds between failed attempts; on success writes the control file, gzips the CSV, logs completion, and exits.

#### Public API
- `log(...) -> status`: writes timestamped messages to standard error.
- `fail(message, status) -> exits`: logs failure and exits with the supplied status or 1.
- `require_tools() -> status`: checks for `sqlplus` and `gzip`.
- `run_export(outfile) -> status`: runs the Oracle query and redirects output to the CSV path.
- `write_control(csv) -> status`: writes row count and business day to the control file.
- Script entry point: parses options, checks tools, creates output directory, and runs the retry loop.

#### Constants and configuration
- `ORACLE_SID_DEFAULT`: `PAYPRD`.
- `EXPORT_DIR_DEFAULT`: `/data/export/payments`.
- `MAX_RETRIES`: `3`.
- `attempt`: initialized to `0`.
- `business_day`: initialized from `date +%Y%m%d`; overridden by `-d`.
- `export_dir`: initialized from `EXPORT_DIR_DEFAULT`; overridden by `-o`.
- `ORACLE_SID`: exported from existing environment value or `ORACLE_SID_DEFAULT`.
- `NLS_LANG`: set to `AMERICAN_AMERICA.AL32UTF8`.
- Retry delay: `60` seconds.
- Exit statuses: usage error `2`; missing tool `3`; exhausted retries `4`; other `fail` calls default to `1`.

#### Dependencies
- External commands: `date`, `whence`, `mkdir`, `sqlplus`, `wc`, `gzip`, `sleep`.
- KornShell built-ins: `print`, `getopts`, `shift`, `exit`.
- Oracle objects: `settled_payment`; columns `payment_id`, `account_no`, `amount`, `currency`, `business_day`.
- Oracle functions: `TO_DATE`.

#### Exceptions / Errors
- Exits with status `3` if `sqlplus` or `gzip` is unavailable.
- Exits with status `2` for invalid options or usage.
- Logs and retries when `run_export` fails; exits with status `4` after three failed attempts.
- Exits with status `1` if the export directory cannot be created.
