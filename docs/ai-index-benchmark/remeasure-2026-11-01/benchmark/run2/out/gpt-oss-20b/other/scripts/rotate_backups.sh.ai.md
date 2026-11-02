### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:47:57Z
- G: 
- A: 0.0.0
- X: file
---
> Creates dated, compressed backups of a directory and retains only the newest N archives.

#### Purpose
- Rotates backups of a specified directory.
- Runs directly as a command-line utility.

#### Type
- `bash`
- `set -euo pipefail`

#### Input
- `$1` → `SOURCE_DIR` (required)
- `$2` → `BACKUP_DIR` (required)
- `$3` → `KEEP` (optional, defaults to `7`)

#### Output
- Creates `BACKUP_DIR/<basename>_YYYYMMDDTHHMMSSZ.tar.gz`.
- Prints creation and removal messages.
- Exit status `0` on success, `2` on invalid arguments or missing directory.

#### Core logic
- Validate `KEEP` is a positive integer.
- Ensure `SOURCE_DIR` exists; create `BACKUP_DIR`.
- Build archive name with UTC timestamp.
- Create temporary `.partial` archive with `tar -C ... -czf`.
- Move temporary to final archive; report size.
- List existing archives sorted newest first.
- Delete all but the first `KEEP` archives; report each removal.

#### Functions
- None.

#### Constants and configuration
- None.

#### Dependencies
- `tar`
- `date`
- `du`
- `ls`
- `rm`
- `mv`
- `echo`

#### Errors and exit codes
- `exit 2` if `KEEP` not a positive integer or `SOURCE_DIR` missing.
- `trap` removes temporary file on exit.
