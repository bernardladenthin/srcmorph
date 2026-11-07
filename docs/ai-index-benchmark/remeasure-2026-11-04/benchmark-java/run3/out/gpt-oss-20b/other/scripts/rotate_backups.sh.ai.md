### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:49:18Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Creates a dated, compressed backup of a directory and prunes older archives, keeping only the newest N.

#### Purpose
- Generates a timestamped `.tar.gz` backup of `SOURCE_DIR` into `BACKUP_DIR`.
- Deletes older backups beyond the specified `KEEP` count.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`).
- Strict mode: `set -euo pipefail`.

#### Input
- `$1` → `SOURCE_DIR` (required).
- `$2` → `BACKUP_DIR` (required).
- `$3` → `KEEP` (optional, defaults to `7`).
- Validates `KEEP` as a positive integer.
- Checks `SOURCE_DIR` exists; creates `BACKUP_DIR` if missing.

#### Output
- Writes a new archive file `BACKUP_DIR/<basename>_YYYYMMDDTHHMMSSZ.tar.gz`.
- Prints creation message with size.
- Removes older archives beyond `KEEP`; prints each removal.
- Exit status `0` on success, `2` on invalid arguments or missing directory.

#### Core logic
- Validate inputs and `KEEP`.
- Ensure `BACKUP_DIR` exists.
- Build `archive` path with UTC timestamp.
- Create temporary archive `tmp`; trap to delete on exit.
- `tar -C "$(dirname "$SOURCE_DIR")" -czf "$tmp" "$name"`.
- Move `tmp` to final `archive`.
- List existing archives sorted newest first.
- Loop over archives beyond `KEEP` and delete them.

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
- External commands: `tar`, `date`, `du`, `ls`, `rm`, `mv`.
- Uses `ls -1t` for sorting by modification time.

#### Errors and exit codes
- `exit 2` if `KEEP` not a positive integer or `SOURCE_DIR` missing.
- `trap` ensures temporary file `tmp` is removed on exit.
- No explicit retries or timeouts.
