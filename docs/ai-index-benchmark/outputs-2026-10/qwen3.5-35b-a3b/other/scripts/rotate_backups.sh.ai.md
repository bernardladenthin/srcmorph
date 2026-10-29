### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:38:27Z
- G: 
- A: 0.0.0
- X: file
---
> Creates compressed dated backups of a directory and enforces a retention limit on stored archives.

#### Purpose
*   Automates backup rotation for a specific directory.
*   Maintains a fixed number of recent compressed archives.

#### Type
*   Bash shell script executable.

#### Input
*   `source_dir`: Directory path to backup.
*   `backup_dir`: Destination path for archives.
*   `keep`: Optional integer count of archives to retain (default 7).

#### Output
*   New `.tar.gz` archive file in `backup_dir`.
*   Deleted older archive files exceeding retention limit.
*   Console logs for creation and removal actions.

#### Core logic
*   Validates `keep` argument is a positive integer.
*   Verifies `source_dir` exists.
*   Creates `backup_dir` if missing.
*   Generates timestamped archive filename using `date -u`.
*   Writes `tar` archive to temporary file with `trap` cleanup.
*   Moves temporary file to final archive location.
*   Lists existing archives sorted by modification time (newest first).
*   Removes archives beyond the `keep` threshold.

#### Public API
*   `rotate_backups.sh(source_dir, backup_dir, keep) -> exit code`

#### Dependencies
*   `bash` shell.
*   `tar` (GNU tar implied by flags).
*   `date` (GNU date implied by format).
*   `ls`, `mv`, `rm`, `mkdir`, `echo`, `mapfile`.

#### Exceptions / Errors
*   Missing arguments trigger usage message and exit 2.
*   Invalid `keep` value triggers error and exit 2.
*   Missing `source_dir` triggers error and exit 2.
*   `set -euo pipefail` exits on command failure.
*   `trap` ensures temporary file removal on exit.
