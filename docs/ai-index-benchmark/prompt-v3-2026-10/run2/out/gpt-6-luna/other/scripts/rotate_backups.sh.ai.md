### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:13:04Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a timestamped compressed backup of a directory and removes older archives beyond the retention limit.

#### Purpose
- Back up one source directory as a `.tar.gz` archive.
- Retain only the newest configured number of archives for that source.

#### Type
- Bash shell script; executable command-line utility.

#### Input
- Positional argument `$1` / `SOURCE_DIR`: directory to archive.
- Positional argument `$2` / `BACKUP_DIR`: destination for archives.
- Optional positional argument `$3` / `KEEP`: positive archive retention count; defaults to `7`.

#### Output
- Writes `${BACKUP_DIR}/${name}_<UTC timestamp>.tar.gz`.
- Removes older matching archives after creating the new archive.
- Prints the created archive path and size; prints each removed archive path.
- Creates `BACKUP_DIR` if needed.

#### Core logic
- Enables `set -euo pipefail`; requires source and destination arguments.
- Validates `KEEP` against a positive-integer pattern and checks that `SOURCE_DIR` exists.
- Uses the source directory’s basename for the archive name and archives it from its parent directory.
- Writes to a `.partial` path, registers an exit trap to remove that temporary path, then renames it to the final archive.
- Lists matching archives newest first and deletes entries after the first `KEEP`.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` — create and rotate directory backups.
- Internal variables: `SOURCE_DIR`, `BACKUP_DIR`, `KEEP`, `name`, `archive`, `tmp`, `archives`, `old`.
- No functions or `main` entry point; the script body runs on invocation.

#### Constants and configuration
- `KEEP`: default `7`; must match `^[1-9][0-9]*$`.
- Archive timestamp format: UTC `+%Y%m%dT%H%M%SZ`.
- Temporary archive suffix: `.partial`.
- Retention filename pattern: `${name}_*.tar.gz`.

#### Dependencies
- Bash features: `[[ ]]`, arrays, process substitution, `mapfile`, parameter expansion, `trap`.
- External commands: `basename`, `dirname`, `date`, `tar`, `mv`, `du`, `cut`, `ls`, `rm`, `mkdir`, `echo`.

#### Exceptions / Errors
- Missing source or destination argument: parameter expansion emits usage text and exits under `set -u`.
- Invalid `KEEP`: prints an error to stderr and exits with status `2`.
- Missing source directory: prints an error to stderr and exits with status `2`.
- With `set -e`, failures in commands terminate the script; the exit trap removes the temporary archive path.
