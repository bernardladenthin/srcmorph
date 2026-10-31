### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:46:58Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated compressed backup of a directory and removes older archives beyond the retention limit.

#### Purpose
- Back up one source directory as a `.tar.gz` archive.
- Retain only the newest configured number of archives for that source.

#### Type
- Bash shell script; executable command-line utility.

#### Input
- Positional argument `$1`: source directory.
- Positional argument `$2`: backup directory.
- Optional positional argument `$3`: retention count (`KEEP`), defaulting to `7`.

#### Output
- Creates a UTC-timestamped archive at `BACKUP_DIR/<source-basename>_<timestamp>.tar.gz`.
- Prints the created archive path and human-readable size; prints each removed archive path.
- Creates `BACKUP_DIR` if needed and deletes excess matching archives.

#### Core logic
- Enables `set -euo pipefail` for strict shell error handling.
- Requires source and backup directory arguments; validates `KEEP` as a positive integer.
- Verifies that `SOURCE_DIR` exists as a directory, then creates `BACKUP_DIR`.
- Builds an archive name from the source basename and UTC timestamp (`%Y%m%dT%H%M%SZ`).
- Archives the source directory from its parent directory into a `.partial` path, then renames it to the final archive.
- Registers an exit trap to remove the partial archive.
- Lists matching archives newest first and deletes entries after the first `KEEP`.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` — create and rotate directory backups.
- Internal functions: none.

#### Constants and configuration
- `KEEP`: optional retention count; default `7`; must match `^[1-9][0-9]*$`.
- `SOURCE_DIR`: required first argument.
- `BACKUP_DIR`: required second argument.
- Archive timestamp format: `%Y%m%dT%H%M%SZ`, generated in UTC.
- Temporary archive suffix: `.partial`.

#### Dependencies
- Bash features: `set`, `[[ ]]`, `mapfile`, process substitution, parameter expansion, `trap`.
- Commands: `basename`, `dirname`, `date`, `du`, `cut`, `tar`, `mkdir`, `mv`, `ls`, `rm`, `echo`.

#### Exceptions / Errors
- Missing `SOURCE_DIR` or `BACKUP_DIR`: usage error from parameter expansion; strict mode exits.
- Invalid `KEEP`: prints an error to stderr and exits with status `2`.
- Missing source directory: prints an error to stderr and exits with status `2`.
- With strict mode enabled, failing commands can terminate the script; the exit trap removes the partial archive.
