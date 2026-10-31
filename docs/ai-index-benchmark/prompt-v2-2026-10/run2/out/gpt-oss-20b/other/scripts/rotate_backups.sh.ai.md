### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:54:26Z
- G: 
- A: 0.0.0
- X: file
---
> Creates dated, compressed backups of a directory and retains only the most recent N archives.

#### Purpose
- Automates backup creation for a specified directory.
- Keeps a limited number of recent backups to manage disk space.

#### Type
- Bash script (`rotate_backups.sh`).

#### Input
- `SOURCE_DIR`: directory to back up.
- `BACKUP_DIR`: destination for backup archives.
- Optional `KEEP`: number of recent archives to retain (default 7).

#### Output
- New `.tar.gz` archive in `BACKUP_DIR` named `<source>_YYYYMMDDTHHMMSSZ.tar.gz`.
- Deletes older archives beyond the `KEEP` count.
- Prints creation and removal messages to stdout.

#### Core logic
- Validate `KEEP` is a positive integer.
- Ensure `SOURCE_DIR` exists; create `BACKUP_DIR` if missing.
- Build archive name with UTC timestamp.
- Create temporary partial archive, then rename to final.
- List existing archives sorted newest first.
- Remove archives beyond the `KEEP` threshold.

#### Public API
- Script entry point: `rotate_backups.sh <source_dir> <backup_dir> [keep]` – performs backup rotation.

#### Constants and configuration
- Default `KEEP` value: `7`.

#### Dependencies
- Bash built‑ins: `set`, `trap`, `echo`, `exit`, `mkdir`, `basename`, `date`, `tar`, `du`, `cut`, `ls`, `mapfile`.
- External commands: `tar`, `du`, `cut`, `ls`.

#### Exceptions / Errors
- Exits with code 2 if `KEEP` is invalid or `SOURCE_DIR` does not exist.
- Prints error messages to stderr for invalid arguments or missing directory.
