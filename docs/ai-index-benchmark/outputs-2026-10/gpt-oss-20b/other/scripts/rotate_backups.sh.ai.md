### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:52:07Z
- G: 
- A: 0.0.0
- X: file
---
> Creates dated, compressed backups of a directory and retains only the most recent N archives.

#### Purpose
- Automates backup creation for a specified directory.
- Keeps backup history limited to a configurable number of recent archives.

#### Type
- Bash script (`rotate_backups.sh`).

#### Input
- `$1`: source directory to back up.
- `$2`: destination backup directory.
- `$3` (optional): number of recent archives to keep (default 7).

#### Output
- New `.tar.gz` archive in `$BACKUP_DIR` named `<source>_YYYYMMDDTHHMMSSZ.tar.gz`.
- Deletes older archives beyond the keep count.
- Prints creation and removal messages to stdout.

#### Core logic
- Validate arguments and `KEEP` as positive integer.
- Ensure source exists; create backup directory if missing.
- Build archive name with UTC timestamp.
- Create temporary partial archive, then rename to final name.
- List existing archives sorted newest first.
- Remove archives beyond the first `KEEP` entries.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` → creates backup, cleans old ones.

#### Dependencies
- `tar`, `date`, `du`, `ls`, `basename`, `dirname`, `mkdir`, `rm`, `echo`.

#### Exceptions / Errors
- Exits with status 2 on missing arguments, non‑numeric `keep`, or missing source directory.
