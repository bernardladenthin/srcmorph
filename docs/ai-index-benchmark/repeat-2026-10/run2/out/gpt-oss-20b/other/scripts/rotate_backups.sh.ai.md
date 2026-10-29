### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:15:29Z
- G: 
- A: 0.0.0
- X: file
---
> Creates dated, compressed backups of a directory and retains only the newest N archives.

#### Purpose
- Automates backup creation and rotation.
- Keeps storage limited to a configurable number of recent archives.

#### Type
- Bash script (`rotate_backups.sh`).

#### Input
- `SOURCE_DIR`: directory to back up (argument 1).
- `BACKUP_DIR`: destination for archives (argument 2).
- `KEEP`: optional positive integer, default 7 (argument 3).

#### Output
- New `.tar.gz` archive in `BACKUP_DIR`.
- Deletion of older archives beyond `KEEP`.
- Console messages: creation size, removal notices.

#### Core logic
- Validate `KEEP` is a positive integer.
- Ensure `SOURCE_DIR` exists; create `BACKUP_DIR` if missing.
- Build archive name: `<basename>_YYYYMMDDTHHMMSSZ.tar.gz`.
- Create temporary partial archive, then rename to final.
- List existing archives sorted newest first.
- Remove archives beyond the first `KEEP` entries.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` → creates and rotates backups.

#### Dependencies
- `tar`, `date`, `du`, `ls`, `basename`, `dirname`, `mkdir`, `trap`.

#### Exceptions / Errors
- Exits with code 2 on invalid `KEEP`, missing source directory, or other failures.
