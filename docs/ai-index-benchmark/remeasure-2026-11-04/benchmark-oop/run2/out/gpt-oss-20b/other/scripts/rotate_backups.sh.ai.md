### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:41:26Z
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
- Validates `KEEP` is a positive integer.
- Checks `SOURCE_DIR` exists; creates `BACKUP_DIR` if missing.

#### Output
- Writes a new archive file `"$BACKUP_DIR/${name}_<timestamp>.tar.gz"`.
- Prints creation and size of the archive.
- Removes older archives beyond `KEEP`; prints each removal.
- Exit status `0` on success, `2` on invalid arguments or missing directory.

#### Core logic
- Validate inputs and create backup directory.
- Build archive name with UTC timestamp.
- Create temporary partial archive `"$tmp"`.
- `tar -C "$(dirname "$SOURCE_DIR")" -czf "$tmp" "$name"` to compress.
- Move temporary to final archive.
- List existing archives sorted newest first.
- Delete all but the first `KEEP` entries.

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
- Uses `mapfile` (bash built‑in) to read archive list.

#### Errors and exit codes
- `exit 2` if `KEEP` not a positive integer or `SOURCE_DIR` missing.
- `trap` ensures temporary file `$tmp` is removed on exit.
- `set -euo pipefail` causes immediate exit on any command failure.
