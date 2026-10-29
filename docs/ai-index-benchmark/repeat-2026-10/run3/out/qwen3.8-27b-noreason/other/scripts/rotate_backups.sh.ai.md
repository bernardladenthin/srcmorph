### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:49:33Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a timestamped tar.gz backup of a directory and prunes old archives to retain only the newest N copies.

#### Purpose
- Generates a compressed, dated backup archive of a specified source directory.
- Enforces a retention policy by deleting the oldest backup archives beyond a configurable limit.

#### Type
- Bash script (executable)

#### Input
- `SOURCE_DIR`: Path to the directory to back up (required, must exist).
- `BACKUP_DIR`: Destination directory for archives (required, created if missing).
- `KEEP`: Maximum number of archives to retain (optional, default 7, must be positive integer).

#### Output
- `archive`: New `.tar.gz` file in `BACKUP_DIR` named `<basename>_<UTC-timestamp>.tar.gz`.
- `stdout`: Confirmation message with archive path and size; removal messages for pruned files.
- `stderr`: Error messages for invalid input or missing source directory.
- Side effects: Deletes old `.tar.gz` files in `BACKUP_DIR` matching the source basename pattern.

#### Core logic
- Validates `KEEP` as a positive integer and `SOURCE_DIR` existence; exits with code 2 on failure.
- Creates `BACKUP_DIR` if it does not exist.
- Constructs archive filename using `basename` of source and UTC timestamp (`%Y%m%dT%H%M%SZ`).
- Writes to a `.partial` temp file first; sets `trap` to remove temp file on exit.
- Runs `tar -czf` from the parent directory of `SOURCE_DIR` to archive the directory by basename.
- Atomically renames `.partial` to final archive name.
- Lists existing archives for this source sorted newest-first (`ls -1t`).
- Iterates over archives beyond index `KEEP` and removes them.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` -> exit 0 on success, exit 2 on validation error.

#### Dependencies
- `tar`, `date`, `du`, `cut`, `basename`, `dirname`, `ls`, `mapfile`, `mv`, `rm`, `mkdir` (standard Unix utilities).

#### Exceptions / Errors
- Exits 2 if `KEEP` is not a positive integer.
- Exits 2 if `SOURCE_DIR` does not exist or is not a directory.
- `set -euo pipefail` causes immediate exit on any unhandled command failure.
- `trap` ensures `.partial` temp file is cleaned up on any exit.
