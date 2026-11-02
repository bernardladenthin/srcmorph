### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:49:00Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Rotates directory backups by creating a timestamped tar.gz and pruning older archives.

#### Purpose
- Creates a compressed backup of a specified directory.
- Keeps only the most recent N backups; older ones are deleted.
- Intended to be run directly from the command line.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`).
- Strict mode: `set -euo pipefail`.

#### Input
- `$1` → `SOURCE_DIR` (required, source directory).
- `$2` → `BACKUP_DIR` (required, destination for archives).
- `$3` → `KEEP` (optional, defaults to `7`).
- Validates `KEEP` is a positive integer.
- Checks `SOURCE_DIR` exists; creates `BACKUP_DIR` if missing.

#### Output
- Creates `archive` file: `BACKUP_DIR/${name}_YYYYMMDDTHHMMSSZ.tar.gz`.
- Prints creation message with size.
- Deletes older archives beyond the `KEEP` count, printing each removal.
- Exit status: `0` on success, `2` on input errors.

#### Core logic
- Resolve `name` from `SOURCE_DIR`.
- Build `archive` and temporary `tmp` paths.
- Set `trap` to remove `tmp` on exit.
- Run `tar -C "$(dirname "$SOURCE_DIR")" -czf "$tmp" "$name"`.
- Move `tmp` to final `archive`.
- List existing archives sorted newest first.
- Loop over entries beyond `KEEP` and delete them.

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
- Utilities: `basename`, `dirname`.

#### Errors and exit codes
- `exit 2` if `KEEP` not a positive integer.
- `exit 2` if `SOURCE_DIR` does not exist.
- `trap` ensures temporary file is removed on any exit.
