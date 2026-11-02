### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:55:15Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated, compressed backup of a directory and retains only the most recent N archives.

#### Purpose
- Generates a timestamped `.tar.gz` backup of a specified source directory.
- Keeps only the newest N backups, deleting older ones.

#### Type
- Bash script (`rotate_backups.sh`).

#### Input
- `$1`: `SOURCE_DIR` – directory to back up.
- `$2`: `BACKUP_DIR` – destination for archives.
- `$3` (optional): `KEEP` – number of recent archives to retain (default 7).

#### Output
- New archive file in `BACKUP_DIR` named `<basename>_YYYYMMDDTHHMMSSZ.tar.gz`.
- Console messages: creation size, removal notices.
- Side effect: deletes older archives beyond `KEEP`.

#### Core logic
- Validate `KEEP` is a positive integer.
- Ensure `SOURCE_DIR` exists; create `BACKUP_DIR` if missing.
- Build archive path with UTC timestamp.
- Create temporary archive (`.partial`), then rename to final.
- List existing archives sorted newest first.
- Remove archives beyond the first `KEEP` entries.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` – main entry point; performs backup rotation.

#### Constants and configuration
- `KEEP` default value: `7`.
- `trap 'rm -f "$tmp"' EXIT` – cleans temporary file on exit.

#### Dependencies
- Bash built-ins: `set`, `echo`, `exit`, `trap`, `mkdir`, `basename`, `dirname`, `date`, `tar`, `du`, `cut`, `ls`, `mapfile`, `rm`, `mv`.
- External commands: `tar`, `du`, `date`, `ls`.

#### Exceptions / Errors
- Exits with code `2` if:
  - `KEEP` is not a positive integer.
  - `SOURCE_DIR` does not exist.
  - Any command fails due to `set -euo pipefail`.
