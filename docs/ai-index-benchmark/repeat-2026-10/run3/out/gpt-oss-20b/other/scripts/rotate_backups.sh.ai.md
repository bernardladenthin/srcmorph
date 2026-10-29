### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:54:59Z
- G: 
- A: 0.0.0
- X: file
---
> Creates dated, compressed backups of a directory and retains only the most recent N archives.

#### Purpose
- Automates backup creation for a specified source directory.
- Keeps a limited number of recent backups, deleting older ones.

#### Type
- Bash script (`rotate_backups.sh`).

#### Input
- `SOURCE_DIR`: directory to back up (mandatory).
- `BACKUP_DIR`: destination for archives (mandatory).
- `KEEP`: optional positive integer, default `7`, number of archives to retain.

#### Output
- New `.tar.gz` archive in `BACKUP_DIR` named `<source>_YYYYMMDDTHHMMSSZ.tar.gz`.
- Console messages: creation size, removal notices.
- Deletes older archives beyond the `KEEP` count.

#### Core logic
- Validate `KEEP` is a positive integer.
- Ensure `SOURCE_DIR` exists; create `BACKUP_DIR` if missing.
- Build archive name with UTC timestamp.
- Create temporary partial archive, then rename to final name.
- List existing archives sorted newest first.
- Remove all but the first `KEEP` entries, echoing each deletion.

#### Public API
- Script executable: `rotate_backups.sh <source_dir> <backup_dir> [keep]` → creates backup, manages retention.

#### Dependencies
- `tar`, `date`, `du`, `ls`, `basename`, `dirname`, `mkdir`, `rm`, `echo`.

#### Exceptions / Errors
- Exits with code `2` if `KEEP` invalid or `SOURCE_DIR` missing.
- Uses `set -euo pipefail` for strict error handling.
