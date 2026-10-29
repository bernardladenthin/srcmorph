### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T09:54:35Z
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
* `KEEP` (Positional 3, Optional): Number of recent backups to retain (defaults to 7).

#### Output
* A compressed archive file named `${basename}_YYYYMMDDTHHMMSSZ.tar.gz` in `BACKUP_DIR`.
* Console logs indicating created archive size and removed old archives.
* Deletion of backup files exceeding the `KEEP` threshold.

#### Core logic
* Validates that `KEEP` is a positive integer and `SOURCE_DIR` exists.
* Ensures `BACKUP_DIR` exists via `mkdir -p`.
* Generates a UTC timestamped filename.
* Creates a partial archive using `tar -czf` to prevent incomplete backups from being indexed.
* Atomically moves the partial file to the final archive name.
* Lists archives matching the source name pattern, sorted by modification time (newest first).
* Slices the list of archives and deletes all files beyond the `KEEP` index.

#### Public API
* `rotate_backups.sh <source_dir> <backup_dir> [keep]` -> exitCode: Executes the backup and rotation process.

#### Dependencies
* `tar`: Archive creation.
* `date`: Timestamp generation.
* `du`: File size calculation.
* `ls`: File listing and sorting.
* `basename`/`dirname`: Path manipulation.

#### Exceptions / Errors
* Exit 2: Triggered if `KEEP` is not a positive integer.
* Exit 2: Triggered if `SOURCE_DIR` is not a directory.
* `set -euo pipefail`: Ensures script terminates on any command failure or undefined variable.
