### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:51:11Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated compressed archive of a source directory and removes older archives beyond the configured retention count.

#### Purpose
- Back up one directory as a `.tar.gz` archive.
- Retain only the newest configured number of archives for that directory.

#### Type
- Bash script; executable command-line utility.

#### Input
- Positional arguments: `source_dir`, `backup_dir`, optional `keep` (defaults to `7`).
- Reads the source directory’s basename and contents.

#### Output
- Writes a UTC-timestamped archive: `<backup_dir>/<source_basename>_YYYYMMDDTHHMMSSZ.tar.gz`.
- Prints the created archive path and human-readable size; prints each removed archive.

#### Core logic
- Enables `set -euo pipefail` for strict shell error handling.
- Requires source and backup directory arguments; validates `keep` as a positive integer.
- Exits with status `2` if `keep` is invalid or the source directory does not exist.
- Creates the backup directory if needed.
- Creates a temporary `.partial` archive using `tar`, then renames it to the final archive path.
- Registers an exit trap to remove the temporary archive.
- Lists matching archives newest-first and deletes entries after the first `keep`.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` — create backup and rotate archives.

#### Dependencies
- External commands: `basename`, `date`, `du`, `cut`, `mkdir`, `tar`, `mv`, `ls`, `rm`.
- Bash features: `mapfile`, process substitution, arrays, `[[ ... =~ ... ]]`.

#### Exceptions / Errors
- Missing required arguments trigger usage errors through parameter expansion.
- Invalid `keep` or missing source directory prints an error to stderr and exits with status `2`.
- Strict shell mode exits on command failures; the exit trap removes the temporary archive.
