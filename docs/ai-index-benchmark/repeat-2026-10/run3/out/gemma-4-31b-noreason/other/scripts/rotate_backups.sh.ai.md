### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:30:53Z
- G: 
- A: 0.0.0
- X: file
---
> Manages directory backups by creating dated compressed archives and pruning old versions to maintain a fixed retention count.

#### Purpose
* Automates the creation of timestamped `.tar.gz` backups for a specific directory.
* Implements a rotation policy to limit the number of stored backup files.

#### Type
Bash script.

#### Input
* `SOURCE_DIR` (Positional 1): Path to the directory to be backed up.
* `BACKUP_DIR` (Positional 2): Destination path for the archive files.
* `KEEP` (Positional 3, Optional): Number of most recent backups to retain (defaults to 7).

#### Output
* A compressed archive file named `${basename}_YYYYMMDDTHHMMSSZ.tar.gz` in `BACKUP_DIR`.
* Console logs indicating created archive size and removed old archives.
* Deletion of backup files exceeding the `KEEP` threshold.

#### Core logic
* Validates that `KEEP` is a positive integer and `SOURCE_DIR` exists.
* Ensures `BACKUP_DIR` exists via `mkdir -p`.
* Generates a UTC timestamped filename.
* Creates a partial archive using `tar -czf` to prevent incomplete backups from being indexed.
* Uses a `trap` to clean up partial files on exit.
* Renames the partial file to the final archive name upon successful completion.
* Lists archives for the specific source directory sorted by modification time (newest first).
* Slices the list of archives and deletes all entries beyond the `KEEP` index.

#### Dependencies
* `tar` (archiving)
* `date` (timestamping)
* `du` (disk usage reporting)
* `ls` (file listing/sorting)
* `basename` / `dirname` (path manipulation)

#### Exceptions / Errors
* Exit 1: Triggered by `set -e` on any command failure.
* Exit 2: Triggered if `KEEP` is not a positive integer or `SOURCE_DIR` is missing.
* Usage error: Triggered if required positional arguments are missing.
