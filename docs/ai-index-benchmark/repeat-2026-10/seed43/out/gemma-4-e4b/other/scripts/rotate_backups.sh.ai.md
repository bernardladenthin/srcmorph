### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:59:36Z
- G: 
- A: 0.0.0
- X: file
---
> This script creates compressed, dated backups of a specified directory and automatically prunes older backups to maintain a defined retention count.

#### Purpose
* Creates compressed tarball backups of a source directory.
* Manages backup rotation by deleting older archives based on a retention count.

#### Type
Shell Script (Bash)

#### Input
* $1: `SOURCE_DIR` (Directory to back up).
* $2: `BACKUP_DIR` (Directory where backups are stored).
* $3: `KEEP` (Optional integer specifying number of backups to retain, defaults to 7).

#### Output
* Creates a dated, compressed archive file in `BACKUP_DIR`.
* Deletes specified older archive files from `BACKUP_DIR`.

#### Core logic
* Validates input arguments: checks for required directories and ensures `KEEP` is a positive integer.
* Creates a unique archive filename using the source directory name and UTC timestamp.
* Executes `tar` to create a compressed archive (`.tar.gz`) of the `SOURCE_DIR` into a temporary file.
* Renames the temporary file to the final archive name.
* Lists all existing archives matching the source directory pattern in reverse chronological order.
* Iterates through the list, deleting all archives beyond the count specified by `KEEP`.

#### Public API
rotate_backups.sh <source_dir> <backup_dir> [keep] -> None: Creates and rotates backups.

#### Dependencies
tar, du, ls, date, mapfile

#### Exceptions / Errors
* Exits if required arguments are missing.
* Exits if `KEEP` is not a positive integer.
* Exits if `SOURCE_DIR` does not exist.
