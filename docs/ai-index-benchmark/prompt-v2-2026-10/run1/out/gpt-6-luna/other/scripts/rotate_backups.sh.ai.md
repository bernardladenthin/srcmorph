### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:33:30Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated compressed archive of a source directory and removes older archives beyond the retention limit.

#### Purpose
- Back up one directory as a `.tar.gz` archive and retain only the newest configured number of archives for that source.

#### Type
- Bash implementation script; invoked as `rotate_backups.sh`.

#### Input
- Positional arguments: source directory, backup directory, optional archive count to keep.
- Reads the source directory’s contents and uses the current UTC time for the archive timestamp.

#### Output
- Creates the backup directory if needed.
- Writes a timestamped `.tar.gz` archive and prints its path and human-readable size.
- Deletes archives beyond the retention limit and prints each removed path.

#### Core logic
- Enables `set -euo pipefail`; requires the first two arguments and defaults the retention count to `7`.
- Validates `KEEP` as a positive integer and verifies `SOURCE_DIR` exists.
- Names the archive from the source directory’s basename and a UTC timestamp; writes first to a `.partial` file.
- Archives the source directory from its parent directory, then renames the temporary file to the final archive path.
- Registers an exit trap to remove the temporary file.
- Lists matching archives newest-first and removes entries after the first `KEEP`.

#### Public API
- `rotate_backups.sh(source_dir, backup_dir, keep=7) -> exit status` — create and rotate backups.
- Internal functions: none.

#### Constants and configuration
- `SOURCE_DIR`: first positional argument; required.
- `BACKUP_DIR`: second positional argument; required.
- `KEEP`: third positional argument; default `7`; must match `^[1-9][0-9]*$`.
- `name`: basename of `SOURCE_DIR`, used in archive names.
- `archive`: `${BACKUP_DIR}/${name}_<UTC timestamp>.tar.gz`.
- `tmp`: `archive` plus `.partial`.
- Timestamp format: `date -u +%Y%m%dT%H%M%SZ`.

#### Dependencies
- Bash builtins: `basename`, `dirname`, `mapfile`, `test` (`[ ]`), `trap`.
- Commands: `date`, `tar`, `mkdir`, `mv`, `echo`, `du`, `cut`, `ls`, `rm`.

#### Exceptions / Errors
- Missing required arguments: parameter expansion prints usage and exits under `set -u`.
- Invalid `KEEP` or missing source directory: prints an error to stderr and exits with status `2`.
- With `set -e`, failing commands terminate the script; the exit trap removes the partial archive.
