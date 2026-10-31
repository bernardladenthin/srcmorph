### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:41:06Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a compressed, dated backup of a directory and retains only the most recent N archives.

#### Purpose
- Generates a timestamped `.tar.gz` backup of a specified source directory.
- Keeps a configurable number of recent backups, deleting older ones.

#### Type
- Bash script (`rotate_backups.sh`).

#### Input
- `SOURCE_DIR`: path to directory to back up.
- `BACKUP_DIR`: path where backup archives are stored.
- Optional `KEEP`: number of recent archives to retain (default 7).

#### Output
- New archive file in `BACKUP_DIR` named `<basename>_YYYYMMDDTHHMMSSZ.tar.gz`.
- Console messages: creation size, removal notices.
- Deletes older archives beyond the `KEEP` count.

#### Core logic
- Validate `KEEP` is a positive integer.
- Ensure `SOURCE_DIR` exists; create `BACKUP_DIR` if missing.
- Build archive name with UTC timestamp.
- Create temporary partial archive, then rename to final name.
- List existing archives sorted newest first.
- Remove archives beyond the first `KEEP` entries.

#### Public API
- Script entry point: `rotate_backups.sh <source_dir> <backup_dir> [keep]` – performs backup rotation.

#### Constants and configuration
- Default `KEEP` value: `7`.

#### Dependencies
- Bash built‑ins: `set`, `trap`, `echo`, `mkdir`, `basename`, `dirname`, `tar`, `date`, `du`, `cut`, `ls`, `mapfile`, `rm`, `mv`.
- External commands: `tar`, `date`, `du`, `cut`, `ls`.

#### Exceptions / Errors
- Exits with status `2` if:
  - `KEEP` is not a positive integer.
  - `SOURCE_DIR` does not exist.
  - Any command fails (due to `set -e`).
- Error messages printed to `stderr`.
