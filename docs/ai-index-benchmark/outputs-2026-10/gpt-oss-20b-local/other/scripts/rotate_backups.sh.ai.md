### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:19:19Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a compressed, dated backup of a directory and retains only the most recent N archives.

#### Purpose
- Automates backup creation for a specified directory.
- Keeps a limited number of recent archives to manage disk space.

#### Type
- Bash script (`rotate_backups.sh`).

#### Input
- `$1`: source directory to back up.
- `$2`: destination backup directory.
- `$3` (optional): number of archives to keep (default 7).

#### Output
- New `.tar.gz` archive in `$BACKUP_DIR`.
- Console messages: creation size, removal notices.
- Deletes older archives beyond the keep count.

#### Core logic
- Validate `KEEP` is a positive integer.
- Ensure source directory exists; create backup directory if missing.
- Build archive name: `<basename>_YYYYMMDDTHHMMSSZ.tar.gz`.
- Create temporary archive with `tar -czf` to `$tmp`.
- Move temporary file to final archive name.
- List existing archives sorted newest first.
- Remove all but the first `KEEP` archives, echoing each deletion.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` → creates backup, manages retention.

#### Dependencies
- `tar`, `date`, `du`, `basename`, `ls`, `mapfile`.

#### Exceptions / Errors
- Exits with status 2 if `KEEP` invalid or source directory missing.
- Uses `set -euo pipefail` to abort on errors.
