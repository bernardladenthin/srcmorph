### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:06:41Z
- G: 
- A: 0.0.0
- X: file
---
> This script creates dated, compressed backups of a specified directory and automatically prunes older backups to maintain a fixed retention count.

#### Purpose
* Creates compressed, dated archives of a source directory.
* Manages backup rotation by deleting the oldest archives beyond a specified limit.

#### Type
Shell script (Bash)

#### Input
* $1: Source directory path (SOURCE_DIR).
* $2: Backup destination directory path (BACKUP_DIR).
* $3 (Optional): Number of backups to keep (KEEP, defaults to 7).

#### Output
* A new compressed archive file in BACKUP_DIR.
* Deletion messages for removed old archives.

#### Core logic
* Validates input arguments: checks for required directories and ensures KEEP is a positive integer.
* Creates a unique, timestamped archive name based on the source directory name.
* Uses `tar` to create a compressed archive (`.tar.gz`) of the SOURCE_DIR into a temporary file.
* Renames the temporary file to the final archive name.
* Lists all existing archives for the source directory, sorted newest first (`ls -1t`).
* Iterates over the list, deleting all archives starting from the index position equal to KEEP.

#### Public API
rotate_backups.sh <source_dir> <backup_dir> [keep] -> None: Creates and rotates backups.

#### Dependencies
tar, du, ls, date, mapfile

#### Exceptions / Errors
* Exits if SOURCE_DIR or BACKUP_DIR are missing or invalid.
* Exits if KEEP is not a positive integer.
* Handles temporary file cleanup via `trap`.
