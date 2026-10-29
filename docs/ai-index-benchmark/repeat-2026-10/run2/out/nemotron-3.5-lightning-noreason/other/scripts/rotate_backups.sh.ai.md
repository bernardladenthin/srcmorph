### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:29Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a compressed, dated backup of one directory and retains only the newest N archives.

#### Purpose
- Creates a timestamped, compressed archive of a source directory.
- Retains only the newest N archives in the backup directory, deleting older ones.

#### Type
Shell script (bash). Uses `set -euo pipefail`. No class or interface.

#### Input
- `SOURCE_DIR` (required): path to the directory to back up.
- `BACKUP_DIR` (required): path where archives are stored.
- `KEEP` (optional, default 7): positive integer specifying how many newest archives to retain.

#### Output
- Writes a new `.tar.gz` archive to `BACKUP_DIR` named `<basename>_YYYYMMDDTHHMMSSZ.tar.gz`.
- Deletes oldest archives when the total count exceeds `KEEP`.
- Prints creation and removal messages to stdout.

#### Core logic
- Validates that `KEEP` is a positive integer and that `SOURCE_DIR` exists.
- Creates the backup archive using `tar`, writing to a temporary file then moving it atomically.
- Lists archives in the backup directory sorted by modification time (newest first).
- Deletes any archives beyond the `KEEP` limit.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep=7]` – creates and rotates backups.

#### Dependencies
- bash
- `tar`
- `date`
- `du`
- `ls`
- `mv`
- `rm`

#### Exceptions / Errors
- Exits with code 2 and prints message if `KEEP` is not a positive integer.
- Exits with code 2 and prints message if `SOURCE_DIR` does not exist.
