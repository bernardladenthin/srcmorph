### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:52:36Z
- G: 
- A: 0.0.0
- X: file
---
> This script creates dated, compressed backups of a specified directory and automatically purges older backups to maintain a defined retention count.

#### Purpose
* Creates compressed, dated archives of a source directory.
* Manages backup rotation by deleting the oldest archives beyond a specified limit.

#### Type
Shell script (Bash)

#### Input
* $1: Source directory path (SOURCE_DIR).
* $2: Backup destination directory path (BACKUP_DIR).
* $3: Number of backups to keep (KEEP), defaults to 7.

#### Output
* Creates a new compressed archive file in BACKUP_DIR.
* Deletes specified old archive files from BACKUP_DIR.

#### Core logic
* Validates input arguments: checks for required directories and ensures KEEP is a positive integer.
* Creates a unique, timestamped archive filename based on the source directory name.
* Uses `tar` to create a compressed archive (`.tar.gz`) of the SOURCE_DIR into a temporary file.
* Renames the temporary file to the final archive name.
* Lists all existing archives for the source directory, sorted newest first.
* Iterates over the list, deleting all archives starting from the $(KEEP+1)th entry.

#### Public API
rotate_backups.sh <source_dir> <backup_dir> [keep] -> None: Executes backup and rotation logic.

#### Dependencies
tar, du, ls, mapfile, basename, date

#### Exceptions / Errors
* Exits if SOURCE_DIR or BACKUP_DIR are invalid or missing.
* Exits if KEEP is not a positive integer.
* Handles temporary file cleanup via trap on EXIT.
