### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T09:59:16Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a timestamped tar.gz backup of a source directory and prunes old archives to retain only the newest N copies.

#### Purpose
- Generates a compressed, dated backup archive of a single directory.
- Enforces a retention policy by deleting the oldest archives beyond a configurable count.

#### Type
- Bash script (executable shell script)

#### Input
- `SOURCE_DIR`: Path to the directory to back up (required, must exist).
- `BACKUP_DIR`: Destination directory for archives (required, created if missing).
- `KEEP`: Maximum number of archives to retain (optional, default `7`, must be a positive integer).

#### Output
- A new `.tar.gz` archive in `BACKUP_DIR` named `<basename>_<UTC-timestamp>.tar.gz`.
- Deletion of excess old archives from `BACKUP_DIR`.
- Stdout messages for created and removed archive paths.
- Stderr messages and non-zero exit codes on validation failure.

#### Core logic
- Validates `KEEP` as a positive integer and `SOURCE_DIR` existence; exits with code 2 on failure.
- Creates `BACKUP_DIR` if it does not exist.
- Constructs archive filename using `basename` of source and UTC timestamp (`%Y%m%dT%H%M%SZ`).
- Writes archive to a `.partial` temp file first; uses `trap` to clean up on exit.
- Runs `tar -czf` from the parent directory to archive the source directory.
- Atomically renames `.partial` to final archive name.
- Lists existing archives for this source sorted newest-first (`ls -1t`).
- Iterates over archives beyond index `KEEP` and deletes them.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` -> exit 0 on success; exit 2 on invalid input.

#### Dependencies
- `tar`, `gzip` (via `tar -czf`)
- `date` (UTC timestamp)
- `basename`, `dirname`
- `du`, `cut` (size reporting)
- `ls`, `mapfile` (Bash 4+ built-in)
- `mkdir`, `mv`, `rm`

#### Exceptions / Errors
- Missing `SOURCE_DIR` or `BACKUP_DIR` argument: usage message to stderr, exit 2.
- `KEEP` not a positive integer: error to stderr, exit 2.
- `SOURCE_DIR` does not exist: error to stderr, exit 2.
- `set -euo pipefail`: any unhandled command failure aborts the script.
- `trap` on EXIT removes the `.partial` temp file if the script is interrupted before `mv`.
