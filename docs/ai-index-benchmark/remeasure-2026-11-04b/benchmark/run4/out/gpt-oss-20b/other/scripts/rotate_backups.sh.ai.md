### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:51:58Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Creates a dated, compressed backup of a directory and prunes older archives to keep only the newest N.

#### Purpose
- Backup a single directory.
- Keep only the most recent *N* archives.

#### Type
- `bash`
- `set -euo pipefail`

#### Input
- `$1` → `SOURCE_DIR` (required)
- `$2` → `BACKUP_DIR` (required)
- `$3` → `KEEP` (optional, defaults to `7`)

#### Output
- Creates `"$BACKUP_DIR/${name}_<timestamp>.tar.gz"`.
- Deletes older archives beyond the newest `KEEP`.
- Prints creation and removal messages.
- Exits with status `0` on success, `2` on argument or validation errors.

#### Core logic
- Validate `KEEP` is a positive integer.
- Ensure `SOURCE_DIR` exists; create `BACKUP_DIR`.
- Build archive name with UTC timestamp.
- Create temporary `.partial` tarball, then rename to final archive.
- List existing archives sorted newest first.
- Remove archives beyond the first `KEEP`.

#### Functions
- None.

#### Constants and configuration
- `SOURCE_DIR`=`"${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR`=`"${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP`=`"${3:-7}"`
- `name`=`"$(basename "$SOURCE_DIR")"`
- `archive`=`"$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp`=`"$archive.partial"`

#### Dependencies
- `tar` (creates compressed archive)
- `du` (reports archive size)
- `ls` (lists existing archives)
- `date` (generates timestamp)

#### Errors and exit codes
- `2` – missing arguments, invalid `KEEP`, or non‑existent `SOURCE_DIR`.
- `0` – successful backup and cleanup.
