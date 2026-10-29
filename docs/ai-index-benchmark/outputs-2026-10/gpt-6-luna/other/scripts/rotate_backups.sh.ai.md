### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T21:22:07Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated compressed archive of a source directory and removes older archives beyond the configured retention count.

#### Purpose
- Back up one directory as a `.tar.gz` archive and retain only the newest `KEEP` archives for that directory.

#### Type
- Bash shell script; executable entry point.

#### Input
- Positional arguments: `SOURCE_DIR`, `BACKUP_DIR`, optional `KEEP` (defaults to `7`).
- Reads the source directory contents through `tar`.

#### Output
- Writes a UTC-dated `${name}_YYYYMMDDTHHMMSSZ.tar.gz` archive in `BACKUP_DIR`.
- Prints the created archive and its human-readable size; prints each removed archive.

#### Core logic
- Requires source and backup directory arguments; validates `KEEP` as a positive integer and `SOURCE_DIR` as an existing directory.
- Creates `BACKUP_DIR` if needed; derives the archive name from the source directory basename.
- Creates a compressed tar archive of the source directory via its parent directory, writing first to a `.partial` file.
- Removes the partial file on exit; renames it to the final archive after successful tar creation.
- Lists matching archives newest first and deletes all entries after the first `KEEP`.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep=7]` — create archive and prune older backups.

#### Dependencies
- Bash built-ins: `basename`, `date`, `dirname`, `mapfile`, `set`, `trap`.
- External commands: `tar`, `mkdir`, `mv`, `du`, `cut`, `ls`, `rm`.

#### Exceptions / Errors
- Exits with status `2` if required arguments are missing, `KEEP` is not a positive integer, or `SOURCE_DIR` does not exist.
- `set -euo pipefail` makes unset-variable use and failing commands terminate the script; the exit trap removes the partial archive.
