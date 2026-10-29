### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:05:10Z
- G: 
- A: 0.0.0
- X: file
---
> Rotates and purges old compressed backup archives for a specified source directory, retaining only the newest N copies.

#### Purpose
*   Creates a dated compressed tar.gz backup of the source directory.
*   Deletes older backup archives beyond the specified retention count.

#### Type
Shell script (Bash).

#### Input
*   `source_dir`: Path to the directory to backup.
*   `backup_dir`: Path to the directory storing backups.
*   `keep` (optional): Positive integer specifying number of archives to retain.

#### Output
*   `archive`: New compressed backup file path (e.g., `backup_dir/name_YYYYMMDDTHHMMSSZ.tar.gz`).
*   `stdout`: Confirmation message with archive size.
*   `stdout`: List of removed archive paths.

#### Core logic
*   Validates `keep` argument is a positive integer; exits with error if invalid.
*   Verifies `source_dir` exists as a directory; exits with error if missing.
*   Constructs archive filename using source basename and UTC timestamp.
*   Creates temporary file, archives source directory contents, replaces temp with final archive.
*   Lists existing archives for the source sorted by modification time (newest first).
*   Iterates through list, removing archives starting from index `keep` onwards.

#### Public API
`rotate_backups.sh <source_dir> <backup_dir> [keep]` -> Exit code 0 on success, 2 on validation failure.

#### Dependencies
*   `tar` (GNU tar)
*   `ls`
*   `date`
*   `du`
*   `cut`
*   `mapfile` (Bash 4+)

#### Exceptions / Errors
*   Exit code 2: Invalid `keep` argument (not a positive integer).
*   Exit code 2: Source directory does not exist.
