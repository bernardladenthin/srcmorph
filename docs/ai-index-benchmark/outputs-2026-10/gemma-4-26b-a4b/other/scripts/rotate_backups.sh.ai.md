### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:59:26Z
- G: 
- A: 0.0.0
- X: file
---
> Creates compressed, timestamped directory backups and maintains a fixed number of recent archives.

#### Purpose
* Automates directory archiving using `tar` with Gzip compression.
* Implements a retention policy by deleting older backup files.

#### Type
Shell script (Bash)

#### Input
* `SOURCE_DIR`: The directory to be backed up.
* `BACKUP_DIR`: The destination directory for archives.
* `KEEP`: Number of recent archives to retain (defaults to 7).

#### Output
* A compressed `.tar.gz` archive in `BACKUP_DIR`.
* Side effect: Deletion of files in `BACKUP_DIR` exceeding the `KEEP` threshold.
* Console output: Status of creation and removal.

#### Core logic
* Validates that `KEEP` is a positive integer and `SOURCE_DIR` exists.
* Generates a UTC timestamped filename using the source directory's basename.
* Creates a partial archive file (`.partial`) to prevent corrupted backups on interruption.
* Uses `tar` to compress the source directory.
* Lists existing archives in `BACKUP_DIR` sorted by modification time (newest first).
* Iterates through the list starting at index `KEEP` to remove outdated archives.

#### Public API
`rotate_backups.sh(source_dir, backup_dir, [keep]) -> void` Executes the backup and rotation process.

#### Exceptions / Errors
* Exits with code 2 if `KEEP` is not a positive integer.
* Exits with code 2 if `SOURCE_DIR` does not exist.
* Uses `set -e` to terminate on any command failure.
* Uses `trap` to clean up partial files on exit.

#### Dependencies
* `tar`
* `date`
* `du`
* `ls`
* `mkdir`
* `rm`
