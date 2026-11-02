### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:20:49Z
- G: 
- A: 0.0.0
- X: file
---
> Creates dated, compressed backups of a directory and retains only the most recent N archives.

#### Purpose
- Automates backup creation for a specified directory.
- Maintains a limited number of recent backups.

#### Type
- Bash script.

#### Input
- `SOURCE_DIR`: directory to back up (argument 1).
- `BACKUP_DIR`: destination for archives (argument 2).
- `KEEP`: optional number of archives to keep (argument 3, default 7).

#### Output
- Compressed tarball (`.tar.gz`) in `BACKUP_DIR` named `<basename>_YYYYMMDDTHHMMSSZ.tar.gz`.
- Console messages: creation size, removal notices.
- Deletes older archives beyond `KEEP`.

#### Core logic
- Validate `KEEP` is a positive integer.
- Ensure `SOURCE_DIR` exists; create `BACKUP_DIR`.
- Build archive name with UTC timestamp.
- Create temporary partial archive, then rename to final.
- List existing archives sorted newest first.
- Remove all but the first `KEEP` entries.

#### Public API
- Script entry point: `rotate_backups.sh <source_dir> <backup_dir> [keep]` – initiates backup rotation.

#### Constants and configuration
- Default `KEEP` value: `7`.
- Timestamp format: `+%Y%m%dT%H%M%SZ`.

#### Dependencies
- Bash built‑ins: `set`, `[[`, `echo`, `mkdir`, `basename`, `tar`, `date`, `du`, `cut`, `ls`, `mapfile`, `trap`, `rm`, `mv`.
- External utilities: `tar`, `date`, `du`, `cut`, `ls`.

#### Exceptions / Errors
- Exits with status 2 if:
  - Missing required arguments.
  - `KEEP` not a positive integer.
  - `SOURCE_DIR` does not exist.
- Prints error messages to `stderr`.
