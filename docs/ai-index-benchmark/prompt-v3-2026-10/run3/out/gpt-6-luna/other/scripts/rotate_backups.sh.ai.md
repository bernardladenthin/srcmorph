### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:37:42Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a timestamped compressed archive of a directory and removes older archives beyond the retention limit.

#### Purpose
- Back up one source directory as a `.tar.gz` archive.
- Retain only the newest configured number of archives for that directory.

#### Type
- Bash shell script; executable command-line script.

#### Input
- Positional argument `$1` / `SOURCE_DIR`: directory to archive.
- Positional argument `$2` / `BACKUP_DIR`: directory for backup archives.
- Optional positional argument `$3` / `KEEP`: positive archive retention count; defaults to `7`.

#### Output
- Writes a UTC-dated `${name}_YYYYMMDDTHHMMSSZ.tar.gz` archive in `BACKUP_DIR`.
- Prints the created archive path and human-readable size; prints each removed archive path.
- Deletes older matching archives beyond the first `KEEP`.

#### Core logic
- Requires `SOURCE_DIR` and `BACKUP_DIR`; validates `KEEP` against a positive-integer pattern.
- Verifies that `SOURCE_DIR` is a directory and creates `BACKUP_DIR` if needed.
- Uses the source directory’s basename for the archive name and archives it from its parent directory.
- Writes to an `.partial` path, then renames it to the final archive; an exit trap removes the partial file.
- Lists matching archives newest-first and removes entries after the first `KEEP`.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` — create and rotate directory backups.
- Internal entry point: script body; no named functions.

#### Constants and configuration
- `SOURCE_DIR`: required first argument.
- `BACKUP_DIR`: required second argument.
- `KEEP`: optional third argument; default `7`; must match `^[1-9][0-9]*$`.
- `name`: basename of `SOURCE_DIR`.
- `archive`: path `${BACKUP_DIR}/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz`.
- `tmp`: partial archive path `${archive}.partial`.
- `EXIT` trap: removes `tmp`.

#### Dependencies
- Bash features: `set -euo pipefail`, `[[ =~ ]]`, `mapfile`, process substitution, array slicing, `trap`.
- Commands: `basename`, `dirname`, `date`, `tar`, `mkdir`, `mv`, `echo`, `du`, `cut`, `ls`, `rm`.

#### Exceptions / Errors
- Missing required arguments: parameter expansion prints usage and exits under `set -u`.
- Invalid `KEEP`: prints an error to stderr and exits with status `2`.
- Missing source directory: prints an error to stderr and exits with status `2`.
- `set -euo pipefail` makes failures in applicable commands, unset-variable references, or pipelines terminate the script; the exit trap removes the partial archive.
